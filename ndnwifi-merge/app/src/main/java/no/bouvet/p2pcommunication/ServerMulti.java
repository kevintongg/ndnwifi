package no.bouvet.p2pcommunication;

import android.os.StrictMode;

import java.io.IOException;
import java.net.ServerSocket;
import java.io.*;
import java.net.*;
/**
 * Created by Hieu on 3/18/2018.
 */

public class ServerMulti {
    public static void Server () {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ServerSocket serversocket=null;
        final int PORT_NUM=4444;
        boolean flag=true;
        try{
            System.out.println("Listening for connection");
            serversocket=new ServerSocket(PORT_NUM);
        }catch(IOException e){
            System.out.println("Could not listen to port: "+PORT_NUM);
            System.exit(-1);
        }
        try {
            while (flag) {
                new ClientWorker(serversocket.accept()).start();
            }
            System.out.println("Terminating server...");
            serversocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
