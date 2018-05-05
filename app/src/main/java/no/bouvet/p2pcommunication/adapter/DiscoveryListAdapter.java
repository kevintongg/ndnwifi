package no.bouvet.p2pcommunication.adapter;

import static no.bouvet.p2pcommunication.P2PCommunicationActivity.deviceList;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.Objects;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.devicelist.Device;

public class DiscoveryListAdapter extends ArrayAdapter<WifiP2pDevice> {

  private final Context CONTEXT;
  private final int LAYOUT_RESOURCE_ID;

  public DiscoveryListAdapter(Context CONTEXT, int LAYOUT_RESOURCE_ID) {
    super(CONTEXT, LAYOUT_RESOURCE_ID);
    this.CONTEXT = CONTEXT;
    this.LAYOUT_RESOURCE_ID = LAYOUT_RESOURCE_ID;
  }

  @NonNull
  @Override
  public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
    convertView = ensureConvertView(convertView);
    DiscoveryListAdapterViewHolder discoveryListAdapterViewHolder = ensureDiscoveryListAdapterViewHolder(
        convertView);

    WifiP2pDevice myDevice = getItem(position);

    //saves the name and key
    if (!(deviceList.containsKey(Objects.requireNonNull(myDevice).deviceAddress))) {
      deviceList
          .put(myDevice.deviceAddress, new Device(myDevice.deviceName, myDevice.deviceAddress));
    }
    discoveryListAdapterViewHolder.deviceNameTextView.setText(myDevice.deviceName);
    discoveryListAdapterViewHolder.deviceStatusTextView.setText(getDeviceStatus(myDevice.status));
    return convertView;
  }

  private View ensureConvertView(View convertView) {
    if (convertView == null) {
      convertView = getLayoutInflaterService().inflate(LAYOUT_RESOURCE_ID, null);
    }
    return convertView;
  }

  private DiscoveryListAdapterViewHolder ensureDiscoveryListAdapterViewHolder(View convertView) {
    if (convertView.getTag() == null) {
      convertView.setTag(new DiscoveryListAdapterViewHolder(convertView));
    }
    return (DiscoveryListAdapterViewHolder) convertView.getTag();
  }

  private LayoutInflater getLayoutInflaterService() {
    return (LayoutInflater) CONTEXT.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  private String getDeviceStatus(int deviceStatus) {
    switch (deviceStatus) {
      case WifiP2pDevice.AVAILABLE:
        return CONTEXT.getString(R.string.tap_to_connect);
      case WifiP2pDevice.INVITED:
        return CONTEXT.getString(R.string.invited);
      case WifiP2pDevice.CONNECTED:
        return CONTEXT.getString(R.string.connected);
      case WifiP2pDevice.FAILED:
        return CONTEXT.getString(R.string.failed);
      case WifiP2pDevice.UNAVAILABLE:
        return CONTEXT.getString(R.string.unavailable);
      default:
        return CONTEXT.getString(R.string.unknown);
    }
  }
}
