import 'dart:async';

import 'package:flutter/services.dart';

class CardStreamPaymentSdk {
  static const MethodChannel _channel = MethodChannel("payment");

  Future<dynamic> resultHandler(MethodCall methodCall) async {
    switch (methodCall.method) {
      case "show":
        print(" FROM AND: ${methodCall.arguments.toString()}");
        break;
      default:
        print("nothing");
        break;
    }
  }

  Future<int> makePayment(
      {required String directUrl,
      required String merchantID,
      required String merchantSecret,
      required String amount,
      required String cardNumber,
      required String cardExpiryDate,
      required String cardCVV,
      required String customerAddress,
      required String customerPostCode}) async {
  //  _channel.setMethodCallHandler(resultHandler);
    Completer<int> completer = Completer<int>();

    try {
      Map<String, String> arguments = {
        "directURL": directUrl,
        "merchantID": merchantID,
        "merchantSecret": merchantSecret,
        "amount": amount,
        "cardNumber": cardNumber,
        "cardExpiryDate": cardExpiryDate,
        "cardCVV": cardCVV,
        "customerAddress": customerAddress,
        "customerPostCode": customerPostCode,
      };

      _channel.invokeMethod("pay", arguments).then((value) {
        completer.complete(value);
      }).catchError((error) {
        completer.completeError(Exception("Error Payment"));
      });
    } catch (e) {
      completer.completeError(Exception("Error payment"));
    }
    return completer.future;
  }
}
