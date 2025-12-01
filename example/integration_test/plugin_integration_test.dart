import 'dart:io';

import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

import 'package:age_range_signals/age_range_signals.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('initialize completes successfully', (WidgetTester tester) async {
    await expectLater(
      AgeRangeSignals.instance.initialize(ageGates: [13, 16, 18]),
      completes,
    );
  });

  testWidgets('checkAgeSignals returns a result or throws exception', (
    WidgetTester tester,
  ) async {
    if (Platform.isIOS) {
      await AgeRangeSignals.instance.initialize(ageGates: [13, 16, 18]);
    }

    try {
      final result = await AgeRangeSignals.instance.checkAgeSignals();
      expect(result, isA<AgeSignalsResult>());
      expect(result.status, isNotNull);
    } on AgeSignalsException catch (e) {
      // On platforms where the API isn't available, we expect an exception
      expect(e, isA<AgeSignalsException>());
    }
  });
}
