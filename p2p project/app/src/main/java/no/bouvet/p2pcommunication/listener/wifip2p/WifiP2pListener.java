package no.bouvet.p2pcommunication.listener.wifip2p;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;

public interface WifiP2pListener {

    void onWifiP2pStateEnabled();

    void onWifiP2pStateDisabled();

    void onStartPeerDiscovery();

    void onStopPeerDiscovery();

    void onRequestPeers();

    void onConnect(WifiP2pDevice wifiP2pDevice);

    void onCancelConnect();

    void onDisconnect();

    void onIsDisconnected();

    void onCreateGroup();

    void onRequestConnectionInfo();

    void onThisDeviceChanged(WifiP2pDevice wifiP2pDevice);

    void onGroupHostInfoChanged(WifiP2pInfo wifiP2pInfo);

}
