package no.bouvet.p2pcommunication.locationSocket;

import android.os.Handler;
import android.os.Message;

import java.nio.ByteBuffer;

import no.bouvet.p2pcommunication.listener.multicast.MulticastMessageReceivedListener;
import no.bouvet.p2pcommunication.util.NetworkUtil;

/**
 * Created by micha on 10/17/2017.
 */

public class LocationReceivedHandler extends Handler {


    public static final String RECEIVED_TEXT = "";
    public static final String SENDER_IP_ADDRESS = "SENDER_IP_ADDRESS";
    private MulticastMessageReceivedListener multicastMessageReceivedListener;

    public LocationReceivedHandler(MulticastMessageReceivedListener multicastMessageReceivedListener) {
        this.multicastMessageReceivedListener = multicastMessageReceivedListener;
    }

    @Override
    public void handleMessage(Message messageFromMulticastMessageReceiverService) {
        LocationTask multicastMessage = createMulticastMessage(messageFromMulticastMessageReceiverService);
       // multicastMessageReceivedListener.onMulticastMessageReceived(multicastMessage);
    }

    private LocationTask createMulticastMessage(Message messageFromMulticastMessageReceiverService) {
        byte[] receivedText = messageFromMulticastMessageReceiverService.getData().getByteArray(RECEIVED_TEXT);
        double[] otherLocation = byteToDouble(receivedText);
        String senderIpAddress = getSenderIpAddress(messageFromMulticastMessageReceiverService);
        LocationTask multicastMessage = new LocationTask(otherLocation, senderIpAddress);
        if (senderIpAddress.equals(NetworkUtil.getMyWifiP2pIpAddress())) {
            multicastMessage.setSentByMe(true);
        }
        return multicastMessage;
    }

    private String getSenderIpAddress(Message messageFromReceiverService) {
        return messageFromReceiverService.getData().getString(SENDER_IP_ADDRESS);
    }

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
