package no.bouvet.p2pcommunication.multiclients;

/**
 * Created by Hieu on 3/20/2018.
 */

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import no.bouvet.p2pcommunication.image.Receive;

public class Server extends Activity {

    String message = "";
    ServerSocket serverSocket;

    public void  run() {

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        int count = 0;

        @Override
        public void run() {
            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {

                serverSocket = new ServerSocket(SocketServerPORT);


                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(
                            socket.getInputStream());
                    dataOutputStream = new DataOutputStream(
                            socket.getOutputStream());

                    String messageFromClient = "";

                    //If no message sent from client, this code will block the program
                    messageFromClient = dataInputStream.readUTF();

                    count++;
                    message += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n"
                            + "Msg from client: " + messageFromClient + "\n";

                    Log.d("server count", String.valueOf(count));

//                    Log.d("server", message);
//                    //send this msg to client
//                    if(count >= 2) {
                    String msgReply = "fourleaf.jpg";
                    dataOutputStream.writeUTF(msgReply);
                    Log.d("server","executed");
//                    }
                    if( count >= 2){
                        try {
                            Thread.sleep(5000);
                        } catch(InterruptedException e){
                            e.printStackTrace();
                        }finally {
                            socket.close();
                            Log.d("server","executed-----------");

                            Receive run = new Receive();
                            run.run();
                        }

                    }

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        }

    }




}