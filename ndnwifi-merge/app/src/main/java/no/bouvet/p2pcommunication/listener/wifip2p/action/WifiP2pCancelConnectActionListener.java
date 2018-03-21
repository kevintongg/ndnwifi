package no.bouvet.p2pcommunication.listener.wifip2p.action;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.util.Log;
import android.widget.Toast;

import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.wifip2p.P2pCommunicationWifiP2pManager;

public class WifiP2pCancelConnectActionListener implements ActionListener {

    private static final String TAG = WifiP2pCancelConnectActionListener.class.getSimpleName();
    private final Context context;

    public WifiP2pCancelConnectActionListener(Context context) {
        this.context = context;
    }

    @Override
    public void onSuccess() {
        Toast.makeText(context, context.getString(R.string.successfully_cancelled_connection), Toast.LENGTH_SHORT).show();
        Log.i(TAG, context.getString(R.string.successfully_cancelled_connection));
    }

    @Override
    public void onFailure(int reasonCode) {
        String reason = context.getString(R.string.could_not_cancel_connection) + ": ";
        reason += P2pCommunicationWifiP2pManager.getFailureReason(context, reasonCode);
        Toast.makeText(context, reason, Toast.LENGTH_SHORT).show();
        Log.w(TAG, reason);
    }
}
