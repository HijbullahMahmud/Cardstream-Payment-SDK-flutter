#include "include/card_stream_payment_sdk/card_stream_payment_sdk_plugin_c_api.h"

#include <flutter/plugin_registrar_windows.h>

#include "card_stream_payment_sdk_plugin.h"

void CardStreamPaymentSdkPluginCApiRegisterWithRegistrar(
    FlutterDesktopPluginRegistrarRef registrar) {
  card_stream_payment_sdk::CardStreamPaymentSdkPlugin::RegisterWithRegistrar(
      flutter::PluginRegistrarManager::GetInstance()
          ->GetRegistrar<flutter::PluginRegistrarWindows>(registrar));
}
