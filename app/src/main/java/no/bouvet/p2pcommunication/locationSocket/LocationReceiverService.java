//package no.bouvet.p2pcommunication.locationSocket;
//
//import android.app.IntentService;
//import android.content.Intent;
//import android.util.Log;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.MulticastSocket;
//import java.net.NetworkInterface;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//import java.nio.ByteBuffer;
//
//import no.bouvet.p2pcommunication.util.NetworkUtil;
//
///**
// * Created by micha on 10/17/2017.
// */
//
//
///**
// * TESTING PURPOSES:
// * Not needed.
// */
//public class LocationReceiverService  extends IntentService{
//
//    public static final String TAG = LocationReceiverService.class.getSimpleName();
//    public static boolean isRunning = false;
//    public static double[] othersLocation = new double[4];
//    byte[] buffer = new byte[1024];
//
//    public LocationReceiverService() {
//        super(LocationReceiverService.class.getSimpleName());
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//
//            try {
//                MulticastSocket multicastSocket = createMulticastSocket();
//                while (isRunning) {
//                    DatagramPacket datagramPacket = createDatagramPacket();
//                    multicastSocket.receive(datagramPacket);
//
//
//                    byte[] bb = datagramPacket.getData();
//                    othersLocation = byteToDouble(bb);
//                    Log.d(TAG, "Location in Bytes: " + othersLocation[0]);
//                }
//            } catch (IOException e) {
//                Log.e(TAG, e.toString());
//            }
//    }
//
//    @Override
//    public void onDestroy() {
//        isRunning = false;
//        super.onDestroy();
//    }
//
//    private String getSenderIpAddress(DatagramPacket datagramPacket) {
//        return datagramPacket.getAddress().getHostAddress();
//    }
//
//
//    private MulticastSocket createMulticastSocket() throws IOException {
//        MulticastSocket multicastSocket = new MulticastSocket(getPort());
//        multicastSocket.setNetworkInterface(getNetworkInterface());
//        multicastSocket.joinGroup(new InetSocketAddress(getMulticastGroupAddress(), getPort()), getNetworkInterface());
//        return multicastSocket;
//    }
//
//    private NetworkInterface getNetworkInterface() throws SocketException {
//        return NetworkUtil.getWifiP2pNetworkInterface();
//    }
//
//    private InetAddress getMulticastGroupAddress() throws UnknownHostException {
//        return NetworkUtil.getMulticastGroupAddress();
//    }
//
//    private int getPort() {
//        return NetworkUtil.getPort();
//    }
//
//    private DatagramPacket createDatagramPacket() {
//        return new DatagramPacket(buffer, buffer.length);
//    }
//
//    private double[] byteToDouble(byte[] bytearray){
//        ByteBuffer bb = ByteBuffer.wrap(bytearray);
//        int length = bytearray.length / 8;
//        double[] doubles = new double[length];
//        for(int i = 0; i < doubles.length; i++) {
//            doubles[i] = bb.getDouble();
//        }
//
//        return doubles;
//    }
//}
