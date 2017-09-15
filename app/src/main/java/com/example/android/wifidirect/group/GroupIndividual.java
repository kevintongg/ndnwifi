package com.example.android.wifidirect.group;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.android.wifidirect.DeviceListFragment;
import com.example.android.wifidirect.Utils;
import com.example.android.wifidirect.connectivity.BitmapData;
import com.example.android.wifidirect.connectivity.ConnectedThread;
import com.example.android.wifidirect.connectivity.DataType;
import com.example.android.wifidirect.connectivity.MessageType;
import com.example.android.wifidirect.connectivity.SerialMessage;
import com.example.android.wifidirect.connectivity.SocketClientThread;
import com.example.android.wifidirect.connectivity.SocketServerThread;
import com.example.android.wifidirect.image.ImageCache;
import com.example.android.wifidirect.measure.BandwidthDBHelper;
import com.example.android.wifidirect.measure.Timer;
import com.example.android.wifidirect.measure.TimerDBHelper;
import com.example.android.wifidirect.ndn.ForwardingStrategy;
import com.example.android.wifidirect.ndn.PendingInterestTable;
import com.example.android.wifidirect.ndn.PendingInterestTable.PITEntry;
import com.example.android.wifidirect.HitCount.HitCountMap;
import com.example.android.wifidirect.HitCount.HitCountMap.IpInterface;

import java.io.IOException;
import java.lang.Integer;
import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by tj on 2/5/16.
 */
public class GroupIndividual {
    private static final boolean DEBUG = true;
    private static final String STRING_DATA_NAME = "demostring";
    private static final String STRING_DATA = "this is the demo string data";
    private static final String IMAGE_DATA_NAME = "demoimage";
    private static final String IMAGE_DATA = "fourleaf.png";

    private Map<String, String> cacheMap;
    private HitCountMap hitCountMap;

    public static final String TAG = "groupindividual";
    public Map<String, ConnectedThread> map;
    public SocketServerThread socketServerThread;
    public SocketClientThread socketClientThread;
    public DeviceListFragment fragment;
    public PendingInterestTable pit;
    private ImageCache cache;
    public Map<String, TimeoutTask> timeoutTaskMap;
    public Map<String, TimeoutBestIP> timeoutBestIPMap;
    public Map<String, SequentialTimeoutTask> sequentialTimeoutTaskMap;
    private Timer timer;
    private ForwardingStrategy forwardingStrategy;

    public GroupIndividual(DeviceListFragment fragment){
        this.fragment = fragment;
        map = new HashMap<String, ConnectedThread>();
        hitCountMap = new HitCountMap();
        cacheMap = new HashMap<String, String>();
        cacheMap.put(STRING_DATA_NAME, STRING_DATA);
        cacheMap.put(IMAGE_DATA_NAME, IMAGE_DATA);
        pit = new PendingInterestTable();
        cache = new ImageCache(fragment.getActivity().getApplicationContext());
        timeoutTaskMap = new HashMap<String, TimeoutTask>();
        timeoutBestIPMap = new HashMap<>();
        sequentialTimeoutTaskMap = new HashMap<>();
        timer = new Timer();
        this.forwardingStrategy = ForwardingStrategy.HIT_THEN_FLOOD; // TODO switch to other strategies
    }

    public boolean startServer(){
        Toast.makeText(fragment.getView().getContext(), "starting server", Toast.LENGTH_SHORT).show();
        socketServerThread = new SocketServerThread(this);
        socketServerThread.start();
        return true;
    }

    public boolean startClient(){
        Toast.makeText(fragment.getView().getContext(), "starting client", Toast.LENGTH_SHORT).show();
        socketClientThread = new SocketClientThread(this);
        socketClientThread.start();
        return true;
    }

    public void prepareSerialInterest(String name){
        forwardSerialInterest(name, Utils.retrieveMyIp());
    }

    public void forwardSerialInterest(String name, String incoming){
        // timeout will start with 10 seconds
        // if a data is received, cancel the timout
        // else, remove the corresponding PIT entries
        SerialMessage message = new SerialMessage.MessageBuilder(Utils.retrieveMyIp(), MessageType.INTEREST,
                name).build();
        Log.d(TAG, "preparing message: " + name);
        // try to forward base on hitmap
        if(forwardingStrategy == ForwardingStrategy.FLOOD){
            floodInterest(message, name, incoming);
        }
        else if(forwardingStrategy == ForwardingStrategy.HIT_THEN_FLOOD){
            if(hitCountMap.hasBestIP(name)){
                singleInterest(message, name, incoming);
            }
            // if there are no entries, flood
            else {
                floodInterest(message, name, incoming);
            }
        }
        else if(forwardingStrategy == ForwardingStrategy.SEQUENTIAL){
            // get a queue of interfaces to try
            PriorityQueue<IpInterface> queue = hitCountMap.ipByCount(name, map.keySet());
            Log.i(TAG, "creating sequential interest for queue" + queue.size());
            if(queue.size() == 0){
                floodInterest(message, name, incoming);
            } else{
                sequentialInterest(message, name, incoming, queue);
            }

        }

    }

    public void sequentialInterest(SerialMessage message, String name, String incoming, PriorityQueue<IpInterface> queue){
        if(queue.size() > 0){
            String nextIP = queue.peek().getIpAddress();// get the queue
            pit.insertPitEntry(message, name, incoming, nextIP);
            sendSequentialInterest(message, name, incoming, queue);
        }
    }

    private void sendSequentialInterest(SerialMessage message, String name, String incoming, PriorityQueue<IpInterface> queue){
        if(queue.size() > 0){
            String nextIP = queue.poll().getIpAddress();
            ConnectedThread ct = map.get(nextIP);
            ct.write(message);
            addSequentialTaskTimeout(message, name, incoming, nextIP, queue);// FIXME add sequential timeout
            timer.start(name);
            Log.d(TAG, "sending to : " + nextIP);
        }
    }

    public void singleInterest(SerialMessage message, String name, String incoming){
        String bestIP = hitCountMap.getBestIP(name);
        ConnectedThread ct = map.get(bestIP);
        ct.write(message);
//        addTimeout(name);
        addBestIPTimeout(message, name, incoming, bestIP);
        pit.insertPitEntry(message, name, incoming, bestIP);
        timer.start(name);
        Log.d(TAG, "sending to best: " + bestIP);
    }

    public void floodInterest(SerialMessage message, String name, String incoming){
        for(String key: map.keySet()){
            Log.d(TAG, "connected thread ip " + key);
            Log.d(TAG, "incoming ip " + incoming);
            if(!key.equals(incoming)){
                ConnectedThread ct = map.get(key);
                ct.write(message);
                addTimeout(name);
                pit.insertPitEntry(message, name, incoming, key);
                timer.start(name);
            }
        }
    }


    public void toastMessage(String message){
        final String msg = message;
        Log.d(TAG, "sent message to ui: " + msg);
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(fragment.getView().getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openImageMessage(String name, String filepath){
//        String filePath = "/sdcard/spyder/";
//        File dir = new File(filePath);
//        if(!dir.exists())
//            dir.mkdirs();
//        File file = new File(dir, filename);
//        try{
//            FileOutputStream fOut = new FileOutputStream(file);
//            fOut.write(bd.getImageByteArray());
//            fOut.flush();
//            fOut.close();
//            Log.d(TAG, "wrote file to " + file.getAbsolutePath());
//        }catch(IOException ex){
//            ex.printStackTrace();
//        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + filepath), "image/png");
        fragment.getActivity().getApplicationContext().startActivity(intent);
    }

    public void receiveMessage(SerialMessage message){
        Log.d(TAG, "unwrapping SerialMessage");
        if(message.messageType == MessageType.INTEREST){
            Log.d(TAG, "received interest");
            boolean isCached = dataIsCached(message.name);
            Log.d(TAG, "is data cached ? " + isCached);
            // if cache has item, prepare DATA message to sender
            if(isCached){
                Log.d(TAG, "received interest and data is in my cache");
                sendDataMessage(message, message.sender);
            }
            // if cache does not have item, forward the interest to everyone except sender
            else{
                Log.d(TAG, "forwarding interest to peers");
                forwardSerialInterest(message.name, message.sender);
            }
        }
        else if(message.messageType == MessageType.DATA){
            // if I don't have an entry corresponing in PIT, drop the interest
            int dataSize = (message.bitmapData != null) ? message.bitmapData.getImageByteArray().length : 0;
            BandwidthDBHelper bandwidthDBHelper = new BandwidthDBHelper(fragment.getActivity().getApplicationContext());
            bandwidthDBHelper.addBandwidthEntry(dataSize);

            if(!pit.hasDuplicate(message.name)){
                return;
            }
            hitCountMap.addHit(message.name, message.sender);
            cancelTimeout(message.name);
            cancelBestIPTimeout(message.sender);
            cancelSequentialTaskTimeout(message.sender);
            // used for measuring time taken for response for interest
            Timer.StartStop st = timer.stop(message.name);
            if(st != null){
                TimerDBHelper timeDB = new TimerDBHelper(fragment.getActivity().getApplicationContext());
                timeDB.addTimestamp(message.name, st.getStart(), st.getStop());
            }
            boolean amIOrigin = false;
            // if I am a relay point, forward DATA message to all interested peers
            // and cache the data
            String absolutePath = cache.imageWrite(message.name, message.bitmapData.getImageByteArray());
            List<PITEntry> entries = pit.remove(message.name);
            for(PITEntry entry : entries){
                // send a data message
                if(entry.getIncoming().equals(Utils.retrieveMyIp())){
                    amIOrigin = true;
                } else{
                    sendDataMessage(message, entry.getIncoming());
                }

            }

            // if I sent the initial interest, cache the data and open
            if(amIOrigin){
                if(message.dataType == DataType.STRING){
                    toastMessage(message.stringData);
                }
                if(message.dataType == DataType.IMAGE){
                    openImageMessage(message.name, absolutePath);
                }
            }

        }
        else if(message.messageType == MessageType.NACK){
            // if I sent the initial request, alert the user data could not be found
            // if I am a relay point, prepare a NACK message
        }
    }

    // precondition: the data is cached
    private void sendDataMessage(SerialMessage message, String receiver){
        if(message.name.equals(STRING_DATA_NAME)){
            String data = getStringData(message.name);
            SerialMessage response = new SerialMessage.MessageBuilder(Utils.retrieveMyIp(),
                    MessageType.DATA, message.name).dataType(DataType.STRING).stringData(data).build();
            Log.d(TAG, "sending message to " + receiver);
            map.get(receiver).write(response);
        }
        else {
            try{
                Log.d(TAG, "sending data message");
                BitmapData bd = new BitmapData();
                byte[] arr = cache.imageRead(message.name);
                bd.setImageByteArray(arr);
                SerialMessage response = new SerialMessage.MessageBuilder(Utils.retrieveMyIp(),
                        MessageType.DATA, message.name).dataType(DataType.IMAGE).bitmapData(bd).build();
                map.get(receiver).write(response);
                Log.d(TAG, "sent data message successfully");
            } catch (IOException ex){
                // SEND NACK
                Log.e(TAG, "error ");
            }
        }
    }

    public boolean dataIsCached(String name){
        return cache.isCached(name);
    }

    public String getStringData(String name){
        // TODO actually get data
        return STRING_DATA;
    }

    public void addTimeout(String name){
        // if the name does not exist in the pending interest table
        if(!pit.hasDuplicate(name)){
            TimeoutTask task = new TimeoutTask();
            timeoutTaskMap.put(name, task);
            task.execute(name);
        }
    }

    public void cancelTimeout(String name){
        if(timeoutTaskMap.containsKey(name)){
            timeoutTaskMap.get(name).cancel(true);
        }
    }

    public void addBestIPTimeout(SerialMessage message, String name, String incoming, String outgoing){
        TimeoutBestIP timeout = new TimeoutBestIP(message, name, incoming, outgoing);
        timeoutBestIPMap.put(outgoing, timeout);
        timeout.execute();
    }

    public void cancelBestIPTimeout(String ip){
        if(timeoutBestIPMap.containsKey(ip)){
            timeoutBestIPMap.get(ip).cancel(true);
        }
    }

    public void addSequentialTaskTimeout(SerialMessage message, String name, String incoming,
                                         String outgoing, PriorityQueue<IpInterface> queue){
        SequentialTimeoutTask timeout = new SequentialTimeoutTask(message, name, incoming, outgoing, queue);
        sequentialTimeoutTaskMap.put(outgoing, timeout);
        timeout.execute();
    }

    public void cancelSequentialTaskTimeout(String ip){
        if(sequentialTimeoutTaskMap.containsKey(ip)){
            sequentialTimeoutTaskMap.get(ip).cancel(true);
        }
    }

    public class TimeoutTask extends AsyncTask<String, Integer, Long> {
        public static final String TAG = "timeouttask";
        private final Integer timeout = 2000;
        private String name;
        protected Long doInBackground(String... strings) {
            name = strings[0];
            try{
                Thread.sleep(timeout);
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }
            return 0L;
        }

        protected void onCancelled(){
            Log.d(TAG, "cancelling time out");
            timeoutTaskMap.remove(name);
        }

        protected void onPostExecute(Long result) {
            Log.d(TAG, "timed out--removing " + name + " from pit");
            pit.remove(name);
            timeoutTaskMap.remove(name);
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(fragment.getView().getContext(), "unable to find data", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public class TimeoutBestIP extends AsyncTask<String, Integer, Long> {
        public static final String TAG = "timeouttask";
        private final Integer timeout = 2000;
        private String name;
        private SerialMessage message;
        private String incoming;
        private String outgoing;

        public TimeoutBestIP(SerialMessage message, String name, String incoming, String outgoing){
            this.message = message;
            this.name = name;
            this.incoming = incoming;
            this.outgoing = outgoing;
        }

        protected Long doInBackground(String... strings) {
//            name = strings[0];
            try{
                Thread.sleep(timeout);
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }
            return 0L;
        }

        protected void onCancelled(){
            Log.d(TAG, "cancelling bestip time out");
            timeoutBestIPMap.remove(outgoing);
        }

        protected void onPostExecute(Long result) {
            Log.d(TAG, "timed out-- flooding ");
            // FIXME: should use a combo of ip and interest;
            timeoutBestIPMap.remove(outgoing);
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    floodInterest(message, name, incoming);
                }
            });
        }

    }

    public class SequentialTimeoutTask extends AsyncTask<String, Integer, Long> {
        public static final String TAG = "sequentialtimeouttask";
        private final Integer timeout = 2000;
        private String name;
        private SerialMessage message;
        private String incoming;
        private String outgoing;
        private PriorityQueue<IpInterface> queue;

        public SequentialTimeoutTask(SerialMessage message, String name,
                                     String incoming, String outgoing, PriorityQueue<IpInterface> queue){
            this.message = message;
            this.name = name;
            this.incoming = incoming;
            this.outgoing = outgoing;
            this.queue  = queue;
        }

        protected Long doInBackground(String... strings) {
            try{
                Thread.sleep(timeout);
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }
            return 0L;
        }

        protected void onCancelled(){
            Log.d(TAG, "cancelling sequential time out");
            sequentialTimeoutTaskMap.remove(outgoing);
        }

        protected void onPostExecute(Long result) {
            Log.d(TAG, "timed out -- trying next element ");
            // FIXME: should use a combo of ip and interest;
            sequentialTimeoutTaskMap.remove(outgoing);
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sendSequentialInterest(message, name, incoming, queue);
                }
            });
        }

    }
}
