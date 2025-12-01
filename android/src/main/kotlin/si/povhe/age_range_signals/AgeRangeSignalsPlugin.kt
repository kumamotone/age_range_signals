package si.povhe.age_range_signals

import android.content.Context
import com.google.android.play.agesignals.AgeSignalsManager
import com.google.android.play.agesignals.AgeSignalsManagerFactory
import com.google.android.play.agesignals.AgeSignalsRequest
import com.google.android.play.agesignals.AgeSignalsResult
import com.google.android.play.agesignals.AgeSignalsException
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AgeRangeSignalsPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private var ageSignalsManager: AgeSignalsManager? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "age_range_signals")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext

        try {
            ageSignalsManager = AgeSignalsManagerFactory.create(context)
        } catch (e: Exception) {
            // Manager initialization can fail if Play Services isn't available
            ageSignalsManager = null
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "initialize" -> {
                // No-op on Android, age gates are iOS-specific
                result.success(null)
            }
            "checkAgeSignals" -> {
                checkAgeSignals(result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    private fun checkAgeSignals(result: Result) {
        val manager = ageSignalsManager
        if (manager == null) {
            result.error(
                "API_NOT_AVAILABLE",
                "Age Signals API is not available on this device",
                null
            )
            return
        }

        scope.launch {
            try {
                val ageSignalsResult = withContext(Dispatchers.IO) {
                    val request = AgeSignalsRequest.Builder().build()
                    manager.requestAgeSignals(request)
                }

                val status = when (ageSignalsResult.userStatus) {
                    AgeSignalsResult.USER_STATUS_UNDER_AGE -> "supervised"
                    AgeSignalsResult.USER_STATUS_OVER_AGE -> "verified"
                    AgeSignalsResult.USER_STATUS_UNKNOWN -> "unknown"
                    else -> "unknown"
                }

                val resultMap = mapOf(
                    "status" to status,
                    "installId" to ageSignalsResult.installId,
                    "ageLower" to null,
                    "ageUpper" to null,
                    "source" to null
                )

                withContext(Dispatchers.Main) {
                    result.success(resultMap)
                }
            } catch (e: AgeSignalsException) {
                withContext(Dispatchers.Main) {
                    when (e.errorCode) {
                        AgeSignalsException.ERROR_CODE_API_NOT_AVAILABLE -> {
                            result.error(
                                "API_NOT_AVAILABLE",
                                "Age Signals API is not available",
                                null
                            )
                        }
                        AgeSignalsException.ERROR_CODE_PLAY_STORE_VERSION_OUTDATED,
                        AgeSignalsException.ERROR_CODE_PLAY_SERVICES_VERSION_OUTDATED -> {
                            result.error(
                                "API_NOT_AVAILABLE",
                                "Play Store or Play Services needs to be updated",
                                null
                            )
                        }
                        AgeSignalsException.ERROR_CODE_NETWORK_ERROR -> {
                            result.error(
                                "NETWORK_ERROR",
                                "Network error occurred while fetching age signals",
                                null
                            )
                        }
                        else -> {
                            result.error(
                                "UNKNOWN_ERROR",
                                e.message ?: "An unknown error occurred",
                                null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    result.error(
                        "UNKNOWN_ERROR",
                        e.message ?: "An unexpected error occurred",
                        null
                    )
                }
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
