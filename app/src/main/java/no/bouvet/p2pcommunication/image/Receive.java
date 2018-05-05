package no.bouvet.p2pcommunication.image;

/**
 * Created by Hieu on 3/29/2018.
 */

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

public class Receive {

  private static final String TAG = Receive.class.getSimpleName();
  private static final int SocketServerPORT = 8080;

  public void run() {
    ClientRxThread clientRxThread = new ClientRxThread();
    clientRxThread.start();
  }

  private class ClientRxThread extends Thread {

    String dstAddress;
    final int DESTINATION_PORT;

    ClientRxThread() {
      dstAddress = "192.168.49.1";
      DESTINATION_PORT = Receive.SocketServerPORT;
    }

    @Override
    public void run() {
      Socket socket;
      AtomicReference<Stack<String>> result = new AtomicReference<>(new Stack<>());

      try {
        socket = new Socket(dstAddress, DESTINATION_PORT);
        for (int i = 1; i < 51; i++) {
          File file = new File(Environment.getExternalStorageDirectory(), i + ".jpg");
          Log.d("Receive--------", String.valueOf(i));
          ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
          long start = System.currentTimeMillis();
          byte[] bytes;
          FileOutputStream fos;
          try {
            bytes = (byte[]) ois.readObject();
            fos = new FileOutputStream(file);
            fos.write(bytes);
            long cost = System.currentTimeMillis() - start;
            result.get().push(" speed: " + cost + " /ms");
            Log.d(TAG, " speed: " + cost + " /ms");
          } catch (ClassNotFoundException e) {
            e.printStackTrace();
          }

          try {
            // thread to sleep for 3000 milliseconds
            Thread.sleep(3000);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}