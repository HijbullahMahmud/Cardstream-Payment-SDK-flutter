package com.tenfingers.cardstreamsdk.card_stream_payment_sdk;

public interface PaymentCallback {
    void onPaymentSuccess(int statusCode);
    void onPaymentError(String errorMessage);
}
