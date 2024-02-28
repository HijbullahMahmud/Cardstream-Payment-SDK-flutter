import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:card_stream_payment_sdk/card_stream_payment_sdk.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  Future<void> _payNow() async {
    CardStreamPaymentSdk sdk = CardStreamPaymentSdk();

    var result = await sdk.makePayment(
        directUrl: "https://gateway.cardstream.com/direct/",
        merchantID: "100001",
        merchantSecret: "Circle4Take40Idea",
        amount: "1",
        cardNumber: "4929421234600821",
        cardExpiryDate: "12/25",
        cardCVV: "356",
        customerAddress: "customerAddress",
        customerPostCode: "customerPostCode");

//0 = success response
    print("result: ---------------- $result");
    print("result:xref ---------------- ${result['xref']}");
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
            child: ElevatedButton(
                onPressed: () {
                  _payNow();
                },
                child: Text("Pay  Now"))),
      ),
    );
  }
}
