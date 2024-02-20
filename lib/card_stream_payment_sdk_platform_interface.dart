import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'card_stream_payment_sdk_method_channel.dart';

abstract class CardStreamPaymentSdkPlatform extends PlatformInterface {
  /// Constructs a CardStreamPaymentSdkPlatform.
  CardStreamPaymentSdkPlatform() : super(token: _token);

  static final Object _token = Object();

  static CardStreamPaymentSdkPlatform _instance = MethodChannelCardStreamPaymentSdk();

  /// The default instance of [CardStreamPaymentSdkPlatform] to use.
  ///
  /// Defaults to [MethodChannelCardStreamPaymentSdk].
  static CardStreamPaymentSdkPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [CardStreamPaymentSdkPlatform] when
  /// they register themselves.
  static set instance(CardStreamPaymentSdkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
