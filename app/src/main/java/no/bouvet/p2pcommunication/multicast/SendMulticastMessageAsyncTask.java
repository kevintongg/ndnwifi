package no.bouvet.p2pcommunication.multicast;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import no.bouvet.p2pcommunication.listener.multicast.MulticastMessageSentListener;
import no.bouvet.p2pcommunication.util.NetworkUtil;
import no.bouvet.p2pcommunication.util.UserInputHandler;

/*
    This java file handles the text the user is going to sendS in the multicast chat. Once the sendS button is clicked from communicationFragment.java
    asynctask is called. In the background, a multicast socket gets created and sends a data packet thru the socket for anyone in the network to receive.
 */
public class SendMulticastMessageAsyncTask extends AsyncTask<Void, String, Boolean> {

  private static final String TAG = SendMulticastMessageAsyncTask.class.getSimpleName();
  private final MulticastMessageSentListener MULTICAST_MESSAGE_SENT_LISTENER;
  private final UserInputHandler USER_INPUT_HANDLER;

  public SendMulticastMessageAsyncTask(MulticastMessageSentListener MULTICAST_MESSAGE_SENT_LISTENER,
      UserInputHandler USER_INPUT_HANDLER) {
    this.MULTICAST_MESSAGE_SENT_LISTENER = MULTICAST_MESSAGE_SENT_LISTENER;
    this.USER_INPUT_HANDLER = USER_INPUT_HANDLER;
  }

  @Override
  protected Boolean doInBackground(Void... params) {
    boolean success = false;
    try {
      MulticastSocket multicastSocket = createMulticastSocket();
      //  String messageToBeSent = USER_INPUT_HANDLER.getMessageToBeSentFromUserInput() + "\n Latitude" + P2PCommunicationActivity.locationGetter.get(0) + ", Longitude" + P2PCommunicationActivity.locationGetter.get(1);
      String messageToBeSent = ":" + USER_INPUT_HANDLER.getMessageToBeSentFromUserInput();

      DatagramPacket datagramPacket = new DatagramPacket(messageToBeSent.getBytes(),
          messageToBeSent.length(), getMulticastGroupAddress(), getPort());
      multicastSocket.send(datagramPacket);
      success = true;
    } catch (IOException ioException) {
      Log.e(TAG, ioException.toString());
    }
    return success;
  }

  @Override
  protected void onPostExecute(Boolean success) {
    if (!success) {
      MULTICAST_MESSAGE_SENT_LISTENER.onCouldNotSendMessage();
    }
    USER_INPUT_HANDLER.clearUserInput();
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
}
