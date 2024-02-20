#ifndef FLUTTER_PLUGIN_CARD_STREAM_PAYMENT_SDK_PLUGIN_H_
#define FLUTTER_PLUGIN_CARD_STREAM_PAYMENT_SDK_PLUGIN_H_

#include <flutter/method_channel.h>
#include <flutter/plugin_registrar_windows.h>

#include <memory>

namespace card_stream_payment_sdk {

class CardStreamPaymentSdkPlugin : public flutter::Plugin {
 public:
  static void RegisterWithRegistrar(flutter::PluginRegistrarWindows *registrar);

  CardStreamPaymentSdkPlugin();

  virtual ~CardStreamPaymentSdkPlugin();

  // Disallow copy and assign.
  CardStreamPaymentSdkPlugin(const CardStreamPaymentSdkPlugin&) = delete;
  CardStreamPaymentSdkPlugin& operator=(const CardStreamPaymentSdkPlugin&) = delete;

  // Called when a method is called on this plugin's channel from Dart.
  void HandleMethodCall(
      const flutter::MethodCall<flutter::EncodableValue> &method_call,
      std::unique_ptr<flutter::MethodResult<flutter::EncodableValue>> result);
};

}  // namespace card_stream_payment_sdk

#endif  // FLUTTER_PLUGIN_CARD_STREAM_PAYMENT_SDK_PLUGIN_H_
