package si.povhe.age_range_signals

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.mockito.Mockito
import kotlin.test.Test

internal class AgeRangeSignalsPluginTest {
    @Test
    fun initialize_returnsSuccess() {
        val plugin = AgeRangeSignalsPlugin()

        val call = MethodCall("initialize", null)
        val mockResult: MethodChannel.Result = Mockito.mock(MethodChannel.Result::class.java)
        plugin.onMethodCall(call, mockResult)

        Mockito.verify(mockResult).success(null)
    }
}
