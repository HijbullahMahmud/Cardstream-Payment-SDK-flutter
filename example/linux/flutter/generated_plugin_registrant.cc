//
//  Generated file. Do not edit.
//

// clang-format off

#include "generated_plugin_registrant.h"

#include <card_stream_payment_sdk/card_stream_payment_sdk_plugin.h>

void fl_register_plugins(FlPluginRegistry* registry) {
  g_autoptr(FlPluginRegistrar) card_stream_payment_sdk_registrar =
      fl_plugin_registry_get_registrar_for_plugin(registry, "CardStreamPaymentSdkPlugin");
  card_stream_payment_sdk_plugin_register_with_registrar(card_stream_payment_sdk_registrar);
}
