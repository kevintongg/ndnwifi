package no.bouvet.p2pcommunication.adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import butterknife.BindView;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.multicast.MulticastMessage;

public class ChatListAdapter extends ArrayAdapter<MulticastMessage> {

    private Context context;
    private int layoutResourceId;
    LocationManager locationManager;
    private double longitude, latitude;
   // @BindView(R.id.location_text_view)
    TextView location_text_view;


    public ChatListAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = ensureConvertView(convertView);
        ChatListAdapterViewHolder chatListAdapterViewHolder = ensureChatListAdapterViewHolder(convertView);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 6, locationListener);

            }
        }

        MulticastMessage multicastMessage = getItem(position);
        if (!(multicastMessage.isSentByMe())) {
            chatListAdapterViewHolder.messageSentLayout.setVisibility(View.GONE);
            chatListAdapterViewHolder.messageReceivedLayout.setVisibility(View.VISIBLE);
            chatListAdapterViewHolder.messageReceivedTextView.setText(multicastMessage.getSenderIpAddress() + ":\n" + multicastMessage.getText());
            //chatListAdapterViewHolder.locationTextView.setText("Londitude:" + longitude + " Latutude:" + latitude);

        } else if(multicastMessage.isSentByMe()) {
            chatListAdapterViewHolder.messageReceivedLayout.setVisibility(View.GONE);
            chatListAdapterViewHolder.messageSentLayout.setVisibility(View.VISIBLE);
            chatListAdapterViewHolder.messageSentTextView.setText(multicastMessage.getText());
        }

        return convertView;
    }

    private LayoutInflater getLayoutInflaterService() {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private View ensureConvertView(View convertView) {
        if (convertView == null) {
            convertView = getLayoutInflaterService().inflate(layoutResourceId, null);
        }
        return convertView;
    }

    private ChatListAdapterViewHolder ensureChatListAdapterViewHolder(View convertView) {
        if (convertView.getTag() == null) {
            convertView.setTag(new ChatListAdapterViewHolder(convertView));
        }
        return (ChatListAdapterViewHolder) convertView.getTag();
    }


    //Location listerner gets called when new location is being requested
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("Geo_Location", "Latitude: " + latitude + ", Longitude: " + longitude);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    };
}
