package com.tenfingers.cardstreamsdk.card_stream_payment_sdk;

import android.app.Activity;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** CardStreamPaymentSdkPlugin */
public class CardStreamPaymentSdkPlugin implements FlutterPlugin, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Activity context;
  private BinaryMessenger binaryMessenger;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    binaryMessenger = binding.getBinaryMessenger();
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    this.context = null;
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    context = (FlutterActivity) binding.getActivity();
    channel = new MethodChannel(binaryMessenger, "payment");
    MethodChannel.MethodCallHandler methodCallHandler = new MethodChannelHandler(context, channel);
    channel.setMethodCallHandler(methodCallHandler);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }
}