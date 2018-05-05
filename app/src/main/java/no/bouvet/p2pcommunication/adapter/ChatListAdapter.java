package no.bouvet.p2pcommunication.adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.Objects;
import no.bouvet.p2pcommunication.multicast.MulticastMessage;

public class ChatListAdapter extends ArrayAdapter<MulticastMessage> {

  private final Context CONTEXT;
  private final int LAYOUT_RESOURCE_ID;

  //Location listener gets called when new location is being requested
  private final LocationListener locationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      // TODO Auto-generated method stub

      double latitude = location.getLatitude();
      double longitude = location.getLongitude();
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

  public ChatListAdapter(Context CONTEXT, int LAYOUT_RESOURCE_ID) {
    super(CONTEXT, LAYOUT_RESOURCE_ID);
    this.CONTEXT = CONTEXT;
    this.LAYOUT_RESOURCE_ID = LAYOUT_RESOURCE_ID;
  }

  @Override
  public boolean isEnabled(int position) {
    return false;
  }

  @NonNull
  @Override
  public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
    convertView = ensureConvertView(convertView);
    ChatListAdapterViewHolder chatListAdapterViewHolder = ensureChatListAdapterViewHolder(
        convertView);
    LocationManager locationManager = (LocationManager) CONTEXT
        .getSystemService(Context.LOCATION_SERVICE);
    if (locationManager != null) {
      if (ContextCompat.checkSelfPermission(CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION)
          == PackageManager.PERMISSION_GRANTED) {
        locationManager
            .requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 6, locationListener);
      }
    }

    MulticastMessage multicastMessage = getItem(position);

    if (!(Objects.requireNonNull(multicastMessage).isSentByMe())) {
      chatListAdapterViewHolder.messageSentLayout.setVisibility(View.GONE);
      chatListAdapterViewHolder.messageReceivedLayout.setVisibility(View.VISIBLE);
      chatListAdapterViewHolder.messageReceivedTextView
          .setText(
              String.format("%s:\n%s", multicastMessage.getSENDER_IP_ADDRESS(),
                  multicastMessage.getText()));
    } else if (multicastMessage.isSentByMe()) {
      chatListAdapterViewHolder.messageReceivedLayout.setVisibility(View.GONE);
      chatListAdapterViewHolder.messageSentLayout.setVisibility(View.VISIBLE);
      chatListAdapterViewHolder.messageSentTextView.setText(multicastMessage.getText());
    }
    return convertView;
  }

  private LayoutInflater getLayoutInflaterService() {
    return (LayoutInflater) CONTEXT.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  private View ensureConvertView(View convertView) {
    if (convertView == null) {
      convertView = getLayoutInflaterService().inflate(LAYOUT_RESOURCE_ID, null);
    }
    return convertView;
  }

  private ChatListAdapterViewHolder ensureChatListAdapterViewHolder(View convertView) {
    if (convertView.getTag() == null) {
      convertView.setTag(new ChatListAdapterViewHolder(convertView));
    }
    return (ChatListAdapterViewHolder) convertView.getTag();
  }
}
