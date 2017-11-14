package no.bouvet.p2pcommunication;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
<<<<<<< HEAD

import butterknife.ButterKnife;
import butterknife.BindView;
=======
>>>>>>> e658daf6c4acb525f8d893375dd1458db3a8d052
import no.bouvet.p2pcommunication.adapter.P2pCommunicationFragmentPagerAdapter;
import no.bouvet.p2pcommunication.broadcastreceiver.WifiP2pBroadcastReceiver;
import no.bouvet.p2pcommunication.fragment.CommunicationFragment;
import no.bouvet.p2pcommunication.fragment.DiscoveryAndConnectionFragment;
import no.bouvet.p2pcommunication.listener.discovery.DiscoveryStateListener;
import no.bouvet.p2pcommunication.listener.invitation.InvitationToConnectListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastListener;
import no.bouvet.p2pcommunication.listener.onpagechange.ViewPagerOnPageChangeListener;
import no.bouvet.p2pcommunication.listener.wifip2p.WifiP2pListener;
import no.bouvet.p2pcommunication.locationSocket.Direction;
import no.bouvet.p2pcommunication.locationSocket.LocationAsyncTask;
import no.bouvet.p2pcommunication.locationSocket.Locations;
import no.bouvet.p2pcommunication.wifip2p.P2pCommunicationWifiP2pManager;

<<<<<<< Updated upstream
public class P2PCommunicationActivity extends FragmentActivity implements WifiP2pListener, MulticastListener {

    public static final String TAG = P2PCommunicationActivity.class.getSimpleName();
    private P2pCommunicationWifiP2pManager p2pCommunicationWifiP2pManager;
    private WifiP2pBroadcastReceiver wifiP2pBroadcastReceiver;
    private P2pCommunicationFragmentPagerAdapter p2pCommunicationFragmentPagerAdapter;
    private boolean wifiP2pEnabled;
    public static ArrayList<Double> locationGetter = new ArrayList<>();
    public static final HashMap<String, Locations> deviceLocations = new HashMap<>();
    public static String deviceAddress;
    private CompassLocation compass;
    private Context context;
    Locations data;


    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.my_device_name_text_view) TextView myDeviceNameTextView;
    @BindView(R.id.my_device_status_text_view) TextView myDeviceStatusTextView;
    @BindView(R.id.personal_location) TextView personalLocation;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ButterKnife.bind(this);
        createAndAcquireMulticastLock();
        p2pCommunicationWifiP2pManager = new P2pCommunicationWifiP2pManager(getApplicationContext());
        wifiP2pBroadcastReceiver = new WifiP2pBroadcastReceiver(getApplicationContext(), this);
        p2pCommunicationFragmentPagerAdapter = new P2pCommunicationFragmentPagerAdapter(getSupportFragmentManager(), getFragmentList());
        setViewPager(viewPager, p2pCommunicationFragmentPagerAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        compass = new CompassLocation(this);

//        Timer compassTimer = new Timer();
//        compassTimer.schedule(new TimerTask(){
//            public void run(){
//                // write the method name here. which you want to call continuously
//                compass = new CompassLocation(context);
//            }
//        }, 40000, 8000);



        if (locationManager != null && deviceAddress != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Working...");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30, 0, locationListener);
               deviceLocations.put(deviceAddress, new Locations(deviceAddress));



            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(wifiP2pBroadcastReceiver, createWifiP2pIntentFilter());
        compass.onStart();
        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //Request location updates:
                locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 40, 0, locationListener);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(wifiP2pBroadcastReceiver);

=======
public class P2PCommunicationActivity extends FragmentActivity implements WifiP2pListener,
    MulticastListener {

  public static final String TAG = P2PCommunicationActivity.class.getSimpleName();
  private P2pCommunicationWifiP2pManager p2pCommunicationWifiP2pManager;
  private WifiP2pBroadcastReceiver wifiP2pBroadcastReceiver;
  private P2pCommunicationFragmentPagerAdapter p2pCommunicationFragmentPagerAdapter;
  private boolean wifiP2pEnabled;
  public static ArrayList<Double> locationGetter = new ArrayList<>();
  public static final HashMap<String, Locations> deviceLocations = new HashMap<>();
  public static String deviceAddress;

  @InjectView(R.id.view_pager)
  ViewPager viewPager;
  @InjectView(R.id.my_device_name_text_view)
  TextView myDeviceNameTextView;
  @InjectView(R.id.my_device_status_text_view)
  TextView myDeviceStatusTextView;
  @InjectView(R.id.personal_location)
  TextView personalLocation;
  LocationManager locationManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    ButterKnife.inject(this);
    createAndAcquireMulticastLock();
    p2pCommunicationWifiP2pManager = new P2pCommunicationWifiP2pManager(getApplicationContext());
    wifiP2pBroadcastReceiver = new WifiP2pBroadcastReceiver(getApplicationContext(), this);
    p2pCommunicationFragmentPagerAdapter = new P2pCommunicationFragmentPagerAdapter(
        getSupportFragmentManager(), getFragmentList());
    setViewPager(viewPager, p2pCommunicationFragmentPagerAdapter);

    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    if (locationManager != null) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
          == PackageManager.PERMISSION_GRANTED) {
        Log.d(TAG, "Working...");
        locationManager
            .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 2, locationListener);
        deviceLocations.put(deviceAddress, new Locations(deviceAddress));

      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    registerReceiver(wifiP2pBroadcastReceiver, createWifiP2pIntentFilter());

    if (checkLocationPermission()) {
      if (ContextCompat.checkSelfPermission(this,
          Manifest.permission.ACCESS_FINE_LOCATION)
          == PackageManager.PERMISSION_GRANTED) {

        //Request location updates:
        locationManager
            .requestLocationUpdates(locationManager.NETWORK_PROVIDER, 30000, 2, locationListener);
      }
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    unregisterReceiver(wifiP2pBroadcastReceiver);
  }

  @Override
  public void onWifiP2pStateEnabled() {
    wifiP2pEnabled = true;
  }

  @Override
  public void onWifiP2pStateDisabled() {
    wifiP2pEnabled = false;
  }


  //Call socket here (?)
  @Override
  public void onStartPeerDiscovery() {
    if (wifiP2pEnabled) {
      DiscoveryStateListener discoveryStateListener = p2pCommunicationFragmentPagerAdapter
          .getDiscoveryAndConnectionFragment();
      p2pCommunicationWifiP2pManager.startPeerDiscovery(discoveryStateListener);
    } else {
      Toast.makeText(this, R.string.wifi_p2p_disabled_please_enable, Toast.LENGTH_SHORT).show();
>>>>>>> Stashed changes
    }
  }

  @Override
  public void onStopPeerDiscovery() {
    DiscoveryStateListener discoveryStateListener = p2pCommunicationFragmentPagerAdapter
        .getDiscoveryAndConnectionFragment();
    p2pCommunicationWifiP2pManager.stopPeerDiscovery(discoveryStateListener);
  }

  @Override
  public void onRequestPeers() {
    PeerListListener peerListListener = p2pCommunicationFragmentPagerAdapter
        .getDiscoveryAndConnectionFragment();
    p2pCommunicationWifiP2pManager.requestPeers(peerListListener);
  }

  @Override
  public void onConnect(WifiP2pDevice wifiP2pDevice) {
    InvitationToConnectListener invitationToConnectListener = p2pCommunicationFragmentPagerAdapter
        .getDiscoveryAndConnectionFragment();
    p2pCommunicationWifiP2pManager.connect(wifiP2pDevice, invitationToConnectListener);
    deviceLocations.put(wifiP2pDevice.deviceAddress, new Locations(deviceAddress));
  }

  @Override
  public void onCancelConnect() {
    p2pCommunicationWifiP2pManager.cancelConnect();
  }

  @Override
  public void onDisconnect() {
    p2pCommunicationWifiP2pManager.disconnect();
  }

  @Override
  public void onIsDisconnected() {
    p2pCommunicationFragmentPagerAdapter.getDiscoveryAndConnectionFragment().reset();
    p2pCommunicationFragmentPagerAdapter.getCommunicationFragment().reset();
    onGroupHostInfoChanged(null);
  }

  @Override
  public void onCreateGroup() {
    p2pCommunicationWifiP2pManager.createGroup();
  }

  @Override
  public void onRequestConnectionInfo() {
    ConnectionInfoListener connectionInfoListener = p2pCommunicationFragmentPagerAdapter
        .getDiscoveryAndConnectionFragment();
    p2pCommunicationWifiP2pManager.requestConnectionInfo(connectionInfoListener);
  }

  @Override
  public void onThisDeviceChanged(WifiP2pDevice wifiP2pDevice) {
    deviceAddress = wifiP2pDevice.deviceAddress;
    myDeviceNameTextView.setText(wifiP2pDevice.deviceName);
    myDeviceStatusTextView.setText(getDeviceStatus(wifiP2pDevice.status));
  }

  @Override
  public void onGroupHostInfoChanged(WifiP2pInfo wifiP2pInfo) {
    if (wifiP2pInfo != null && wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
      // personalLocation.setText(getString(R.string.ip_capital_letters) + ": " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
    } else if (wifiP2pInfo != null && wifiP2pInfo.groupFormed) {
      personalLocation.setText("");
    } else {
      personalLocation.setText("");
    }
  }

  @Override
  public void onStartReceivingMulticastMessages() {
    p2pCommunicationFragmentPagerAdapter.getCommunicationFragment()
        .startReceivingMulticastMessages();
  }

  private void createAndAcquireMulticastLock() {
    WifiManager wifiManager = (WifiManager) this.getApplicationContext()
        .getSystemService(WIFI_SERVICE);
    if (wifiManager != null) {
      MulticastLock multicastLock = wifiManager.createMulticastLock(TAG);
      multicastLock.acquire();
    }
  }

  private void setViewPager(ViewPager viewPager, PagerAdapter pagerAdapter) {
    viewPager.setOnPageChangeListener(new ViewPagerOnPageChangeListener(viewPager));
    viewPager.setAdapter(pagerAdapter);
  }

  private List<Fragment> getFragmentList() {
    List<Fragment> fragmentList = new ArrayList<>();
    fragmentList.add(DiscoveryAndConnectionFragment.newInstance());
    fragmentList.add(CommunicationFragment.newInstance());
    return fragmentList;
  }

  private IntentFilter createWifiP2pIntentFilter() {
    IntentFilter wifiP2pIntentFilter = new IntentFilter();
    wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
    wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
    wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    return wifiP2pIntentFilter;
  }

  private String getDeviceStatus(int deviceStatus) {
    switch (deviceStatus) {
      case WifiP2pDevice.AVAILABLE:
        return getString(R.string.available);
      case WifiP2pDevice.INVITED:
        return getString(R.string.invited);
      case WifiP2pDevice.CONNECTED:
        return getString(R.string.connected);
      case WifiP2pDevice.FAILED:
        return getString(R.string.failed);
      case WifiP2pDevice.UNAVAILABLE:
        return getString(R.string.unavailable);
      default:
        return getString(R.string.unknown);
    }
  }


  //Location listerner gets called when new location is being requested
  private final LocationListener locationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {
      // TODO Auto-generated method stub

      double latitude = location.getLatitude();
      double longitude = location.getLongitude();

      locationGetter.add(latitude);
      locationGetter.add(longitude);

      Locations data = new Locations(deviceAddress, longitude, latitude);

      data.update(deviceAddress, longitude, latitude);
      deviceLocations.put(deviceAddress, data);

      Timer t = new Timer();
      t.schedule(new TimerTask() {
        public void run() {
          // write the method name here. which you want to call continuously
          new LocationAsyncTask().execute();
        }
      }, 40000, 8000);

      personalLocation.setText("Your Coordinates : " + latitude + ",   " + longitude);

<<<<<<< Updated upstream
    @Override
    public void onThisDeviceChanged(WifiP2pDevice wifiP2pDevice) {
        deviceAddress = wifiP2pDevice.deviceAddress;
        data = new Locations(deviceAddress, 0, 0);
        myDeviceNameTextView.setText(wifiP2pDevice.deviceName);
        myDeviceStatusTextView.setText(getDeviceStatus(wifiP2pDevice.status));
    }

    @Override
    public void onGroupHostInfoChanged(WifiP2pInfo wifiP2pInfo) {
        if (wifiP2pInfo != null && wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
           // personalLocation.setText(getString(R.string.ip_capital_letters) + ": " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
        } else if (wifiP2pInfo != null && wifiP2pInfo.groupFormed) {
           // personalLocation.setText("");
        } else {
            //personalLocation.setText("");
        }
    }
=======
      Log.d("Geo_Location", "Latitude: " + latitude + ", Longitude: " + longitude);
    }

    @Override
    public void onProviderDisabled(String provider) {
      // TODO Auto-generated method stub
>>>>>>> Stashed changes

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

  //App permission asks user to use location feature.
  public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

  public boolean checkLocationPermission() {
    if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {

      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
          Manifest.permission.ACCESS_FINE_LOCATION)) {

        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.
        new AlertDialog.Builder(this)
            .setTitle("")
            .setMessage("")
            .setPositiveButton("", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(P2PCommunicationActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
              }
            })
            .create()
            .show();
      } else {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_REQUEST_LOCATION);
      }
      return false;
    } else {
      return true;
    }
<<<<<<< Updated upstream



    //Location listerner gets called when new location is being requested
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub


            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            locationGetter.add(latitude);
            locationGetter.add(longitude);



            data.update(deviceAddress, longitude, latitude);
            deviceLocations.put(deviceAddress, data);
            //Log.d("Prev Locations", "" + deviceLocations.get(deviceAddress).getLocations());


            Timer t = new Timer();
            t.schedule(new TimerTask(){
                public void run(){
                    // write the method name here. which you want to call continuously
                    new LocationAsyncTask().execute();
                }
            }, 40000, 8000);

            updateUserStatus(latitude, longitude);

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

    //App permission asks user to use location feature.
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission. ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(P2PCommunicationActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
=======
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[],
      int[] grantResults) {
    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_LOCATION: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // permission was granted, yay! Do the
          // location-related task you need to do.
          if (ContextCompat.checkSelfPermission(this,
              Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Request location updates:
            locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 6, locationListener);
          }
>>>>>>> Stashed changes
        } else {
<<<<<<< HEAD
            return true;
        }
    }

    private void updateUserStatus(double latitude, double longitude){

        double angle = Direction.getBearings(deviceLocations.get(deviceAddress).getPreviousLongitude(), deviceLocations.get(deviceAddress).getPreviousLatitude(), deviceLocations.get(deviceAddress).getCurrentLongitude(), deviceLocations.get(deviceAddress).getCurrentLatitude());
        String heading = Direction.getBearingsString(angle);

        personalLocation.setText("Your Cordinates : " + latitude + ",   " + longitude + "\nGoing: " + heading );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 30000, 6, locationListener);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }
=======
          // permission denied, boo! Disable the
          // functionality that depends on this permission.
>>>>>>> e658daf6c4acb525f8d893375dd1458db3a8d052
        }
      }
    }
  }
}
