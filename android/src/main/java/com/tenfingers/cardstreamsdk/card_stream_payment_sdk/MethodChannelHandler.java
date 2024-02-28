package com.tenfingers.cardstreamsdk.card_stream_payment_sdk;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;



public class MethodChannelHandler extends FlutterActivity implements MethodChannel.MethodCallHandler {
    private final String TAG = MethodChannelHandler.class.getSimpleName();
    private Activity context;
    private MethodChannel channel;

    public MethodChannelHandler(Activity context, MethodChannel channel) {
        this.context = context;
        this.channel = channel;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        if (call.method.equals("pay")) {
            String directURL = call.argument("directURL");
            String merchantID = call.argument("merchantID");
            String merchantSecret = call.argument("merchantSecret");
            //make payment
            String amount = call.argument("amount");
            String cardNumber = call.argument("cardNumber");
            String cardExpiryDate = call.argument("cardExpiryDate");
            String cardCVV = call.argument("cardCVV");
            String customerAddress = call.argument("customerAddress");
            String customerPostCode = call.argument("customerPostCode");
            makePayment(directURL, merchantID, merchantSecret, amount, cardNumber, cardExpiryDate, cardCVV, customerAddress, customerPostCode, new PaymentCallback() {
                @Override
                public void onPaymentSuccess(Map<String, String> response) {
//                    Log.e(TAG, "onPaymentSuccess: "+ response );
                    result.success(response);
                }

                @Override
                public void onPaymentError(String errorMessage) {
                    Log.e(TAG, "onPaymentError: "+ errorMessage );
                    result.error("PAYMENT_ERROR", errorMessage, null);
                }
            });

        }

        else {
            result.notImplemented();
        }
    }

    private void makePayment(String directUrl, String merchantID, String merchantSecret, String amount, String cardNumber, String cardExpiryDate, String cardCVV, String customerAddress, String customerPostCode, PaymentCallback callback) {
        BigDecimal amountDecimal = new BigDecimal(amount);
        HashMap<String, String> request = new HashMap<>();
        request.put("action", "SALE");
        request.put("amount", amountDecimal.multiply(new BigDecimal("100")).toBigInteger().toString());
        request.put("cardNumber", cardNumber);
        request.put("cardExpiryDate", cardExpiryDate);
        request.put("cardCVV", cardCVV);
        request.put("customerAddress", customerAddress);
        request.put("customerPostCode", customerPostCode);
        request.put("countryCode", "826"); // GB
        request.put("currencyCode", "826"); //GB
        request.put("type", "1"); // e-commerce

        Gateway gateway = new Gateway(directUrl, merchantID, merchantSecret);

        new AsyncTask<Void, Void, Map<String, String>>() {
            @Override
            protected Map<String, String> doInBackground(Void... voids) {
                try {
                    Map<String, String> response = gateway.directRequest(request);
                    return response;
                } catch (IOException e) {
                    Log.e(TAG, "Gateway submit failed", e);
                    HashMap<String, String> error = new HashMap<>();
                    error.put("responseMessage", e.getMessage());
                    error.put("state", e.getClass().getName());
                    return error;
                }
            }

            @Override
            protected void onPostExecute(Map<String, String> response) {
//                Log.d(TAG, "RESPONSE" + response);
                channel.invokeMethod("show", response);
                Log.d(TAG, "Method invoked!");
                Log.d(TAG, "CHANNEL " + channel);
                //responseCode

                // Check if payment was successful or not and call the appropriate callback method
                if (response != null && response.containsKey("responseCode")) {
                    callback.onPaymentSuccess(response);
                } else {
                    callback.onPaymentError("Error processing payment");
                }
            }
        }.execute();
    }
}



