package no.bouvet.p2pcommunication;

/**
 * Created by Hieu on 3/6/2018.
 */


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.ClassNotFoundException;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

//public class ServerSocketAuto extends AppCompatActivity {
//    private ServerSocket server;
//    private int port = 7777;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        ServerSocketAuto example = new ServerSocketAuto();
//        example.handleConnection();
//    }
//
//    public ServerSocketAuto() {
//        try {
//            server = new ServerSocket(port);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
////    public void ServerRun() {
////        ServerSocketAuto example = new ServerSocketAuto();
////        example.handleConnection();
////    }
//
//    public void handleConnection() {
////        System.out.println("Waiting for client message...");
//        Log.d("Server running", " waiting client");
//        //
//        // The server do a loop here to accept all connection initiated by the
//        // client application.
//        //
//        while (true) {
//            try {
//                if(server != null) {
//                    Socket socket = server.accept();
//                if (socket != null) {
//                    new ConnectionHandler(socket);
//                }
//            }} catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
//
//    class ConnectionHandler extends AppCompatActivity implements Runnable {
//        private Socket socket;
//
//        public ConnectionHandler(Socket socket) {
//            this.socket = socket;
//
//            Thread t = new Thread(this);
//            t.start();
//        }
//
//        public void run() {
//            if (socket != null) {
//                try {
//                    //
//                    // Read a message sent by client application
//                    //
//                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//                    String message = (String) ois.readObject();
////            System.out.println("Message Received: " + message);
//
//                    if (message == "Client") {
//                        Intent intent = new Intent(getApplicationContext(), Server.class);
//                        startActivity(intent);
//
//                    }
//
//                    //
//                    // Send a response information to the client application
//                    //
//                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//                    oos.writeObject("Client");
//
//                    ois.close();
//                    oos.close();
//                    socket.close();
//
////            System.out.println("Waiting for client message...");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


public class ServerSocketAuto  {
    String message = "";
    String msg ="";
    ServerSocket serverSocket;


    protected void start() {

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public static Context context;

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 7000;
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(7000);
//                serverSocket.setReuseAddress(true);
//                serverSocket.bind(new InetSocketAddress(SocketServerPORT));


                while (true) {
                    Socket socket = serverSocket.accept();
                    count++;
                    message +=  count;
                    Log.d("socket server thread" , message);

                    if(message != null){


                        FileServer fs = new FileServer();
                        fs.onCreate();


                    }


                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                            socket, count);
                    socketServerReplyThread.run();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            //send to client
            String msgReply = "Client";

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                message += msgReply;
//                Intent intent = new Intent(context,Server.class);
//                context.startActivity(intent);


//                Log.d("SocketServerReplyThread inside" , message);
                serverSocket.close();
                if( message != null){

//                    hostThreadSocket.close();

//                    Intent intent = new Intent( P2PCommunicationActivity.getContext(), Server.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    P2PCommunicationActivity.getContext().startActivity(intent);

                    FileServer run = new FileServer();
                    run.onCreate();

                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }
        }
    }

}