package no.bouvet.p2pcommunication.multicast;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.locationSocket.Direction;
import no.bouvet.p2pcommunication.locationSocket.Locations;
import no.bouvet.p2pcommunication.util.NetworkUtil;

import static no.bouvet.p2pcommunication.P2PCommunicationActivity.deviceAddress;
import static no.bouvet.p2pcommunication.P2PCommunicationActivity.deviceLocations;


/*
    This java file creates a multicast socket and receives any incoming data packets.
 */

public class MulticastMessageReceiverService extends IntentService {

    public static final String TAG = MulticastMessageReceiverService.class.getSimpleName();
    public static final String ACTION_LISTEN_FOR_MULTICAST = "ACTION_LISTEN_FOR_MULTICAST";
    public static final String EXTRA_HANDLER_MESSENGER = "EXTRA_HANDLER_MESSENGER";
    public static boolean isRunning = false;
    public static double[] othersLocation = new double[4];


    public MulticastMessageReceiverService() {
        super(MulticastMessageReceiverService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        if (action.equals(ACTION_LISTEN_FOR_MULTICAST)) {
            isRunning = true;
            try {
                MulticastSocket multicastSocket = createMulticastSocket();
                while (isRunning) {
                    DatagramPacket datagramPacket = createDatagramPacket();
                    multicastSocket.receive(datagramPacket);

                    byte[] bb = datagramPacket.getData();
                    String test = getFirstLetter(datagramPacket);


                    //In order to determine what type of data packet was sent every chat message starts with ':', this condition checks for this and determines whether the packet is
                    //being sent for location or chat.
                    if(!(test.contains(":"))) {

                        othersLocation = byteToDouble(bb);
                        String ip = getSenderName(datagramPacket);

                       // if(ip != NetworkUtil.getMyWifiP2pIpAddress()) {
                            //Log.d(TAG, "This ip " + NetworkUtil.getMyWifiP2pIpAddress());

                            if (deviceLocations.containsKey(ip)){

                                deviceLocations.get(ip).update(ip, othersLocation[0], othersLocation[1]);

                            } else if (!deviceLocations.containsKey(ip)) {

                                Locations data = new Locations(ip, othersLocation[0], othersLocation[1]);
                                data.update(ip, othersLocation[0], othersLocation[1]);
                                deviceLocations.put(getSenderName(datagramPacket), data);

                            }

                           // Log.d(TAG, "Location: " + ip);
                     //   }
                    }else {
                        sendReceivedDataToMulticastMessageReceivedHandler(getHandlerMessenger(intent), datagramPacket);
                    }
                }
            } catch (IOException | RemoteException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    private void sendReceivedDataToMulticastMessageReceivedHandler(Messenger handlerMessenger, DatagramPacket datagramPacket) throws RemoteException {
        Message handlerMessage = createHandlerMessage(getReceivedText(datagramPacket), getSenderIpAddress(datagramPacket));
        handlerMessenger.send(handlerMessage);
    }

    private Message createHandlerMessage(String receivedMessage, String senderIpAddress) {
        Bundle receivedData = new Bundle();
        receivedData.putString(MulticastMessageReceivedHandler.RECEIVED_TEXT, receivedMessage);
        receivedData.putString(MulticastMessageReceivedHandler.SENDER_IP_ADDRESS, senderIpAddress);
        Message handlerMessage = new Message();
        handlerMessage.setData(receivedData);
        return handlerMessage;
    }

    private Messenger getHandlerMessenger(Intent intent) {
        return (Messenger) intent.getExtras().get(EXTRA_HANDLER_MESSENGER);
    }

    private String getSenderIpAddress(DatagramPacket datagramPacket) {
        return datagramPacket.getAddress().getHostAddress();
    }

    private String getSenderName(DatagramPacket datagramPacket) {

        return datagramPacket.getAddress().getHostName();
    }

    private String getReceivedText(DatagramPacket datagramPacket) {
        return new String(datagramPacket.getData(), 1, datagramPacket.getLength());
    }

    private String getFirstLetter(DatagramPacket datagramPacket) {
        return new String(datagramPacket.getData(), 0, 1);
    }

    private MulticastSocket createMulticastSocket() throws IOException {
        MulticastSocket multicastSocket = new MulticastSocket(getPort());
        multicastSocket.setNetworkInterface(getNetworkInterface());
        multicastSocket.joinGroup(new InetSocketAddress(getMulticastGroupAddress(), getPort()), getNetworkInterface());
        return multicastSocket;
    }

    private NetworkInterface getNetworkInterface() throws SocketException {
        return NetworkUtil.getWifiP2pNetworkInterface();
    }

    private InetAddress getMulticastGroupAddress() throws UnknownHostException {
        return NetworkUtil.getMulticastGroupAddress();
    }

    private int getPort() {
        return NetworkUtil.getPort();
    }

    private DatagramPacket createDatagramPacket() {
        byte[] buffer = new byte[1024];
        return new DatagramPacket(buffer, buffer.length);
    }

    //Changes the byte to a double array if datapacket is being used for Location updates.
    private double[] byteToDouble(byte[] bytearray){
        ByteBuffer bb = ByteBuffer.wrap(bytearray);
        int length = bytearray.length / 8;
        double[] doubles = new double[length];
        for(int i = 0; i < doubles.length; i++) {
            doubles[i] = bb.getDouble();
        }

        return doubles;
    }

}