package no.bouvet.p2pcommunication.listener.wifip2p.action;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.listener.invitation.InvitationToConnectListener;
import no.bouvet.p2pcommunication.wifip2p.P2pCommunicationWifiP2pManager;

public class WifiP2pConnectActionListener implements WifiP2pManager.ActionListener {

    private static final String TAG = WifiP2pConnectActionListener.class.getSimpleName();
    private Context context;
    private InvitationToConnectListener invitationToConnectListener;

    public WifiP2pConnectActionListener(Context context, InvitationToConnectListener invitationToConnectListener) {
        this.context = context;
        this.invitationToConnectListener = invitationToConnectListener;
    }

    @Override
    public void onSuccess() {
        invitationToConnectListener.onSentInvitationToConnect();
        Log.i(TAG, context.getString(R.string.successfully_sent_invitation_to_connect));
    }

    @Override
    public void onFailure(int reasonCode) {
        String reason = context.getString(R.string.could_not_send_invitation_to_connect) + ": ";
        reason += P2pCommunicationWifiP2pManager.getFailureReason(context, reasonCode);
        Toast.makeText(context, reason, Toast.LENGTH_SHORT).show();
        Log.w(TAG, reason);
    }
}
