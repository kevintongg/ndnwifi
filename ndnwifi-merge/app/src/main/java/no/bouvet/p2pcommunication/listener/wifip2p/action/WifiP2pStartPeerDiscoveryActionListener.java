package no.bouvet.p2pcommunication.listener.wifip2p.action;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.util.Log;
import android.widget.Toast;

import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.listener.discovery.DiscoveryStateListener;
import no.bouvet.p2pcommunication.wifip2p.P2pCommunicationWifiP2pManager;

public class WifiP2pStartPeerDiscoveryActionListener implements ActionListener {

    private static final String TAG = WifiP2pStartPeerDiscoveryActionListener.class.getSimpleName();
    private final Context context;
    private DiscoveryStateListener discoveryStateListener;

    public WifiP2pStartPeerDiscoveryActionListener(Context context, DiscoveryStateListener discoveryStateListener) {
        this.context = context;
        this.discoveryStateListener = discoveryStateListener;
    }

    @Override
    public void onSuccess() {
        discoveryStateListener.onStartedDiscovery();
        Log.i(TAG, context.getString(R.string.successfully_started_discovery));
    }

    @Override
    public void onFailure(int reasonCode) {
        String reason = context.getString(R.string.could_not_start_discovery) + ": ";
        reason += P2pCommunicationWifiP2pManager.getFailureReason(context, reasonCode);
        Toast.makeText(context, reason, Toast.LENGTH_SHORT).show();
        Log.w(TAG, reason);
    }

}
