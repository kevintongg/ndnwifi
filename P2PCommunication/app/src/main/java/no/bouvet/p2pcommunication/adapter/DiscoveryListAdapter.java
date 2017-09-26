package no.bouvet.p2pcommunication.adapter;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import no.bouvet.p2pcommunication.R;

public class DiscoveryListAdapter extends ArrayAdapter<WifiP2pDevice> {

    private Context context;
    private int layoutResourceId;

    public DiscoveryListAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = ensureConvertView(convertView);
        DiscoveryListAdapterViewHolder discoveryListAdapterViewHolder = ensureDiscoveryListAdapterViewHolder(convertView);

        WifiP2pDevice wifiP2pDevice = getItem(position);
        discoveryListAdapterViewHolder.deviceNameTextView.setText(wifiP2pDevice.deviceName);
        discoveryListAdapterViewHolder.deviceStatusTextView.setText(getDeviceStatus(wifiP2pDevice.status));

        return convertView;
    }

    private View ensureConvertView(View convertView) {
        if (convertView == null) {
            convertView = getLayoutInflaterService().inflate(layoutResourceId, null);
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
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return context.getString(R.string.tap_to_connect);
            case WifiP2pDevice.INVITED:
                return context.getString(R.string.invited);
            case WifiP2pDevice.CONNECTED:
                return context.getString(R.string.connected);
            case WifiP2pDevice.FAILED:
                return context.getString(R.string.failed);
            case WifiP2pDevice.UNAVAILABLE:
                return context.getString(R.string.unavailable);
            default:
                return context.getString(R.string.unknown);
        }
    }

}
