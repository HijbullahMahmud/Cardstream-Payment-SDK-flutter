import 'package:flutter_test/flutter_test.dart';
import 'package:card_stream_payment_sdk/card_stream_payment_sdk.dart';
import 'package:card_stream_payment_sdk/card_stream_payment_sdk_platform_interface.dart';
import 'package:card_stream_payment_sdk/card_stream_payment_sdk_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockCardStreamPaymentSdkPlatform
    with MockPlatformInterfaceMixin
    implements CardStreamPaymentSdkPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final CardStreamPaymentSdkPlatform initialPlatform = CardStreamPaymentSdkPlatform.instance;

  test('$MethodChannelCardStreamPaymentSdk is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelCardStreamPaymentSdk>());
  });

  test('getPlatformVersion', () async {
    CardStreamPaymentSdk cardStreamPaymentSdkPlugin = CardStreamPaymentSdk();
    MockCardStreamPaymentSdkPlatform fakePlatform = MockCardStreamPaymentSdkPlatform();
    CardStreamPaymentSdkPlatform.instance = fakePlatform;

    // expect(await cardStreamPaymentSdkPlugin.getPlatformVersion(), '42');
  });
}
