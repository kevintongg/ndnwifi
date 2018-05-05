package no.bouvet.p2pcommunication.image;

/**
 * Created by Hieu on 3/29/2018.
 */

import android.os.Environment;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Send {

  private static final int SocketServerPORT = 8080;
  private ServerSocket serverSocket;

  private ServerSocketThread serverSocketThread;

  public void run() {

    serverSocketThread = new ServerSocketThread();
    serverSocketThread.start();
    onDestroy();

  }

  private void onDestroy() {

    if (serverSocket != null) {
      try {
        serverSocket.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }


  class ServerSocketThread extends Thread {

    @Override
    public void run() {
      Socket socket;

      try {
        serverSocket = new ServerSocket(SocketServerPORT);
        while (true) {
          socket = serverSocket.accept();
          FileTxThread fileTxThread = new FileTxThread(socket);
          fileTxThread.start();
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
//            } finally {
//                if (SOCKET != null) {
//                    try {
//                        SOCKET.close();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
      }
    }

  }

  class FileTxThread extends Thread {

    final Socket SOCKET;

    FileTxThread(Socket SOCKET) {
      this.SOCKET = SOCKET;
    }

    @Override
    public void run() {
      for (int i = 1; i < 51; i++) {
        File file = new File(Environment.getExternalStorageDirectory(), i + ".jpg");
        Log.d("Send--------", String.valueOf(i));

        byte[] bytes = new byte[(int) file.length()];
        BufferedInputStream bis;
        try {
          bis = new BufferedInputStream(new FileInputStream(file));
          bis.read(bytes, 0, bytes.length);

          ObjectOutputStream oos = new ObjectOutputStream(SOCKET.getOutputStream());
          oos.writeObject(bytes);
          oos.flush();

//                    SOCKET.close();

          final String sentMsg = "File sent to: " + SOCKET.getInetAddress();
          Log.d("Message---------------", sentMsg);

        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
//                } finally {
//                    try {
////                        SOCKET.close();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
        }
      }
    }
  }
}