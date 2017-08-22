package com.example.android.wifidirect.connectivity;

import android.util.Log;

import com.example.android.wifidirect.DeviceListFragment;
import com.example.android.wifidirect.group.GroupIndividual;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tj on 1/31/16.
 */
public class SocketClientThread extends Thread{
    private final int port;
    public GroupIndividual groupIndividual;
    private final String ipAddress;
    private Socket socket ;
    public ConnectedThread connectedThread;
    private static final String TAG = "socketclientthread";

    public SocketClientThread(SocketClientThread otherSocketClientThread){
        this(otherSocketClientThread.groupIndividual,
                otherSocketClientThread.ipAddress, otherSocketClientThread.port);
    }

    public SocketClientThread(GroupIndividual groupIndividual) {
        this(groupIndividual, "192.168.49.1", 9000);
    }

    public SocketClientThread(GroupIndividual groupIndividual, String ipAddress, int port) {
        this.groupIndividual = groupIndividual;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public void run() {
        Log.d(TAG, "starting client connected socket");
        try {
            socket = new Socket(ipAddress, port);
            connectedThread = new ConnectedThread(socket, ThreadType.CLIENT, groupIndividual);
            connectedThread.start();
            Log.d(TAG, "got new socket for client");
            Log.d(TAG, "inet address is: " + socket.getInetAddress().getHostAddress());
            // TODO give the GroupIndividual the address and thread
             groupIndividual.map.put(socket.getInetAddress().toString().substring(1), connectedThread);
        } catch (IOException e) {
            Log.e(TAG, "exception in SocketClientThread", e);
            e.printStackTrace();
        }
    }
}
