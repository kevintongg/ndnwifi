//package no.bouvet.p2pcommunication.util.button;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Messenger;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.widget.Button;
//
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import no.bouvet.p2pcommunication.R;
//import no.bouvet.p2pcommunication.listener.wifip2p.WifiP2pListener;
//import no.bouvet.p2pcommunication.locationSocket.LocationAsyncTask;
//import no.bouvet.p2pcommunication.locationSocket.LocationReceiverService;
//import no.bouvet.p2pcommunication.locationSocket.LocationAsyncTask;
//import no.bouvet.p2pcommunication.multicast.MulticastMessage;
//import no.bouvet.p2pcommunication.multicast.MulticastMessageReceivedHandler;
//import no.bouvet.p2pcommunication.multicast.MulticastMessageReceiverService;
//
//public class LocationButton extends Button {
//    private WifiP2pListener wifiP2pListener;
//    private DiscoveryAndConnectionButtonState buttonState;
//    private Intent multicastReceiverServiceIntent;
//
//    public LocationButton(Context context) {
//        super(context);
//    }
//
//    public LocationButton(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public LocationButton(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    public void initialize(WifiP2pListener wifiP2pListener){
//        this.wifiP2pListener = wifiP2pListener;
//    }
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        ButterKnife.inject(this);
//    }
//
//    @OnClick
//    public void onClick() {
//        new LocationAsyncTask().execute();
//    }
//
//
//}
