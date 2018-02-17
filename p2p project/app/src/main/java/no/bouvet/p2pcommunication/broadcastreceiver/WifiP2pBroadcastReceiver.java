/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.bouvet.p2pcommunication.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.listener.wifip2p.WifiP2pListener;

public class WifiP2pBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = WifiP2pBroadcastReceiver.class.getSimpleName();
    private final Context context;
    private WifiP2pListener wifiP2pListener;

    public WifiP2pBroadcastReceiver(Context context, WifiP2pListener wifiP2pListener) {
        this.context = context;
        this.wifiP2pListener = wifiP2pListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            notifyWifiP2pStateChanged(intent);

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            notifyPeersChanged();

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            notifyConnectionStateChanged(getNetworkInfo(intent));

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            notifyThisDeviceChanged(intent);
        }
    }

    private void notifyWifiP2pStateChanged(Intent intent) {
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            wifiP2pListener.onWifiP2pStateEnabled();
            Log.i(TAG, context.getString(R.string.wifi_p2p_enabled) + " (" + state + ")");
        } else {
            wifiP2pListener.onWifiP2pStateDisabled();
            Log.i(TAG, context.getString(R.string.wifi_p2p_disabled) + " (" + state + ")");
        }
    }

    private void notifyPeersChanged() {
        wifiP2pListener.onRequestPeers();
        Log.i(TAG, context.getString(R.string.peers_requested));
    }

    private NetworkInfo getNetworkInfo(Intent intent) {
        return intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
    }

    private void notifyConnectionStateChanged(NetworkInfo networkInfo) {
        if (networkInfo.isConnected()) {
            wifiP2pListener.onRequestConnectionInfo();
            Log.i(TAG, context.getString(R.string.is_connected));
        } else {
            wifiP2pListener.onIsDisconnected();
            Log.i(TAG, context.getString(R.string.is_disconnected));
        }
    }

    private void notifyThisDeviceChanged(Intent intent) {
        WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        wifiP2pListener.onThisDeviceChanged(wifiP2pDevice);
        Log.i(TAG, context.getString(R.string.this_device_changed));
    }
}
