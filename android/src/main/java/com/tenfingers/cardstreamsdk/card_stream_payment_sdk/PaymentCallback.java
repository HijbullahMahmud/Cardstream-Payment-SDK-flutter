package com.tenfingers.cardstreamsdk.card_stream_payment_sdk;

import java.util.Map;

public interface PaymentCallback {
    void onPaymentSuccess(Map<String, String> response);
    void onPaymentError(String errorMessage);
}
