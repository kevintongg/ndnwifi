package no.bouvet.p2pcommunication.multicast;

import static no.bouvet.p2pcommunication.P2PCommunicationActivity.deviceList;
import static no.bouvet.p2pcommunication.P2PCommunicationActivity.deviceLocations;
import static no.bouvet.p2pcommunication.P2PCommunicationActivity.myDeviceName;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
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
import no.bouvet.p2pcommunication.locationSocket.Locations;
import no.bouvet.p2pcommunication.util.NetworkUtil;


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

              byte[] byteDate = datagramPacket.getData();

              String test = getFirstLetter(datagramPacket);

              /* In order to determine what type of data packet was sent every chat message starts with ':', this condition checks for this and determines whether the packet is
              being sent for location or chat. */
              if (!(test.contains(":"))) {

                  getLocation(byteDate, datagramPacket);

              }else {
                  sendReceivedDataToMulticastMessageReceivedHandler(getHandlerMessenger(intent), datagramPacket);
              }
          }
      }catch (IOException | RemoteException e) {
          Log.e(TAG, e.toString());
      }
      }
  }

  @Override
  public void onDestroy() {
    isRunning = false;
    super.onDestroy();
  }

  private void sendReceivedDataToMulticastMessageReceivedHandler(Messenger handlerMessenger,
      DatagramPacket datagramPacket) throws RemoteException {
    Message handlerMessage = createHandlerMessage(getReceivedText(datagramPacket),
        getSenderIpAddress(datagramPacket));
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

    return datagramPacket.getAddress().getHostAddress();
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
    multicastSocket.joinGroup(new InetSocketAddress(getMulticastGroupAddress(), getPort()),
        getNetworkInterface());
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
  private double[] byteToDouble(byte[] bytearray) {
    ByteBuffer bb = ByteBuffer.wrap(bytearray);
    int length = bytearray.length / 8;
    double[] doubles = new double[length];
    for (int i = 0; i < doubles.length; i++) {
      doubles[i] = bb.getDouble();
    }

    return doubles;
  }


    private void getLocation(byte[] byteDate, DatagramPacket datagramPacket){
        othersLocation = byteToDouble(byteDate);
        String ip2Name = getSenderName(datagramPacket);



        if (!ip2Name.equals(NetworkUtil.getMyWifiP2pIpAddress())) {
            //String temp = ip2Name;


            //ip2Name = deviceList.get(ip2Name).getDeviceName();

            Log.d("--------TEST: ", ip2Name);

            //Contains Name
            if (deviceLocations.containsKey(ip2Name)) {


                deviceLocations.get(ip2Name).update(ip2Name,
                        othersLocation[0],
                        othersLocation[1]);

                //Does not contain name
            }else if (!deviceLocations.containsKey(ip2Name)) {
                //deviceList.get(ip2Name).setIp(temp);

                Locations data = new Locations(ip2Name,
                        othersLocation[0],
                        othersLocation[1]);

                data.update(ip2Name, othersLocation[0], othersLocation[1]);

                deviceLocations.put(ip2Name, data);

                deviceLocations.get(ip2Name).setDistance(othersLocation[0], othersLocation[1],
                        deviceLocations.get(myDeviceName).getCurrentLatitude(),
                        deviceLocations.get(myDeviceName).getCurrentLongitude());

            }
        }
    }


}