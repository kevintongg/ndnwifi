package no.bouvet.p2pcommunication.image;

/**
 * Created by Hieu on 3/29/2018.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import android.os.Environment;
import android.util.Log;

public class Receive {
    static final int SocketServerPORT = 8080;

    public void run() {

        ClientRxThread clientRxThread = new ClientRxThread("192.168.49.10", SocketServerPORT);
        clientRxThread.start();

    }

    private class ClientRxThread extends Thread {
        String dstAddress;
        int dstPort;

        ClientRxThread(String address, int port) {
            dstAddress = address;
            dstPort = port;
        }

        @Override
        public void run() {
            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                File file = new File(Environment.getExternalStorageDirectory(), "fourleaf.jpg");

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                byte[] bytes;
                FileOutputStream fos = null;
                try {
                    bytes = (byte[])ois.readObject();
                    fos = new FileOutputStream(file);
                    fos.write(bytes);


                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if(fos!=null){
                        fos.close();
                    }

                }

                socket.close();

                if(file.exists()){
                    Log.d("client","image found");
//                    Send run = new Send();
//                    run.run();
                }
                else{
                    Log.d("client","image not found");
                }



            } catch (IOException e) {

                e.printStackTrace();


            } finally {
                if(socket != null){
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

}
