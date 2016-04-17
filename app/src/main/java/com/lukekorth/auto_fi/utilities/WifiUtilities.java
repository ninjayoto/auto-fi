package com.lukekorth.auto_fi.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;

import com.lukekorth.auto_fi.AutoFiApplication;

import java.util.List;

public class WifiUtilities {

    public static boolean isConnectedToWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Nullable
    public static WifiConfiguration getWifiNetwork(int networkId) {
        WifiManager wifiManager = (WifiManager) AutoFiApplication.getContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> wifiConfigurationList = wifiManager.getConfiguredNetworks();
        if (wifiConfigurationList != null) {
            for (WifiConfiguration configuration : wifiManager.getConfiguredNetworks()) {
                if (configuration.networkId == networkId) {
                    return configuration;
                }
            }
        }

        return null;
    }
}