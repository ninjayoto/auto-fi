package com.lukekorth.auto_fi.services;

import android.app.IntentService;
import android.content.Intent;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.lukekorth.auto_fi.models.WifiNetwork;
import com.lukekorth.auto_fi.utilities.ConnectivityUtils;
import com.lukekorth.auto_fi.utilities.VpnHelper;
import com.lukekorth.auto_fi.utilities.WifiHelper;

public class ConnectivityCheckIntentService extends IntentService {

    public static final String EXTRA_ATTEMPT_TO_BYPASS_CAPTIVE_PORTAL =
            "com.lukekorth.auto_fi.EXTRA_ATTEMPT_TO_BYPASS_CAPTIVE_PORTAL";

    public ConnectivityCheckIntentService() {
        super(ConnectivityCheckIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (ConnectivityUtils.checkConnectivity(this)) {
            case CONNECTED: {
                VpnHelper.startVpn(this);
                FirebaseAnalytics.getInstance(this).logEvent("connectivity_connected", null);
                break;
            } case REDIRECTED: {
                if (intent.getBooleanExtra(EXTRA_ATTEMPT_TO_BYPASS_CAPTIVE_PORTAL, true)) {
                    startService(new Intent(this, CaptivePortalBypassService.class));
                } else {
                    blacklistAndDisconnectFromNetwork();
                }
                FirebaseAnalytics.getInstance(this).logEvent("connectivity_redirected", null);
                break;
            } case NO_CONNECTIVITY: {
                blacklistAndDisconnectFromNetwork();
                FirebaseAnalytics.getInstance(this).logEvent("connectivity_none", null);
                break;
            }
        }

        new WifiHelper(this).cleanupSavedWifiNetworks();
    }

    private void blacklistAndDisconnectFromNetwork() {
        WifiHelper wifiHelper = new WifiHelper(this);
        if (WifiNetwork.isAutoconnectedNetwork(wifiHelper.getCurrentNetwork())) {
            wifiHelper.blacklistAndDisconnectFromCurrentWifiNetwork();
        }
    }
}
