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
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;

public class Client extends AppCompatActivity {
  public static final int NOTIFICATION_ID = 1;

  String welcomeMsg = "fourleaf.jpg";

    public void run() {
            MyClientTask myClientTask = new MyClientTask("192.168.49.1",8080, welcomeMsg);
            myClientTask.execute();
    }


    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        String msgToServer;

        MyClientTask(String addr, int port, String msgTo) {
            dstAddress = addr;
            dstPort = port;
            msgToServer = msgTo;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                if(msgToServer != null){
                    dataOutputStream.writeUTF(msgToServer);
                }

                response = dataInputStream.readUTF();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
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

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

//                String fileName = response;
//                File f = new File("/storage/emulated/0/DCIM/Camera/" + fileName);
//                if(!f.mkdir()){
//                    Log.d("client","image found");
//                    FileTxThread fileTxThread = new FileTxThread(socket);
//                    fileTxThread.start();
//                }
//                else{
//                    Log.d("client","image not found");
//                }


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            textResponse.setText(response);
            Log.d("Client",response);
            String fileName = response;
            File f = new File("/storage/emulated/0/DCIM/Camera/" + fileName);
            if(!f.mkdir()){
                Log.d("client","image found");
//                FileTxThread fileTxThread = new FileTxThread(socket);
//                fileTxThread.start();
                welcomeMsg = "image found";
            }
            else{
                Log.d("client","image not found");
            }

            super.onPostExecute(result);
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
                    "fourleaf.jpg");

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