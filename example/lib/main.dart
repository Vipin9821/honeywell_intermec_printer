import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:honnywellintermecpr3/honnywellintermecpr3.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
//commande to send to printer
  var cmd = [
    // "setBold;true",
    // "setDoubleHigh;true",
    // "setDoubleWide;true",
    // "write;Test news",
    // "setBold;false",
    // "setDoubleHigh;false",
    // "setDoubleWide;false",
    // "newLine;2",
    // "newLine;2",
    // "write;ok",
    // "newLine;2",
    // "newLine;2",
    // "offset;80",
    "width;550",
    "heigh;500",
    // "image;iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAIAAAD/gAIDAAAB8klEQVR4nO2dS27DMAwF697/zumCmwL9WCNR5JPz5gIixxMgsI3ker1eH2aMz+4BTsKyAJYFsCyAZQEsC2BZAMsCWBZAVNZ1Xd0j/IKoLE0UZUVWgnEpypJFTtb3oNTikpOljJasnylJxaUlSxwhWX9FpBOXkCx9VGT9n49IXCqyjkBC1kg4CnFJyDqFflnjybTH1S/rIJpl0Vh643JZgE5Zc5k0xuWyAG2yVgLpistlAXpkrafREpfLAjTIyoqiPi6XBaiWlZtDcVwuC1Aqa0cIlXG5LECdrH0JlMXlsgBFsnZf/Jq4XBagQlbNZS84xWUBtsuq/B60+yyXBdgrq/7GwNYTXRZgo6yuO+X7znVZgF2yeh8dbzrdZQG2yGp/3WXTDC4LkC9LIasgfRKXBUiWpZNVkDuPywJkylLLKkicymUB0mRpZhVkzeayADmylLMKUiZ0WYAEWfpZBetzuizAqqxTsgoWp3VZgCVZZ2UVrMzssgDzsk7MKpie3GUBJmWdm1UwN7/LAszIOj2rYGILlwXAsp6RVUB3cVkAJutJWQVoI5cFALKel1UwvpfLAozKempWweB2LgswJOvZWQUjO7oswL2sd8gquN3UZQFuZL1PVsHNj1r676/G8ccQYFkAywJYFsCyAJYFsCyAZQG+ABQaB7S5B3LuAAAAAElFTkSuQmCC"
  ];

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: OutlinedButton(
            onPressed: () async {
              final string = await convertImageToBase64("assets/crp.jpeg");
              cmd.add("image;$string");

              Honnywellintermecpr3 n = Honnywellintermecpr3();
              n.printGeneral("PR3", "88:6B:0F:AF:11:DB", cmd);
            },
            child: Text('open'),
          ),
        ),
      ),
    );
  }
}

Future<String> convertImageToBase64(String assetPath) async {
  try {
    // Load the image file from the asset bundle
    final ByteData imageData = await rootBundle.load(assetPath);

    // Get the bytes from the ByteData
    final List<int> imageBytes = imageData.buffer.asUint8List();

    // Encode the bytes to Base64
    return base64Encode(imageBytes);
  } catch (e) {
    print('Error: $e');
    return '';
  }
}
