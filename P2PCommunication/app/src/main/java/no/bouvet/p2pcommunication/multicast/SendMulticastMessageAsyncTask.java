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

public class SendMulticastMessageAsyncTask extends AsyncTask<Void, String, Boolean> {

    public static final String TAG = SendMulticastMessageAsyncTask.class.getSimpleName();
    private MulticastMessageSentListener multicastMessageSentListener;
    private UserInputHandler userInputHandler;


    public SendMulticastMessageAsyncTask(MulticastMessageSentListener multicastMessageSentListener, UserInputHandler userInputHandler) {
        this.multicastMessageSentListener = multicastMessageSentListener;
        this.userInputHandler = userInputHandler;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success = false;
        try {
            MulticastSocket multicastSocket = createMulticastSocket();
            String messageToBeSent = userInputHandler.getMessageToBeSentFromUserInput();
            DatagramPacket datagramPacket = new DatagramPacket(messageToBeSent.getBytes(), messageToBeSent.length(), getMulticastGroupAddress(), getPort());
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
            multicastMessageSentListener.onCouldNotSendMessage();
        }
        userInputHandler.clearUserInput();
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


}
