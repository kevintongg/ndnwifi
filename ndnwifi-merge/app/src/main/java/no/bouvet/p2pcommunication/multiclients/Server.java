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
//                MainActivity.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        info.setText("I'm waiting here: "
//                                + serverSocket.getLocalPort());
//                    }
//                });

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

//                    MainActivity.this.runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            msg.setText(message);
//                        }
//                    });
                    Log.d("server", message);
                    //send this msg to client
                    String msgReply = "fourleaf.jpg";
                    dataOutputStream.writeUTF(msgReply);

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                final String errMsg = e.toString();
//                MainActivity.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        msg.setText(errMsg);
//                    }
//                });

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

    public class FileTxThread extends Thread {
        Socket socket;

        FileTxThread(Socket socket){
            this.socket= socket;
        }

        @Override
        public void run() {
            File file = new File(
                    Environment.getExternalStorageDirectory(),
                    "pic1.jpg");

            byte[] bytes = new byte[(int) file.length()];
            BufferedInputStream bis;
            try {
                bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytes, 0, bytes.length);

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(bytes);
                oos.flush();

                socket.close();


            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }


}
