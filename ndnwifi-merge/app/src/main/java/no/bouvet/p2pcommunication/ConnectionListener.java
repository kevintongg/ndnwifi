//package no.bouvet.p2pcommunication;
//
//import android.util.Log;
//
//import java.io.*;
//import java.util.*;
//import java.net.*;
//
//public class ConnectionListener implements Runnable {
//
//    private Server          server;
//    private ServerSocket    socket;
//    private boolean         running;
//    private Thread          t;
//
//    public ConnectionListener(Server server) {
//        this.server = server;
//        this.socket = server.getSocket();
//        running = false;
//    }
//
//    public synchronized void start() {
//
//        if (running)
//            return;
//
//        running = true;
//        t = new Thread(this);
//        t.start();
//    }
//
//    public synchronized void stop() {
//
//        if (!running)
//            return;
//
//        System.out.print("Terminating connection listener on:" + socket.getLocalSocketAddress() + "...");
//
//        running = false;
//
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            t.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("TERMINATED!");
//    }
//
//    @Override
//    public void run() {
//
//        System.out.println("Listening for connections on: " + socket.getLocalSocketAddress());
//        Log.d("ConnL","Listening for connections on: ");
//
//        try {
//            while (running) {
//                Socket request = socket.accept();
//                Connection connection = new Connection(request);
//                server.addConnection(connection);
//            }
//        } catch (IOException e) {
//            //e.printStackTrace();
//        }
//
//    }
//
//    public boolean isAlive() {
//
//        return running;
//    }
//
//}