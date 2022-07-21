package com.example.gateway;

import static android.content.Context.WIFI_SERVICE;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * GatewayPlugin
 */
public class GatewayPlugin implements MethodCallHandler, FlutterPlugin {
    private static final String CHANNEL = "gateway";

    final String TAG = "GatewayPlugin";
    private Context context;
    private MethodChannel methodChannel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();
        methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL);
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        context = null;
        methodChannel = null;
    }

    @Override
    public void onMethodCall(MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("getInfo")) {
            result.success(getInfo());
        } else {
            result.notImplemented();
        }
    }

    public HashMap<String, String> getInfo() {
        HashMap<String, String> result;
        // initial capacity 4 and load factor one, so it only increases when it is full (which is never)
        result = new HashMap<>(4, 1.0f);

        result.put("ip", "0.0.0.0");
        result.put("localIP", "0.0.0.0");
        result.put("netmask", "0.0.0.0");
        result.put("broadcast", "0.0.0.0");

        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            result.put("localIP", intToIp(dhcpInfo.ipAddress));
            result.put("ip", intToIp(dhcpInfo.serverAddress));

            if (dhcpInfo.netmask == 0) {
                try {
                    NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(intToIp(dhcpInfo.ipAddress)));
                    for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                        if (address.getBroadcast() != null) {
                            result.put("netmask", prefixToSubnet(address.getNetworkPrefixLength()));
                            result.put("broadcast", address.getBroadcast().toString().substring(1));
                            break;
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            } else {
                result.put("netmask", intToIp(dhcpInfo.netmask));
                result.put("broadcast", intToIp((dhcpInfo.ipAddress & dhcpInfo.netmask) | ~dhcpInfo.netmask));
            }

            return result;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return result;
    }

    String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                ((i >> 24) & 0xFF);
    }

    String prefixToSubnet(int i) {
        return intToIp((int) (Math.pow(2, i) - 1));
    }
}
