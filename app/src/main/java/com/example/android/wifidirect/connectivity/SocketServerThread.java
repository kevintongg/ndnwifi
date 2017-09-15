package com.example.android.wifidirect.connectivity;

import android.app.Activity;
import android.util.Log;

import com.example.android.wifidirect.DeviceListFragment;
import com.example.android.wifidirect.WiFiDirectActivity;
import com.example.android.wifidirect.group.GroupIndividual;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tj on 1/31/16.
 */
public class SocketServerThread extends Thread {
    private final int port;
    public GroupIndividual groupIndividual;
    private ServerSocket serverSocket;
    public ConnectedThread connectedThread;
    public Map<String, ConnectedThread> map;
    private static final String TAG = "socketserverthread";

    public SocketServerThread(SocketServerThread otherSocketServerThread){
        this(otherSocketServerThread.groupIndividual, otherSocketServerThread.port);
    }
    public SocketServerThread(GroupIndividual groupIndividual){
        this(groupIndividual, 9000);
    }

    public SocketServerThread(GroupIndividual groupIndividual, int port){
        this.groupIndividual = groupIndividual;
        this.port = port;
        this.map = new HashMap<String, ConnectedThread>();
    }

    @Override
    public void run(){
        Log.d(TAG, "starting sever connected socket");
        try{
            serverSocket = new ServerSocket(port);
            while(true){
                Socket socket = serverSocket.accept();
                connectedThread = new ConnectedThread(socket, ThreadType.SERVER, groupIndividual);
                connectedThread.start();
                Log.d(TAG, "got new socket for server");
                Log.d(TAG, "inet address is: " + socket.getInetAddress().getHostAddress());
//                map.put(socket.getInetAddress().toString(), connectedThread);
                // TODO give the GroupIndividual the address and thread
                 groupIndividual.map.put(socket.getInetAddress().toString().substring(1), connectedThread);
            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }

    }
}
