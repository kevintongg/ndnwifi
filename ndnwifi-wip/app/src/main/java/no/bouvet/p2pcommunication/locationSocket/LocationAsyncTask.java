package no.bouvet.p2pcommunication.locationSocket;

import android.os.AsyncTask;
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
import no.bouvet.p2pcommunication.util.NetworkUtil;

/**
 * Created by micha on 10/17/2017.
 */

//This AsyncTask creates a data packet for the location to be send to everyone in the wifi-direct group.
public class LocationAsyncTask extends AsyncTask<Void, String, Boolean> {

  public static final String TAG = LocationAsyncTask.class.getSimpleName();
  byte[] buffer = new byte[1024];

  @Override
  protected Boolean doInBackground(Void... params) {
    boolean success = false;
    try {
      MulticastSocket multicastSocket = createMulticastSocket();
      double[] mLocationTest = new double[2];

      mLocationTest[0] = P2PCommunicationActivity.locationGetter.get(0);
      mLocationTest[1] = P2PCommunicationActivity.locationGetter.get(1);

      ByteBuffer bb = ByteBuffer.allocate(mLocationTest.length * 8);
      for (double d : mLocationTest) {
        bb.putDouble(d);
      }
      byte[] byteArray = bb.array();
      DatagramPacket datagramPacket = new DatagramPacket(byteArray, byteArray.length,
          getMulticastGroupAddress(), getPort());
      multicastSocket.send(datagramPacket);
      //Log.d(TAG, "called");

      success = true;
    } catch (IOException ioException) {
      // Log.e(TAG, ioException.toString());
    }
    return success;
  }

  @Override
  protected void onPostExecute(Boolean success) {
    if (!success) {

    }
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

  private double[] byteToDouble(byte[] bytearray) {
    ByteBuffer bb = ByteBuffer.wrap(bytearray);
    int length = bytearray.length / 8;
    double[] doubles = new double[length];
    for (int i = 0; i < doubles.length; i++) {
      doubles[i] = bb.getDouble();
    }

    return doubles;
  }

  private DatagramPacket createDatagramPacket() {
    return new DatagramPacket(buffer, buffer.length);
  }

}
