package no.bouvet.p2pcommunication.multiclient;

/**
 * Created by Hieu on 3/20/2018.
 */

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import no.bouvet.p2pcommunication.image.Receive;
import no.bouvet.p2pcommunication.image.Send;

public class Client extends AppCompatActivity {

  public static final int NOTIFICATION_ID = 1;

  private final String WELCOME_MESSAGE = "";

  public void run(String ip) {
    MyClientTask myClientTask = new MyClientTask(ip, WELCOME_MESSAGE);
    myClientTask.execute();
  }

  class MyClientTask extends AsyncTask<Void, Void, Void> {

    final String DESTINATION_ADDRESS;
    final int DESTINATION_PORT;
    String response = "";
    final String MESSAGE_TO_SERVER;

    MyClientTask(String address, String msgTo) {
      DESTINATION_ADDRESS = address;
      DESTINATION_PORT = 8080;
      MESSAGE_TO_SERVER = msgTo;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

      Socket socket = null;
      DataOutputStream dataOutputStream = null;
      DataInputStream dataInputStream = null;

      try {
        socket = new Socket(DESTINATION_ADDRESS, DESTINATION_PORT);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
        if (MESSAGE_TO_SERVER != null) {
          dataOutputStream.writeUTF(MESSAGE_TO_SERVER);
        }
        response = dataInputStream.readUTF();
        socket.close();
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
      }
      return null;
    }

    //get msg from server
    @Override
    protected void onPostExecute(Void result) {
      Log.d("Client", response);
      String fileName = response;
      File file = new File(Environment.getExternalStorageDirectory(), fileName);
      if (file.exists()) {
        Log.d("client", "image found");
        Send run = new Send();
        run.run();
      } else {
        Log.d("client", "image not found");
        Receive run = new Receive();
        run.run();
      }
      super.onPostExecute(result);
    }
  }
}