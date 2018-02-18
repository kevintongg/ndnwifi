package no.bouvet.p2pcommunication.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import java.util.ArrayList;
import java.util.List;
import no.bouvet.p2pcommunication.Client;
import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.Server;
import no.bouvet.p2pcommunication.adapter.DiscoveryListAdapter;
import no.bouvet.p2pcommunication.adapter.P2pCommunicationFragmentPagerAdapter;
import no.bouvet.p2pcommunication.listener.discovery.DiscoveryStateListener;
import no.bouvet.p2pcommunication.listener.invitation.InvitationToConnectListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastListener;
import no.bouvet.p2pcommunication.listener.wifip2p.WifiP2pListener;
import no.bouvet.p2pcommunication.util.button.ConnectionButton;
import no.bouvet.p2pcommunication.util.button.DiscoveryButton;

public class DiscoveryAndConnectionFragment extends ListFragment implements DiscoveryStateListener,
    PeerListListener, InvitationToConnectListener, ConnectionInfoListener {

  public static final String TAG = DiscoveryAndConnectionFragment.class.getSimpleName();
  public static final String SENDER_IP_ADDRESS = "SENDER_IP_ADDRESS";
  public static String clickedDeviceIp;
  public P2PCommunicationActivity p2PCommunicationActivity;
  Unbinder unbinder;
  @BindView(R.id.search_layout)
  RelativeLayout searchLayout;
  @BindView(R.id.no_devices_available_layout)
  RelativeLayout noDevicesAvailableLayout;
  @BindView(R.id.left_bottom_button)
  DiscoveryButton leftBottomButton;
  @BindView(R.id.right_bottom_button)
  ConnectionButton rightBottomButton;
  private boolean viewsInjected;
  private DiscoveryListAdapter discoveryListAdapter;
  private WifiP2pListener wifiP2pListener;
  private MulticastListener multicastListener;
  private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

  public static Fragment newInstance() {
    DiscoveryAndConnectionFragment discoveryAndConnectionFragment = new DiscoveryAndConnectionFragment();
    discoveryAndConnectionFragment.setArguments(getFragmentArguments());
    return discoveryAndConnectionFragment;
  }

  private static Bundle getFragmentArguments() {
    Bundle fragmentArguments = new Bundle();
    fragmentArguments
        .putString(P2pCommunicationFragmentPagerAdapter.FRAGMENT_TITLE, "AVAILABLE DEVICES");
    return fragmentArguments;
  }

  @Override
  public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
      Bundle savedInstanceState) {
    View discoveryAndConnectionFragmentView = layoutInflater
        .inflate(R.layout.discovery_and_connection_fragment, container, false);
    unbinder = ButterKnife.bind(this, discoveryAndConnectionFragmentView);
    viewsInjected = true;
    return discoveryAndConnectionFragmentView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    wifiP2pListener = (WifiP2pListener) getActivity();
    multicastListener = (MulticastListener) getActivity();
    discoveryListAdapter = new DiscoveryListAdapter(getActivity(),
        R.layout.discovery_and_connection_fragment_list_row);
    setListAdapter(discoveryListAdapter);
    // midBottomButton.initialize(wifiP2pListener);
    leftBottomButton.initialize(wifiP2pListener);
    rightBottomButton.initialize(wifiP2pListener);
  }

  @Override
  public void onListItemClick(ListView listView, View view, int position, long id) {
    super.onListItemClick(listView, view, position, id);
    WifiP2pDevice wifiP2pDevice = discoveryListAdapter.getItem(position);

    if (wifiP2pDevice.status == WifiP2pDevice.CONNECTED) {

//
//            Intent intent = new Intent(getActivity(), FileServer.class);
//            startActivity(intent);

//            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
//            builder1.setMessage("Choose one");
//            builder1.setCancelable(true);
//
//
//
//
//
//            builder1.setPositiveButton(
//                    "Create Chat Room",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//
//
//                            Intent intent = new Intent(getActivity(), Server.class);
//                  startActivity(intent);
//                        }
//                    });
//
//            builder1.setNegativeButton(
//                    "Join Chat Room",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//
//
//                            Intent intent = new Intent(getActivity(), Client.class);
//            startActivity(intent);
//                        }
//                    });
//
//            builder1.setNeutralButton(
//                    "Upload File",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//
//
//                            Intent intent = new Intent(getActivity(), FileServer.class);
//                            startActivity(intent);
//                        }
//                    });
//
//
//            AlertDialog alert11 = builder1.create();
//            alert11.show();

      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle("Choose 1");
      builder.setItems(
          new CharSequence[]{"Create chat room", "Join chat room", "Upload file", "Download File"},
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

              switch (which) {
                case 0:
                  Intent intent = new Intent(getActivity(), Server.class);
                  startActivity(intent);
                  break;
                case 1:
                  Intent intent2 = new Intent(getActivity(), Client.class);
                  startActivity(intent2);
                  break;
//                                     case 2:
//                                         Intent intent3 = new Intent(getActivity(), FileServer.class);
//                                         startActivity(intent3);
//                                         break;
//                                     case 3:
//                                         Intent intent4 = new Intent(getActivity(), FileClient.class);
//                                         startActivity(intent4);
//                                         break;
              }
            }
          });
      builder.create().show();

//            try {
//                privateMessage(current.deviceName, wifiP2pDevice.deviceName);
//                MulticastSocket multicastSocket = createMulticastSocket();
//                String messageToBeSent =  wifiP2pDevice.deviceName;
//
//                DatagramPacket datagramPacket = new DatagramPacket(messageToBeSent.getBytes(), messageToBeSent.length(), getMulticastGroupAddress(), getPort());
//                multicastSocket.send(datagramPacket);
//
//                String senderIpAddress = "";
//
//                try{
//                    InetAddress inetAddress = InetAddress.getByName("");
//                    //start socket
//                    Log.d(TAG, "Server");
//                    Log.d(TAG, "Connected as peer");
//                    p2PCommunicationActivity.connectSingle(true, inetAddress);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                Log.d(TAG, "sent");
//
//            } catch (IOException ioException) {
//                Log.e(TAG, ioException.toString());
//            }
    } else {
      wifiP2pListener.onConnect(wifiP2pDevice);
      clickedDeviceIp = wifiP2pDevice.deviceAddress;

      Log.i(TAG, wifiP2pDevice.deviceAddress);
    }

  }

  @Override
  public void onStartedDiscovery() {
    clearDiscoveryList();
    searchLayout.setVisibility(View.VISIBLE);
    noDevicesAvailableLayout.setVisibility(View.GONE);
    leftBottomButton.setStateStopDiscovery();
  }

  @Override
  public void onStoppedDiscovery() {
    searchLayout.setVisibility(View.GONE);
    leftBottomButton.setStateStartDiscovery();
  }

  @Override
  public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
    clearDiscoveryList();
    addAllDiscoveredDevicesToDiscoveryList(wifiP2pDeviceList);
    if (discoveryListAdapter.isEmpty()) {
      noDevicesAvailableLayout.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onSentInvitationToConnect() {
    rightBottomButton.setStateCancelInvitation();
  }

  @Override
  public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
    wifiP2pListener.onStopPeerDiscovery();
    wifiP2pListener.onGroupHostInfoChanged(wifiP2pInfo);
    multicastListener.onStartReceivingMulticastMessages();
    rightBottomButton.setStateDisconnect();
  }

  public void reset() {
    if (viewsInjected) {
      rightBottomButton.setStateCreateGroup();
      Log.i(TAG, getString(R.string.has_been_reset));
    }
  }

  private void addAllDiscoveredDevicesToDiscoveryList(WifiP2pDeviceList wifiP2pDeviceList) {
    discoveryListAdapter.addAll(wifiP2pDeviceList.getDeviceList());
    discoveryListAdapter.notifyDataSetChanged();
  }

  private void clearDiscoveryList() {
    if (discoveryListAdapter != null) {
      discoveryListAdapter.clear();
      discoveryListAdapter.notifyDataSetChanged();
    }
  }

}
