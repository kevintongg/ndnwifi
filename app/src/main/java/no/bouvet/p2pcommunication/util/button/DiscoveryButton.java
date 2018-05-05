package no.bouvet.p2pcommunication.util.button;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.listener.wifip2p.WifiP2PListener;


public class DiscoveryButton extends android.support.v7.widget.AppCompatButton {

  private WifiP2PListener wifiP2PListener;
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

  public void initialize(WifiP2PListener wifiP2PListener) {
    this.wifiP2PListener = wifiP2PListener;
    setStateStartDiscovery();
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
  }

  @OnClick
  public void onClick() {
    if (buttonState == DiscoveryAndConnectionButtonState.START_DISCOVERY) {
      wifiP2PListener.onStartPeerDiscovery();
    } else if (buttonState == DiscoveryAndConnectionButtonState.STOP_DISCOVERY) {
      wifiP2PListener.onStopPeerDiscovery();
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
