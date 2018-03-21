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
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.w3c.dom.Node;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
//import no.bouvet.p2pcommunication.Client;
import no.bouvet.p2pcommunication.ClientSocketAuto;
import no.bouvet.p2pcommunication.FileClient;
import no.bouvet.p2pcommunication.FileServer;
import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.SendClient;
import no.bouvet.p2pcommunication.SendServer;
//import no.bouvet.p2pcommunication.Server;
import no.bouvet.p2pcommunication.adapter.DiscoveryListAdapter;
import no.bouvet.p2pcommunication.adapter.P2pCommunicationFragmentPagerAdapter;
import no.bouvet.p2pcommunication.deviceList.Device;
import no.bouvet.p2pcommunication.listener.discovery.DiscoveryStateListener;
import no.bouvet.p2pcommunication.listener.invitation.InvitationToConnectListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastListener;
import no.bouvet.p2pcommunication.listener.wifip2p.WifiP2pListener;
import no.bouvet.p2pcommunication.util.button.ConnectionButton;
import no.bouvet.p2pcommunication.util.button.DiscoveryButton;

//import static no.bouvet.p2pcommunication.Server.ServerAdd;
//import static no.bouvet.p2pcommunication.Server.editTextAddressServer;

public class DiscoveryAndConnectionFragment extends ListFragment implements DiscoveryStateListener,
    PeerListListener, InvitationToConnectListener, ConnectionInfoListener {

    public static String stringS ;
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
    ArrayList<Node> listNote;

  @Override
  public void onListItemClick(ListView listView, View view, int position, long id) {
    super.onListItemClick(listView, view, position, id);
    WifiP2pDevice wifiP2pDevice = discoveryListAdapter.getItem(position);


//client
    if (wifiP2pDevice.status == WifiP2pDevice.CONNECTED) {


        String ipClinet ="" ;

        listNote = new ArrayList<>();
        readAddresses();

        for (int i = 0; i < listNote.size(); i++) {
            ipClinet = listNote.get(i).toString();

        }

        Log.d("disco", ipClinet);

        ClientSocketAuto run = new ClientSocketAuto();
        run.onCreate();


//        FileClient run = new FileClient();
//        run.onCreate();



//      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//      builder.setTitle("Choose one");
//      builder.setItems(
////           new CharSequence[]{"Create chat room", "Join chat room", "Upload file", "Download File"},
//                    new CharSequence[]{"Create chat room", "Join chat room"},
//          new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//
//              switch (which) {
//                case 0:
//                  Intent intent = new Intent(getActivity(), Server.class);
//                  startActivity(intent);
//                  break;
//                case 1:
//                  Intent intent2 = new Intent(getActivity(), Client.class);
//                  startActivity(intent2);
//                  break;
////                                     case 2:
////                                         Intent intent3 = new Intent(getActivity(), FileServer.class);
////                                         startActivity(intent3);
////                                         break;
////                                     case 3:
////                                         Intent intent4 = new Intent(getActivity(), FileClient.class);
////                                         startActivity(intent4);
////                                         break;
//              }
//            }
//          });
//      builder.create().show();

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
    private void readAddresses() {
        listNote.clear();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        Node thisNode = new Node(ip);
                        listNote.add(thisNode);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class Node {

        String ip;


        Node(String ip) {
            this.ip = ip;
        }

        @Override
        public String toString() {
            return ip;
        }
    }

//    public void sendS() {
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        try {
//            ServerSocket sersock = new ServerSocket(7777);
////        System.out.println("Server ready........");
//            Socket sock = sersock.accept();
//
//            Log.d("SendServer", "Server here waiting");
//
//
//            OutputStream ostream = sock.getOutputStream();
//            BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(ostream));
//            String s2 = "Click ok to join the chat " + new java.util.Date();
//            bw1.write(s2);
//
////            bw1.close();
////            ostream.close();
////            sock.close();
////            sersock.close();
//
//            if(sock.getInputStream().read() == -1){
//                Intent intent = new Intent(getActivity(), Server.class);
//                startActivity(intent);
//            }
//
//
//
//        } catch (SocketException exception) {
//
//        } catch (IOException exception) {
//
//        }
//    }

    public void sendC(String ip) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Socket sock = new Socket(ip, 7777);

            InputStream istream = sock.getInputStream();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(istream));

            stringS = br1.readLine();
//        System.out.println(s1);
            Log.d("Sendclient", "client here waiting ");

            //show alert box for string received from server

            AlertDialog.Builder myAlert = new AlertDialog.Builder(getActivity());
//            myAlert.setMessage(stringS)
//                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(getActivity(), Client.class);
//                            startActivity(intent);
//                        }
//                    })
//                    .create();
            myAlert.show();


            br1.close();
            istream.close();
            sock.close();

        } catch (SocketException exception) {

        } catch (IOException exception) {

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
