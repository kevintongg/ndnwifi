package com.example.android.wifidirect.measure;

import android.content.Context;

import com.example.android.wifidirect.ndn.Caching;

public class BandwidthDBHelper {
    private Caching db;

    public BandwidthDBHelper(Context context){
        db = new Caching(context);
    }

    public void addBandwidthEntry(int dataSize) {
        long timestamp = System.currentTimeMillis();
        db.insertBandwidthEntry(dataSize, timestamp);
    }

}