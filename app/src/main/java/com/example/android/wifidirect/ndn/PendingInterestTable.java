package com.example.android.wifidirect.ndn;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.wifidirect.connectivity.SerialMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tj on 2/15/16.
 */
public class PendingInterestTable {
    private Map<String, List<PITEntry>> entries;

    public PendingInterestTable(){
        entries = new HashMap<String, List<PITEntry>>();
    }

    public void insertPitEntry(SerialMessage message, String name, String incoming, String outgoing){
        PITEntry entry = new PITEntry(message, name, incoming, outgoing);
        if(hasDuplicate(entry.name)){
            entries.get(entry.name).add(entry);
        }
        else {
            List<PITEntry> list = new ArrayList<PITEntry>();
            list.add(entry);
            entries.put(entry.name, list);
        }
    }

    public boolean hasDuplicate(String name){
        return entries.containsKey(name);
    }

    public List<PITEntry> remove(String name) {
        if (entries.containsKey(name)) {
            return entries.remove(name);
        } else {
            List<PITEntry> list = new ArrayList<PITEntry>();
            return list;
        }
    }

    public static class PITEntry {
        private SerialMessage message;
        private String name;
        private String incoming;
        private String outgoing;

        public PITEntry(SerialMessage message, String name, String incoming, String outgoing){
            this.message = message;
            this.name = name;
            this.incoming = incoming;
            this.outgoing = outgoing;
        }

        // FIXME complete copy constructor
        // need to make bitmap data copy contructor as well
        public PITEntry(PITEntry entry){
            this.message = entry.message;
            this.name = entry.name;
            this.incoming = entry.incoming;
            this.outgoing = entry.outgoing;
        }

        public boolean originator(String ip){
            return  ip.equals(incoming);
        }

        public SerialMessage getMessage() {
            return message;
        }

        public void setMessage(SerialMessage message) {
            this.message = message;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIncoming() {
            return incoming;
        }

        public void setIncoming(String incoming) {
            this.incoming = incoming;
        }

        public String getOutgoing() {
            return outgoing;
        }

        public void setOutgoing(String outgoing) {
            this.outgoing = outgoing;
        }
    }

}
