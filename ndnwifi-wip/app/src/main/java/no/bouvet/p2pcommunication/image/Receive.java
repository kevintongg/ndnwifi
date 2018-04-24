package no.bouvet.p2pcommunication.image;

/**
 * Created by Hieu on 3/29/2018.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

import android.os.Environment;
import android.util.Log;

public class Receive {
    static final int SocketServerPORT = 8080;
    public static final String TAG = Receive.class.getSimpleName();
    public void run() {

        ClientRxThread clientRxThread = new ClientRxThread("192.168.49.1", SocketServerPORT);
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
        public void run()  {
            Socket socket = null;
            Stack result = new Stack();

            try {
                socket = new Socket(dstAddress, dstPort);
                for(int i=1;i < 51;i++){
                File file = new File(Environment.getExternalStorageDirectory(), i+".jpg");
                Log.d("Receive--------", String.valueOf(i));
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                long start = System.currentTimeMillis();
                byte[] bytes;
                FileOutputStream fos = null;
                try {
                    bytes = (byte[]) ois.readObject();
                    fos = new FileOutputStream(file);
                    fos.write(bytes);
                    long cost = System.currentTimeMillis() - start;
                        Log.d(TAG," speed: " +  cost + " /ms");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                       fos.close();
                    }
                }
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                    }
                    
                    
                    
                    
                    

//                socket.close();

//                if(file.exists()){
//                    Log.d("client","image found");
//                    Send run = new Send();
//                    run.run();
//                }
//                else{
//                    Log.d("client","image not found");
//                }

            }

            } catch (IOException e) {

                e.printStackTrace();


            } finally {
//                if(socket != null){
//                    try {
//                        socket.close();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }


            }
        }
    }

}
