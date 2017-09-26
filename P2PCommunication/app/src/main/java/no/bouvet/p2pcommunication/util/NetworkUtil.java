package no.bouvet.p2pcommunication.util;

import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetworkUtil {

    private static final String TAG = NetworkUtil.class.getSimpleName();
    private static final String MULTICAST_GROUP_IP = "239.255.1.1";
    private static final String NETWORK_INTERFACE_NAME = "p2p-wlan0-0";
    private static final String ALTERNATE_NETWORK_INTERFACE_NAME = "p2p-p2p0";
    private static final int PORT = 40000;

    public static int getPort() {
        return PORT;
    }

    public static InetAddress getMulticastGroupAddress() throws UnknownHostException {
       return InetAddress.getByName(MULTICAST_GROUP_IP);
    }

    public static NetworkInterface getWifiP2pNetworkInterface() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
            if (isWifiP2pInterface(networkInterface)) {
                return networkInterface;
            }
        }
        return null;
    }

    public static String getMyWifiP2pIpAddress() {
        try {
            Enumeration<InetAddress> inetAddressEnumeration = getWifiP2pNetworkInterface().getInetAddresses();
            while (inetAddressEnumeration.hasMoreElements()) {
                InetAddress inetAddress = inetAddressEnumeration.nextElement();
                if (isIpv4Address(inetAddress)) {
                    return inetAddress.getHostAddress().toString();
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    private static boolean isWifiP2pInterface(NetworkInterface networkInterface) throws SocketException {
        return networkInterface.isUp() && (networkInterface.getDisplayName().equals(NETWORK_INTERFACE_NAME) || networkInterface.getDisplayName().contains(ALTERNATE_NETWORK_INTERFACE_NAME));
    }

    private static boolean isIpv4Address(InetAddress inetAddress) {
        return !inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress());
    }


}
