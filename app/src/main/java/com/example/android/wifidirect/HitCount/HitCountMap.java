package com.example.android.wifidirect.HitCount;

import android.util.Log;

import com.example.android.wifidirect.connectivity.ConnectedThread;

import java.lang.Comparable;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class HitCountMap{

public final String TAG = "HitCountMap";
private Map<String, HitCounter> interestMap;
    public HitCountMap(){
        interestMap = new HashMap<>();
    }

    // data was returned successfully for interest from ip
    public void addHit(String interest, String ip){
        if(interestMap.containsKey(interest)){
            interestMap.get(interest).addHit(ip);
        }
        else {
            HitCounter counter = new HitCounter();
            counter.addHit(ip);
            interestMap.put(interest, counter);
        }
    }


    // returns a priority queue of IpInterfaces
    public Queue<IpInterface> ipByCount(String interest, Set<String> ips) {
        Log.d(TAG, "number of interfaces: " + ips.size());
        Map<String, Integer> count = new HashMap<>(ips.size());
        for(String ip : ips){
            if(!count.containsKey(ip)){
                count.put(ip,0);
            }
        }
//        PriorityQueue<IpInterface> pq = new PriorityQueue<IpInterface>();
        Queue<IpInterface> pq = new ArrayDeque<>();
        if(interestMap.containsKey(interest)){
            HitCounter htc = interestMap.get(interest);
            Map<String, Integer> listOfMaps = htc.interfaceMap;

            for(Map.Entry<String, Integer> entry : listOfMaps.entrySet()){
                int max = Math.max(count.get(entry.getKey()), entry.getValue());
                count.put(entry.getKey(), max);
            }
        }


        Iterator it = count.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            IpInterface inter = new IpInterface((String)pair.getKey(), (int)pair.getValue());
            pq.add(inter);
            Log.d(TAG, "adding interface: " + inter.getIpAddress() + " with count: " + inter.getCount());
        }
        return pq;
    }

    // return the ip with the highest hit count for interest
    public String getBestIP(String interest){
        if(!interestMap.containsKey(interest)){
            return null;
        }
        return interestMap.get(interest).getMaxHitIP();
    }

    public boolean hasBestIP(String interest){
        return interestMap.containsKey(interest);
    }

    public static class HitCounter {
        private Map<String, Integer> interfaceMap;
        private Integer maxHits;
        private String maxHitIP;

        public HitCounter(){
            interfaceMap = new HashMap<>();
            maxHits = null;
            maxHitIP = null;
        }

        public String getMaxHitIP(){
            // returns the ip with the highest number of hits
            return maxHitIP;
        }

        public Integer getMaxHits(){
            return maxHits;
        }

        // adds 1 hit to this ip
        public void addHit(String ip){
            Integer count = interfaceMap.containsKey(ip) ? interfaceMap.get(ip) + 1 : 1;
            interfaceMap.put(ip, count);
            if(maxHits == null || count > maxHits){
                maxHits = count;
                maxHitIP = ip;
            }
        }
    }

    public static class IpInterface implements Comparable<IpInterface>{
        private String ipAddress;
        private int count;

        public IpInterface(String Ip, int counter) {
            this.ipAddress = Ip;
            this.count = counter;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getIpAddress() {

            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        @Override
        public int compareTo(IpInterface other) {
            return other.count - this.count;
        }
    }
}

