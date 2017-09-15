package com.example.android.wifidirect.connectivity;

import android.util.Log;

import com.example.android.wifidirect.group.GroupIndividual;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by tj on 1/31/16.
 */
public class ConnectedThread extends Thread {
    public static final String TAG = "ConnectedThread";
    private Socket connectedSocket;
    private GroupIndividual groupIndividual;
    ConnectionStatus connectionStatus;
    ThreadType threadType;
    OutputStream outputStream;
    InputStream inputStream;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    public ConnectedThread(Socket socket, ThreadType threadType, GroupIndividual groupIndividual) {
        this.threadType = threadType;
        this.groupIndividual = groupIndividual;
        connectedSocket = socket;
        connectionStatus = ConnectionStatus.CONNECTED;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = connectedSocket.getInputStream();
            tmpOut = connectedSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }
        inputStream = tmpIn;
        outputStream = tmpOut;
    }



    @Override
    public void run() {
        SerialMessage receivedMessage = null;
        try{
            oos = new ObjectOutputStream(outputStream);
            oos.flush();
            ois = new ObjectInputStream(inputStream);
        }
        catch(IOException ex){
            Log.e(TAG, "unable to create object input/output streams");
        }
        if(threadType == ThreadType.SERVER){
            Log.d(TAG, "server thread running");
            // need to inform player what their id is
            while (connectionStatus == ConnectionStatus.CONNECTED) {
                try {
                    try{
                        receivedMessage = (SerialMessage)ois.readObject();
                    } catch(ClassNotFoundException ex){
                        Log.e(TAG, "class not found");
                    }
                    if(receivedMessage == null && connectionStatus == ConnectionStatus.CONNECTED){ // the socket was lost
                        connectionStatus = ConnectionStatus.DISCONNECTED;
//                        TODO :connectionLost();
                    }
                    else if(receivedMessage != null){
                        Log.d(TAG, "message received: " + receivedMessage);
                        handleMessage(receivedMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        else if(threadType == ThreadType.CLIENT){
            while (true) {
                try {
                    try{
                        receivedMessage = (SerialMessage)ois.readObject();
                    } catch(ClassNotFoundException ex){
                        Log.e(TAG, "class not found");
                    }
                    Log.d(TAG, "message received: " + receivedMessage.toString());
                    handleMessage(receivedMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    public void write(SerialMessage message) {
        Log.d(TAG, "writing serial message ");
        try{
            oos.writeObject(message);
            oos.flush();
        } catch(IOException ex){
            Log.e(TAG, "error while tryint to write object: " + message.toString());
        }
    }

    public void handleMessage(SerialMessage receivedMessage){
        groupIndividual.receiveMessage(receivedMessage);
    }

    // TODO private void connectionLost(ConnectedThread connectedHostThread) {
        // Send a failure message
//        try {
//            ConnectedThread lostThread = MyApplication.threadMap.get(id);
//            MyApplication.threadMap.remove(id);
//            lostThread.connectedSocket.close();
//            connectedHostThread.connectedSocket.close();
//            Log.i(TAG, "closed socket for player: " + id.toString());
//        } catch (IOException e) {
//            Log.e(TAG, "connectionLost() on socketId: " + socketId, e);
//        }
//    }
}