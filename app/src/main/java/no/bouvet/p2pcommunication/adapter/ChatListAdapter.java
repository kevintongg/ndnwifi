package no.bouvet.p2pcommunication.adapter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.multicast.MulticastMessage;

public class ChatListAdapter extends ArrayAdapter<MulticastMessage> {

    private Context context;
    private int layoutResourceId;
    LocationManager locationManager;
    private double longitude, latitude;
    @InjectView(R.id.location_text_view)
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


        P2PCommunicationActivity.locationGetter.get(0);
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 6, locationListener);

            }
        }

        MulticastMessage multicastMessage = getItem(position);
        if (multicastMessage.isSentByMe()) {
            chatListAdapterViewHolder.messageReceivedLayout.setVisibility(View.GONE);
            chatListAdapterViewHolder.messageSentLayout.setVisibility(View.VISIBLE);
            chatListAdapterViewHolder.messageSentTextView.setText(multicastMessage.getText());
            //chatListAdapterViewHolder.locationTextView.setText("Londitude:" + longitude + " Latutude:" + latitude);
        } else {
            chatListAdapterViewHolder.messageReceivedLayout.setVisibility(View.VISIBLE);
            chatListAdapterViewHolder.messageSentLayout.setVisibility(View.GONE);
            chatListAdapterViewHolder.messageReceivedTextView.setText(multicastMessage.getSenderIpAddress() + ":\n" + multicastMessage.getText());
            chatListAdapterViewHolder.locationTextView.setText("Londitude:" + longitude + " Latutude:" + latitude);
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

    private double getLocationStatus(double location){
        return 56;
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
//
//    //App permission asks user to use location feature.
//    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
//
//    public boolean checkLocationPermission() {
//        if (ContextCompat.checkSelfPermission(context,
//                Manifest.permission. ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
//                    Manifest.permission. ACCESS_FINE_LOCATION)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//                new AlertDialog.Builder(context)
//                        .setTitle("Hello World")
//                        .setMessage("Hello World")
//                        .setPositiveButton("Okkkkkkk", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //Prompt the user once explanation has been shown
//                                ActivityCompat.requestPermissions(context,
//                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                        MY_PERMISSIONS_REQUEST_LOCATION);
//                            }
//                        })
//                        .create()
//                        .show();
//
//
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(context,
//                        new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_LOCATION);
//            }
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // location-related task you need to do.
//                    if (ContextCompat.checkSelfPermission(context,
//                            Manifest.permission.ACCESS_FINE_LOCATION)
//                            == PackageManager.PERMISSION_GRANTED) {
//
//                        //Request location updates:
//                        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 30000, 6, locationListener);
//                    }
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//
//                }
//                return;
//            }
//        }
//    }
}
