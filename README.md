# gateway

A flutter plugin for get gateway information when wifi connect.
Work on iOS and Android.

For Android:
Need add:
```xml
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```
Usage:
```dart
import 'package:gateway/gateway.dart';

Gateway gt = await Gateway.info;
print(gt);
```
## Getting Started

This project is a starting point for a Flutter
[plug-in package](https://flutter.dev/developing-packages/),
a specialized package that includes platform-specific implementation code for
Android and/or iOS.

For help getting started with Flutter, view our 
[online documentation](https://flutter.dev/docs), which offers tutorials, 
samples, guidance on mobile development, and a full API reference.
