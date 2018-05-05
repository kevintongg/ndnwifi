package no.bouvet.p2pcommunication.util.button;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.listener.wifip2p.WifiP2PListener;

public class ConnectionButton extends android.support.v7.widget.AppCompatButton {

  private WifiP2PListener wifiP2PListener;
  private DiscoveryAndConnectionButtonState buttonState;

  public ConnectionButton(Context context) {
    super(context);
  }

  public ConnectionButton(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ConnectionButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void initialize(WifiP2PListener wifiP2PListener) {
    this.wifiP2PListener = wifiP2PListener;
    setStateCreateGroup();
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
  }

  @OnClick
  public void onClick() {
    if (buttonState == DiscoveryAndConnectionButtonState.CANCEL_INVITATION) {
      wifiP2PListener.onCancelConnect();
    } else if (buttonState == DiscoveryAndConnectionButtonState.CREATE_GROUP) {
      wifiP2PListener.onCreateGroup();
    } else if (buttonState == DiscoveryAndConnectionButtonState.DISCONNECT) {
      wifiP2PListener.onDisconnect();
    }
  }

  public void setStateCancelInvitation() {
    setText(getContext().getString(R.string.cancel_invitation));
    buttonState = DiscoveryAndConnectionButtonState.CANCEL_INVITATION;
  }

  public void setStateCreateGroup() {
    setText(getContext().getString(R.string.create_group));
    buttonState = DiscoveryAndConnectionButtonState.CREATE_GROUP;
  }

  public void setStateDisconnect() {
    setText(getContext().getString(R.string.disconnect));
    buttonState = DiscoveryAndConnectionButtonState.DISCONNECT;
  }

}
