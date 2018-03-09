package no.bouvet.p2pcommunication;

/**
 * Created by Hieu on 3/6/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

//public class ClientSocketAuto extends AppCompatActivity {
//    ArrayList<Node> listNote;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        try {
//            //
//            // Create a connection to the server socket on the server application
//            //
//
//            String ipClient ="" ;
//
//
//            listNote = new ArrayList<>();
//            readAddresses();
//
//            for (int i = 0; i < listNote.size(); i++) {
//                ipClient = listNote.get(i).toString();
//
//            }
//
////            InetAddress host = InetAddress.getLocalHost();
//            Socket socket = new Socket(ipClient, 7777);
//
//            //
//            // Send a message to the client application
//            //
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//            oos.writeObject("Client");
//
//            //
//            // Read and display the response message sent by server application
//            //
//            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//            String message = (String) ois.readObject();
//            System.out.println("Message: " + message);
//
//            if(message =="Server"){
//                Intent intent = new Intent(getApplicationContext(), Client.class);
//                startActivity(intent);
//            }
//
//            ois.close();
//            oos.close();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void readAddresses() {
//        listNote.clear();
//        BufferedReader bufferedReader = null;
//
//        try {
//            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
//
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                String[] splitted = line.split(" +");
//                if (splitted != null && splitted.length >= 4) {
//                    String ip = splitted[0];
//                    String mac = splitted[3];
//                    if (mac.matches("..:..:..:..:..:..")) {
//                        Node thisNode = new Node(ip);
//                        listNote.add(thisNode);
//                    }
//                }
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                bufferedReader.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    class Node {
//
//        String ip;
//
//
//        Node(String ip) {
//            this.ip = ip;
//        }
//
//        @Override
//        public String toString() {
//            return ip;
//        }
//    }
//}

 public class ClientSocketAuto  {
     ArrayList<Node> listNote;
     String textResponse;
     static final int SocketServerPORT = 8080;
     String ipClinet ="" ;


public void onCreate(String add) {




        listNote = new ArrayList<>();
        readAddresses();

        for (int i = 0; i < listNote.size(); i++) {
            ipClinet = listNote.get(i).toString();

        }


        Log.d("----------------------", ipClinet);

    MyClientTask myClientTask = new MyClientTask(add,SocketServerPORT);
    myClientTask.execute();


}

     private void readAddresses() {
         listNote.clear();
         BufferedReader bufferedReader = null;

         try {
             bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));

             String line;
             while ((line = bufferedReader.readLine()) != null) {
                 String[] splitted = line.split(" +");
                 if (splitted != null && splitted.length >= 4) {
                     String ip = splitted[0];
                     String mac = splitted[3];
                     if (mac.matches("..:..:..:..:..:..")) {
                         Node thisNode = new Node(ip);
                         listNote.add(thisNode);
                     }
                 }
             }

         } catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         } finally {
             try {
                 bufferedReader.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     }
     class Node {

         String ip;


         Node(String ip) {
             this.ip = ip;
         }

         @Override
         public String toString() {
             return ip;
         }
     }


public class MyClientTask extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    String response = "";

    MyClientTask(String addr, int port){
        dstAddress = addr;
        dstPort = port;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(ipClinet, dstPort);
//            socket.setReuseAddress(true);
//
//            socket.bind(new InetSocketAddress(ipClinet, dstPort));

            ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

    /*
     * notice:
     * inputStream.read() will block if no data return
     */
            while ((bytesRead = inputStream.read(buffer)) != -1){
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");

                Log.d("MyClientTask inside" , response);


                if( response != null){
//                    socket.close();

                    Intent intent = new Intent( P2PCommunicationActivity.getContext(), Client.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    P2PCommunicationActivity.getContext().startActivity(intent);


                }



            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        }finally{
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        textResponse = (response);
        super.onPostExecute(result);
    }

}

}