package no.bouvet.p2pcommunication.fragment;


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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.adapter.DiscoveryListAdapter;
import no.bouvet.p2pcommunication.adapter.P2PCommunicationFragmentPagerAdapter;
import no.bouvet.p2pcommunication.listener.discovery.DiscoveryStateListener;
import no.bouvet.p2pcommunication.listener.invitation.InvitationToConnectListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastListener;
import no.bouvet.p2pcommunication.listener.wifip2p.WifiP2PListener;
import no.bouvet.p2pcommunication.util.button.ConnectionButton;
import no.bouvet.p2pcommunication.util.button.DiscoveryButton;

//import no.bouvet.p2pcommunication.Server;

public class DiscoveryAndConnectionFragment extends ListFragment implements DiscoveryStateListener,
    PeerListListener, InvitationToConnectListener, ConnectionInfoListener {

  public static String stringS;
  private static final String TAG = DiscoveryAndConnectionFragment.class.getSimpleName();
  public static final String SENDER_IP_ADDRESS = "SENDER_IP_ADDRESS";
  public P2PCommunicationActivity p2PCommunicationActivity;
  private Unbinder unbinder;
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
  private WifiP2PListener wifiP2PListener;
  private MulticastListener multicastListener;
  private List<WifiP2pDevice> peers = new ArrayList<>();

  public static Fragment newInstance() {
    DiscoveryAndConnectionFragment discoveryAndConnectionFragment = new DiscoveryAndConnectionFragment();
    discoveryAndConnectionFragment.setArguments(getFragmentArguments());
    return discoveryAndConnectionFragment;
  }

  private static Bundle getFragmentArguments() {
    Bundle fragmentArguments = new Bundle();
    fragmentArguments
        .putString(P2PCommunicationFragmentPagerAdapter.FRAGMENT_TITLE, "AVAILABLE DEVICES");
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

    wifiP2PListener = (WifiP2PListener) getActivity();
    multicastListener = (MulticastListener) getActivity();
    discoveryListAdapter = new DiscoveryListAdapter(getActivity(),
        R.layout.discovery_and_connection_fragment_list_row);
    setListAdapter(discoveryListAdapter);
    leftBottomButton.initialize(wifiP2PListener);
    rightBottomButton.initialize(wifiP2PListener);
  }

  private ArrayList<Node> listNote;

  @Override
  public void onListItemClick(ListView listView, View view, int position, long id) {
    super.onListItemClick(listView, view, position, id);
    WifiP2pDevice wifiP2pDevice = discoveryListAdapter.getItem(position);

//client
    if (Objects.requireNonNull(wifiP2pDevice).status == WifiP2pDevice.CONNECTED) {
      String ipClinet = "";
      listNote = new ArrayList<>();
      readAddresses();
      for (int i = 0; i < listNote.size(); i++) {
        ipClinet = listNote.get(i).toString();
      }
      Log.d("disco", ipClinet);
    } else {
      wifiP2PListener.onConnect(wifiP2pDevice);
      String clickedDeviceIp = wifiP2pDevice.deviceAddress;

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
        if (splitted.length >= 4) {
          String ip = splitted[0];
          String mac = splitted[3];
          if (mac.matches("..:..:..:..:..:..")) {
            Log.d("----TEST", ip + " || " + mac);
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
        Objects.requireNonNull(bufferedReader).close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  class Node {
    final String ip;

    Node(String ip) {
      this.ip = ip;
    }

    @Override
    public String toString() {
      return ip;
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
    wifiP2PListener.onStopPeerDiscovery();
    wifiP2PListener.onGroupHostInfoChanged(wifiP2pInfo);
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