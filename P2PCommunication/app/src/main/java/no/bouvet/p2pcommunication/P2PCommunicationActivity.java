package no.bouvet.p2pcommunication;


import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import no.bouvet.p2pcommunication.adapter.P2pCommunicationFragmentPagerAdapter;
import no.bouvet.p2pcommunication.broadcastreceiver.WifiP2pBroadcastReceiver;
import no.bouvet.p2pcommunication.fragment.CommunicationFragment;
import no.bouvet.p2pcommunication.fragment.DiscoveryAndConnectionFragment;
import no.bouvet.p2pcommunication.listener.discovery.DiscoveryStateListener;
import no.bouvet.p2pcommunication.listener.invitation.InvitationToConnectListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastListener;
import no.bouvet.p2pcommunication.listener.onpagechange.ViewPagerOnPageChangeListener;
import no.bouvet.p2pcommunication.listener.wifip2p.WifiP2pListener;
import no.bouvet.p2pcommunication.wifip2p.P2pCommunicationWifiP2pManager;

public class P2PCommunicationActivity extends FragmentActivity implements WifiP2pListener, MulticastListener {

    public static final String TAG = P2PCommunicationActivity.class.getSimpleName();
    private P2pCommunicationWifiP2pManager p2pCommunicationWifiP2pManager;
    private WifiP2pBroadcastReceiver wifiP2pBroadcastReceiver;
    private P2pCommunicationFragmentPagerAdapter p2pCommunicationFragmentPagerAdapter;
    private boolean wifiP2pEnabled;

    @InjectView(R.id.view_pager) ViewPager viewPager;
    @InjectView(R.id.my_device_name_text_view) TextView myDeviceNameTextView;
    @InjectView(R.id.my_device_status_text_view) TextView myDeviceStatusTextView;
    @InjectView(R.id.am_i_host_question_text_view) TextView amIHostQuestionTextView;
    @InjectView(R.id.host_ip_text_view) TextView hostIpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ButterKnife.inject(this);
        createAndAcquireMulticastLock();
        p2pCommunicationWifiP2pManager = new P2pCommunicationWifiP2pManager(getApplicationContext());
        wifiP2pBroadcastReceiver = new WifiP2pBroadcastReceiver(getApplicationContext(), this);
        p2pCommunicationFragmentPagerAdapter = new P2pCommunicationFragmentPagerAdapter(getSupportFragmentManager(), getFragmentList());
        setViewPager(viewPager, p2pCommunicationFragmentPagerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(wifiP2pBroadcastReceiver, createWifiP2pIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(wifiP2pBroadcastReceiver);
    }

    @Override
    public void onWifiP2pStateEnabled() {
        wifiP2pEnabled = true;
    }

    @Override
    public void onWifiP2pStateDisabled() {
        wifiP2pEnabled = false;
    }

    @Override
    public void onStartPeerDiscovery() {
        if (wifiP2pEnabled) {
            DiscoveryStateListener discoveryStateListener = p2pCommunicationFragmentPagerAdapter.getDiscoveryAndConnectionFragment();
            p2pCommunicationWifiP2pManager.startPeerDiscovery(discoveryStateListener);
        } else {
            Toast.makeText(this, R.string.wifi_p2p_disabled_please_enable, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStopPeerDiscovery() {
        DiscoveryStateListener discoveryStateListener = p2pCommunicationFragmentPagerAdapter.getDiscoveryAndConnectionFragment();
        p2pCommunicationWifiP2pManager.stopPeerDiscovery(discoveryStateListener);
    }

    @Override
    public void onRequestPeers() {
        PeerListListener peerListListener = p2pCommunicationFragmentPagerAdapter.getDiscoveryAndConnectionFragment();
        p2pCommunicationWifiP2pManager.requestPeers(peerListListener);
    }

    @Override
    public void onConnect(WifiP2pDevice wifiP2pDevice) {
        InvitationToConnectListener invitationToConnectListener = p2pCommunicationFragmentPagerAdapter.getDiscoveryAndConnectionFragment();
        p2pCommunicationWifiP2pManager.connect(wifiP2pDevice, invitationToConnectListener);
    }

    @Override
    public void onCancelConnect() {
        p2pCommunicationWifiP2pManager.cancelConnect();
    }

    @Override
    public void onDisconnect() {
        p2pCommunicationWifiP2pManager.disconnect();
    }

    @Override
    public void onIsDisconnected() {
        p2pCommunicationFragmentPagerAdapter.getDiscoveryAndConnectionFragment().reset();
        p2pCommunicationFragmentPagerAdapter.getCommunicationFragment().reset();
        onGroupHostInfoChanged(null);
    }

    @Override
    public void onCreateGroup() {
        p2pCommunicationWifiP2pManager.createGroup();
    }

    @Override
    public void onRequestConnectionInfo() {
        ConnectionInfoListener connectionInfoListener = p2pCommunicationFragmentPagerAdapter.getDiscoveryAndConnectionFragment();
        p2pCommunicationWifiP2pManager.requestConnectionInfo(connectionInfoListener);
    }

    @Override
    public void onThisDeviceChanged(WifiP2pDevice wifiP2pDevice) {
        myDeviceNameTextView.setText(wifiP2pDevice.deviceName);
        myDeviceStatusTextView.setText(getDeviceStatus(wifiP2pDevice.status));
    }

    @Override
    public void onGroupHostInfoChanged(WifiP2pInfo wifiP2pInfo) {
        if (wifiP2pInfo != null && wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
            amIHostQuestionTextView.setText(getString(R.string.am_i_host_question) + " " + getResources().getString(R.string.yes));
            hostIpTextView.setText(getString(R.string.ip_capital_letters) + ": " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
        } else if (wifiP2pInfo != null && wifiP2pInfo.groupFormed) {
            amIHostQuestionTextView.setText(getString(R.string.am_i_host_question) + " " + getResources().getString(R.string.no));
            hostIpTextView.setText("");
        } else {
            amIHostQuestionTextView.setText("");
            hostIpTextView.setText("");
        }
    }

    @Override
    public void onStartReceivingMulticastMessages() {
        p2pCommunicationFragmentPagerAdapter.getCommunicationFragment().startReceivingMulticastMessages();
    }

    private void createAndAcquireMulticastLock() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            MulticastLock multicastLock = wifiManager.createMulticastLock(TAG);
            multicastLock.acquire();
        }
    }

    private void setViewPager(ViewPager viewPager, PagerAdapter pagerAdapter) {
        viewPager.setOnPageChangeListener(new ViewPagerOnPageChangeListener(viewPager));
        viewPager.setAdapter(pagerAdapter);
    }

    private List<Fragment> getFragmentList() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(DiscoveryAndConnectionFragment.newInstance());
        fragmentList.add(CommunicationFragment.newInstance());
        return fragmentList;
    }

    private IntentFilter createWifiP2pIntentFilter() {
        IntentFilter wifiP2pIntentFilter = new IntentFilter();
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return wifiP2pIntentFilter;
    }

    private String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return getString(R.string.available);
            case WifiP2pDevice.INVITED:
                return getString(R.string.invited);
            case WifiP2pDevice.CONNECTED:
                return getString(R.string.connected);
            case WifiP2pDevice.FAILED:
                return getString(R.string.failed);
            case WifiP2pDevice.UNAVAILABLE:
                return getString(R.string.unavailable);
            default:
                return getString(R.string.unknown);
        }
    }

}
