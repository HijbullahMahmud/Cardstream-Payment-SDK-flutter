import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'card_stream_payment_sdk_platform_interface.dart';

/// An implementation of [CardStreamPaymentSdkPlatform] that uses method channels.
class MethodChannelCardStreamPaymentSdk extends CardStreamPaymentSdkPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('card_stream_payment_sdk');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
