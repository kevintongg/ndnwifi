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

import butterknife.ButterKnife;
import butterknife.InjectView;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.adapter.DiscoveryListAdapter;
import no.bouvet.p2pcommunication.adapter.P2pCommunicationFragmentPagerAdapter;
import no.bouvet.p2pcommunication.listener.discovery.DiscoveryStateListener;
import no.bouvet.p2pcommunication.listener.invitation.InvitationToConnectListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastListener;
import no.bouvet.p2pcommunication.listener.wifip2p.WifiP2pListener;
import no.bouvet.p2pcommunication.util.button.ConnectionButton;
import no.bouvet.p2pcommunication.util.button.DiscoveryButton;

public class DiscoveryAndConnectionFragment extends ListFragment implements DiscoveryStateListener, PeerListListener, InvitationToConnectListener, ConnectionInfoListener {

    public static final String TAG = DiscoveryAndConnectionFragment.class.getSimpleName();
    private boolean viewsInjected;
    private DiscoveryListAdapter discoveryListAdapter;
    private WifiP2pListener wifiP2pListener;
    private MulticastListener multicastListener;

    @InjectView(R.id.search_layout) RelativeLayout searchLayout;
    @InjectView(R.id.no_devices_available_layout) RelativeLayout noDevicesAvailableLayout;
    @InjectView(R.id.left_bottom_button) DiscoveryButton leftBottomButton;
    @InjectView(R.id.right_bottom_button) ConnectionButton rightBottomButton;

    public static Fragment newInstance() {
        DiscoveryAndConnectionFragment discoveryAndConnectionFragment = new DiscoveryAndConnectionFragment();
        discoveryAndConnectionFragment.setArguments(getFragmentArguments());
        return discoveryAndConnectionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View discoveryAndConnectionFragmentView = layoutInflater.inflate(R.layout.discovery_and_connection_fragment, container, false);
        ButterKnife.inject(this, discoveryAndConnectionFragmentView);
        viewsInjected = true;
        return discoveryAndConnectionFragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        wifiP2pListener = (WifiP2pListener) getActivity();
        multicastListener = (MulticastListener) getActivity();
        discoveryListAdapter = new DiscoveryListAdapter(getActivity(), R.layout.discovery_and_connection_fragment_list_row);
        setListAdapter(discoveryListAdapter);
        leftBottomButton.initialize(wifiP2pListener);
        rightBottomButton.initialize(wifiP2pListener);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        WifiP2pDevice wifiP2pDevice = discoveryListAdapter.getItem(position);
        wifiP2pListener.onConnect(wifiP2pDevice);
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

    private static Bundle getFragmentArguments() {
        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putString(P2pCommunicationFragmentPagerAdapter.FRAGMENT_TITLE, "AVAILABLE DEVICES");
        return fragmentArguments;
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
