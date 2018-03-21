//package no.bouvet.p2pcommunication;
//
//import android.net.wifi.p2p.WifiP2pDevice;
//import android.util.Log;
//
//import java.io.*;
//import java.util.*;
//import java.net.*;
//
//public class ClientMain {
//    public static WifiP2pDevice currentDevice;
//
//    public static void run() {
//
//        String host = "192.168.49.1";
//
//        int port = 4444;
//
//        try (Socket socket = new Socket(host, port);
//                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
////                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
//
//            Thread input = new Thread() ;
//                try {
//            String msg = "fourleaf.jpg";
//
//            while ((msg = in.readLine()) != null) {
//                        System.out.println(msg);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }finally {
//                    input.close();
//                  }
//
//
//            input.start();
//
//            String userName = "User " + currentDevice.deviceName;
//            String msg = "fourleaf.jpg";
//            try {
//                while ((msg = stdIn.readLine()) != null) {
//                    for (int i = 0; i < msg.length(); i++)
//                        System.out.print("\b");
//                    Log.d("Client Main", msg );
//                    out.write(userName + ": " + msg + "\n");
//                    out.flush();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//}