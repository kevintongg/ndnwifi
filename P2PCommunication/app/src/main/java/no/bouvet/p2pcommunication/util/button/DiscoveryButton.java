package no.bouvet.p2pcommunication.util.button;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.OnClick;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.listener.wifip2p.WifiP2pListener;

public class DiscoveryButton extends Button {

    private WifiP2pListener wifiP2pListener;
    private DiscoveryAndConnectionButtonState buttonState;

    public DiscoveryButton(Context context) {
        super(context);
    }

    public DiscoveryButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiscoveryButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initialize(WifiP2pListener wifiP2pListener){
        this.wifiP2pListener = wifiP2pListener;
        setStateStartDiscovery();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @OnClick
    public void onClick() {
        if (buttonState == DiscoveryAndConnectionButtonState.START_DISCOVERY) {
            wifiP2pListener.onStartPeerDiscovery();
        } else if (buttonState == DiscoveryAndConnectionButtonState.STOP_DISCOVERY) {
            wifiP2pListener.onStopPeerDiscovery();
        }
    }

    public void setStateStartDiscovery() {
        setText(getContext().getString(R.string.discover));
        buttonState = DiscoveryAndConnectionButtonState.START_DISCOVERY;
    }

    public void setStateStopDiscovery() {
        setText(getContext().getString(R.string.stop));
        buttonState = DiscoveryAndConnectionButtonState.STOP_DISCOVERY;
    }
}
