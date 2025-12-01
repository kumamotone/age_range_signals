/// Base exception for age signals operations.
class AgeSignalsException implements Exception {
  /// Creates an [AgeSignalsException].
  const AgeSignalsException(this.message, [this.code]);

  /// A description of the error.
  final String message;

  /// An optional error code.
  final String? code;

  @override
  String toString() {
    if (code != null) {
      return 'AgeSignalsException($code): $message';
    }
    return 'AgeSignalsException: $message';
  }
}

/// Exception thrown when the API is not available on the platform.
class ApiNotAvailableException extends AgeSignalsException {
  /// Creates an [ApiNotAvailableException].
  const ApiNotAvailableException(super.message, [super.code]);
}

/// Exception thrown when the platform version is incompatible.
class UnsupportedPlatformException extends AgeSignalsException {
  /// Creates an [UnsupportedPlatformException].
  const UnsupportedPlatformException(super.message, [super.code]);
}

/// Exception thrown when the plugin has not been initialized.
class NotInitializedException extends AgeSignalsException {
  /// Creates a [NotInitializedException].
  const NotInitializedException(super.message, [super.code]);
}
