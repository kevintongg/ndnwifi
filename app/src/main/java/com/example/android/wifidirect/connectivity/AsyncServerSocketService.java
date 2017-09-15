package com.example.android.wifidirect.connectivity;

import android.util.Log;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tj on 1/28/16.
 */
public class AsyncServerSocketService extends Thread{

    public static final String TAG = "socketservice";
    private final int port;
    private final int TIMEOUT = 8000;

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    public SelectionKey mainKey;

    private Map<SocketChannel, byte[]> dataTracking;


    public AsyncServerSocketService(){
        this(0);
    }

    public AsyncServerSocketService(AsyncServerSocketService otherAsyncServerSocketService){
        this(otherAsyncServerSocketService.port);
    }

    public AsyncServerSocketService(int port){
        Log.d(TAG, "constructing socketservice");
        this.port = port;
        this.dataTracking = new HashMap<SocketChannel, byte[]>();
        try {
            // create a Selector
            this.selector = Selector.open();
            // create the ServerSocket
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.socket().bind(new InetSocketAddress(this.port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
        catch(BindException ex){
            System.err.println("port is already in use");
            System.exit(1);
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void run(){
        try {
            while(!Thread.currentThread().isInterrupted()){
                selector.select(TIMEOUT);
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while(it.hasNext()){
                    SelectionKey key = it.next();
                    // closed connection, perhaps
                    if(!key.isValid()){
                        continue;
                    }
                    // if a new connection made need to check if max connections already
                    if (key.isAcceptable()) {
                        Log.d(TAG, "accepting a new connection");
                        accept(key);
                    }
                    // new input from channel
                    if(key.isWritable()){
                        Log.d(TAG, "writing to channel");
                        mainKey = key;
                        write(key);
                    }
                    if(key.isReadable()){
                        Log.d(TAG, "reading from channel");
                        read(key);
                    }
                    it.remove();
                }
                Log.d(TAG, "writing on timeout");
                if(mainKey != null)
                    write(mainKey);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            closeConnection();
        }
    }

    private void accept(SelectionKey key){
        try{
            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_WRITE);
            String welcomeMessage = "server says hello";
            byte[] message = welcomeMessage.getBytes();
            dataTracking.put(socketChannel, message);
            Log.d(TAG, "accepted channel and prepared message");
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void write(SelectionKey key){
        try{
            SocketChannel channel = (SocketChannel) key.channel();
            byte[] message = dataTracking.get(channel);
            if(message == null)
                message = "simple message".getBytes();
            dataTracking.remove(channel);
            channel.write(ByteBuffer.wrap(message));
            key.interestOps(SelectionKey.OP_READ);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private void read(SelectionKey key){
        try{
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1000);
            buffer.clear();
            int read;
            try{
                read = channel.read(buffer);
            }
            catch (IOException ex){
                Log.w(TAG, "exception while reading");
                key.cancel();
                channel.close();
                return;
            }
            if(read == -1){
                Log.d(TAG, "no stringData to read");
                channel.close();
                key.cancel();
                return;
            }
            byte[] buffArray = buffer.array();
//            buffer.get(buffArray, 0, read + 1);
            String message = new String(buffArray);
            Log.d(TAG, "Message Received: " + message);
            Log.d(TAG, "message length: " + message.length());
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private void closeConnection(){
        Log.d(TAG, "closing connection");
        if (selector != null){
            try {
                selector.close();
                serverSocketChannel.socket().close();
                serverSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
