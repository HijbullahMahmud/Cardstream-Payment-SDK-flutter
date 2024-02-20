import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:card_stream_payment_sdk/card_stream_payment_sdk_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelCardStreamPaymentSdk platform = MethodChannelCardStreamPaymentSdk();
  const MethodChannel channel = MethodChannel('card_stream_payment_sdk');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
