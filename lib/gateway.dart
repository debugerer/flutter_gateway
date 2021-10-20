import 'dart:async';

import 'package:flutter/services.dart';

class Gateway {
  static const MethodChannel _channel = const MethodChannel('gateway');

  final String? localIP;
  final String? ip;
  final String? netmask;
  final String? broadcast;

  Gateway({
    this.localIP = "0.0.0.0",
    this.ip = "0.0.0.0",
    this.netmask = "0.0.0.0",
    this.broadcast = "0.0.0.0",
  });

  Gateway.fromMap(Map<String, String> map)
      : ip = map["ip"],
        localIP = map["localIP"],
        netmask = map["netmask"],
        broadcast = map["broadcast"];

  @override
  String toString() {
    return 'ip:$ip\n localIP:$localIP\n netmask:$netmask\n broadcast:$broadcast';
  }

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<Gateway> get info async {
    return Gateway.fromMap(
        Map<String, String>.from(await _channel.invokeMethod('getInfo')));
  }
}
