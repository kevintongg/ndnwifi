package no.bouvet.p2pcommunication;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import no.bouvet.p2pcommunication.adapter.P2pCommunicationFragmentPagerAdapter;
import no.bouvet.p2pcommunication.broadcastreceiver.WifiP2pBroadcastReceiver;
import no.bouvet.p2pcommunication.deviceList.Device;
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

import static java.lang.Double.NaN;

public class P2PCommunicationActivity extends FragmentActivity implements WifiP2pListener, MulticastListener {

  public static final String TAG = P2PCommunicationActivity.class.getSimpleName();
  public static final int SERVER_PORT = 4545;
  public static final int MESSAGE_READ = 0x400 + 1;
  public static final int MY_HANDLE = 0x400 + 2;
  final static double TEST_DESTINATION_LAT = 42.042246;
  final static double TEST_DESTINATION_LONG = -118.665396;
  public static P2pCommunicationWifiP2pManager p2pCommunicationWifiP2pManager;
  public static ArrayList<Double> locationGetter = new ArrayList<>();
  public static HashMap<String, Locations> deviceLocations = new HashMap<>();
  public static HashMap<String, Device> deviceList = new HashMap<>();
  public static String myDeviceName = "Me";

  public String deviceAddress;
  public static boolean isChatOn = false;
  // import main activity
  // then use current device wifiP2pDevice
  public static WifiP2pDevice currentDevice;
  public static String devicesInChat = "";
  Locations data;

  //test data
  Locations data2 = new Locations("Closest", 0, 0);


  @BindView(R.id.view_pager)
  ViewPager viewPager;
  @BindView(R.id.my_device_name_text_view)
  TextView myDeviceNameTextView;
  @BindView(R.id.my_device_status_text_view)
  TextView myDeviceStatusTextView;
  @BindView(R.id.personal_location)
  TextView personalLocation;
  @BindView(R.id.am_i_host_question_text_view)
  TextView amIHostQuestionTextView;
  @BindView(R.id.host_ip_text_view)
  TextView hostIpTextView;
  LocationManager locationManager;

  private WifiP2pBroadcastReceiver wifiP2pBroadcastReceiver;
  private P2pCommunicationFragmentPagerAdapter p2pCommunicationFragmentPagerAdapter;
  private boolean wifiP2pEnabled;
  private Handler handler;

  public Handler getHandler() {
    return handler;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    ButterKnife.bind(this);
    createAndAcquireMulticastLock();
    p2pCommunicationWifiP2pManager = new P2pCommunicationWifiP2pManager(getApplicationContext());
    wifiP2pBroadcastReceiver = new WifiP2pBroadcastReceiver(getApplicationContext(), this);
    p2pCommunicationFragmentPagerAdapter = new P2pCommunicationFragmentPagerAdapter(
            getSupportFragmentManager(), getFragmentList());
    setViewPager(viewPager, p2pCommunicationFragmentPagerAdapter);

    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    data = new Locations(myDeviceName, 0, 0);

    isBluetoothEnabled();
    isStoragePermissionGranted();

    if (locationManager != null) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
              == PackageManager.PERMISSION_GRANTED) {
        Log.d(TAG, "Location Working...");
        locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locationListener);
        deviceLocations.put(myDeviceName, new Locations(myDeviceName));
      }
    }
    checkLocationPermission();
  }

  @Override
  public void onResume() {
    super.onResume();
    registerReceiver(wifiP2pBroadcastReceiver, createWifiP2pIntentFilter());
    checkLocationPermission();

    if (currentDevice == null) {
      currentDevice = getIntent().getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
    }
  }

  /* --------------------------------------- Permission Requests --------------------------------------- */
  @SuppressLint("MissingPermission")
  @SuppressWarnings("MissingPermission")
  public Boolean isBluetoothEnabled() {
    Boolean isBluetoothEnabled = null;
    try {
      PackageManager pm = this.getPackageManager();
      int hasBluetoothPermission = pm.checkPermission(
              Manifest.permission.BLUETOOTH,
              this.getPackageName());
      if (hasBluetoothPermission == PackageManager.PERMISSION_GRANTED) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
          isBluetoothEnabled = bluetoothAdapter.isEnabled();
        }
      }
    } catch (SecurityException e) {
      // do nothing since we don't have permissions
    } catch (NoClassDefFoundError e) {
      // Some phones doesn't have this class. Just ignore it
    }
    return isBluetoothEnabled;
  }

  public boolean isStoragePermissionGranted() {
    if (Build.VERSION.SDK_INT >= 23) {
      if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
              == PackageManager.PERMISSION_GRANTED) {
        Log.v(TAG, "Permission is granted");
        return true;
      } else {

        Log.v(TAG, "Permission is revoked");
        ActivityCompat
                .requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        return false;
      }
    } else { //permission is automatically granted on sdk<23 upon installation
      Log.v(TAG, "Permission is granted");
      return true;
    }
  }

  /* --------------------------------------- Wi-Fi Direct Methods --------------------------------------- */
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

  @Override
  public void onStartPeerDiscovery() {
    if (wifiP2pEnabled) {
      DiscoveryStateListener discoveryStateListener = p2pCommunicationFragmentPagerAdapter
              .getDiscoveryAndConnectionFragment();
      p2pCommunicationWifiP2pManager.startPeerDiscovery(discoveryStateListener);
    } else {
      Toast.makeText(this, R.string.wifi_p2p_disabled_please_enable, Toast.LENGTH_SHORT).show();
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
    deviceLocations.put(myDeviceName, new Locations(myDeviceName));
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
    deviceAddress = wifiP2pDevice.deviceName;
    myDeviceNameTextView.setText(wifiP2pDevice.deviceName);
    myDeviceStatusTextView.setText(getDeviceStatus(wifiP2pDevice.status));

    if (wifiP2pDevice.status == WifiP2pDevice.CONNECTED) {
      sendC();
      // sendS();
    }

  }

  @Override
  public void onGroupHostInfoChanged(WifiP2pInfo wifiP2pInfo) {
    if (wifiP2pInfo != null && wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
      amIHostQuestionTextView.setText(
              getString(R.string.am_i_host_question) + " " + getResources().getString(R.string.yes));
      hostIpTextView.setText(
              getString(R.string.ip_capital_letters) + ": " + wifiP2pInfo.groupOwnerAddress
                      .getHostAddress());
    } else if (wifiP2pInfo != null && wifiP2pInfo.groupFormed) {
      amIHostQuestionTextView.setText(
              getString(R.string.am_i_host_question) + " " + getResources().getString(R.string.no));
      hostIpTextView.setText(
              getString(R.string.ip_capital_letters) + ": " + wifiP2pInfo.groupOwnerAddress
                      .getHostAddress());


    } else {
      amIHostQuestionTextView.setText("");
      hostIpTextView.setText("");
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

//    public void connectSingle(Boolean amihost, InetAddress ip) {
//        Thread handler = null;
//        /*
//         * The group owner accepts connections using a server socket and then spawns a
//         * client socket for every client. This is handled by {@code
//         * GroupOwnerSocketHandler}
//         */
//
//        if (amihost) {
//            Log.d(TAG, "Connected as group owner");
//            try {
//                handler = new GroupOwnerSocketHandler(
//                        ( this).getHandler());
//                handler.start();
//            } catch (IOException e) {
//                Log.d(TAG,
//                        "Failed to create a server thread - " + e.getMessage());
//                return;
//            }
//        } else {
//            Log.d(TAG, "Connected as peer");
//            handler = new ClientSocketHandler(
//                    ( this).getHandler(),
//                    ip);
//            handler.start();
//        }
////        chatFragment = new WifiChatFragment();
////        getFragmentManager().beginTransaction()
////                .replace(R.id.container_root, chatFragment).commit();
////        statusTxtView.setVisibility(View.GONE);
//    }


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

  /* --------------------------------------- Location Requests --------------------------------------- */
  //App permission asks user to use location feature.
  public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
  private final LocationListener locationListener = new LocationListener() {
    @Override
    public void onLocationChanged(Location location) {

      double latitude = location.getLatitude();
      double longitude = location.getLongitude();

      locationGetter.add(0, latitude);
      locationGetter.add(1, longitude);

      deviceLocations.put("Closest", data2);

      //test data
      data2.update("Closest", 42.042246, -111.355112);
      data2.update("Closest", 42.042246, -111.355621);
      deviceLocations.put("Closest", data2);

      data.update(myDeviceName, latitude, longitude);
      deviceLocations.put(myDeviceName, data);

      Timer t = new Timer();
      t.schedule(new TimerTask() {
        public void run() {
          // write the method name here. which you want to call continuously
          new LocationAsyncTask().execute();
        }
      }, 16000, 16000);

      int secs = 5;
      DelayHandler.delay(secs, new DelayHandler.DelayCallback() {
        @Override
        public void afterDelay() {
          locationBasedSelect();
        }
      });
      updateUserStatus(latitude, longitude);

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
  }

  private void updateUserStatus(double latitude, double longitude) {

    double angle = Direction.getBearings(deviceLocations.get(myDeviceName).getPreviousLongitude(),
            deviceLocations.get(myDeviceName).getPreviousLatitude(),
            deviceLocations.get(myDeviceName).getCurrentLongitude(),
            deviceLocations.get(myDeviceName).getCurrentLatitude());
    String heading = Direction.getBearingsString(angle);

    personalLocation
            .setText("Your Cordinates : " + latitude + ",   " + longitude + "\nGoing: " + heading);

  }

  private void locationBasedSelect() {

    String key = "";
    double value = -1;
    double angle;

    for (java.util.Map.Entry<String, Locations> entry : deviceLocations.entrySet()) {
      String k = entry.getKey();
      Locations v = entry.getValue();

      angle = Direction.angleBetweenThreePoints(v.getPreviousLatitude(), v.getPreviousLongitude(),
              v.getCurrentLatitude(), v.getCurrentLongitude(), 42.042246, -111.355621);

      if (value == -1 && value != NaN) {
        key = k;
        value = angle;
      } else if (value > angle && value != NaN) {
        key = k;
        value = angle;
      }
    }
    Toast.makeText(this, "Closest to North: " + key + "\n", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[],
                                         int[] grantResults) {
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
          }

        } else {

          // permission denied, boo! Disable the
          // functionality that depends on this permission.

        }
        return;
      }
    }

  }

  public void sendS() {
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);

    try {
      ServerSocket sersock = new ServerSocket(8080);
//        System.out.println("Server ready........");
      Socket sock = sersock.accept();

      Log.d("SendServer", "Server here waiting");


      OutputStream ostream = sock.getOutputStream();
      BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(ostream));
      String s2 = "Hello From.... " + new java.util.Date();
      bw1.write(s2);

      bw1.close();
      ostream.close();
      sock.close();
      sersock.close();
    } catch (SocketException exception) {

    } catch (IOException exception) {

    }
  }

  public String stringS;

  public void sendC() {
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    try {
      Socket sock = new Socket("192.168.49.1", 8080);

      InputStream istream = sock.getInputStream();
      BufferedReader br1 = new BufferedReader(new InputStreamReader(istream));

      stringS = br1.readLine();
//        System.out.println(s1);
      Log.d("Sendclient", "client here waiting ");

      //show alert box for string received from server

      AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
      myAlert.setMessage(stringS)
              .setPositiveButton("Exit!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              })
              .create();
      myAlert.show();


      br1.close();
      istream.close();
      sock.close();

    } catch (SocketException exception) {

    } catch (IOException exception) {

    }
  }
}
