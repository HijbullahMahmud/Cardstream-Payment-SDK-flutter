
import 'card_stream_payment_sdk_platform_interface.dart';

class CardStreamPaymentSdk {
  Future<String?> getPlatformVersion() {
    return CardStreamPaymentSdkPlatform.instance.getPlatformVersion();
  }
}
