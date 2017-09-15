package com.example.android.wifidirect.measure;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by tj on 4/2/16.
 */
public class Timer {
    private Map<String, Long> timestamps;

    public Timer(){
        timestamps = new HashMap<>();
    }

    public void start(String interest){
        timestamps.put(interest, System.currentTimeMillis());
    }

    public StartStop stop(String interest){
        if(!timestamps.containsKey(interest)){
            return null;
        } else{
            StartStop st = new StartStop(timestamps.get(interest), System.currentTimeMillis());
            timestamps.remove(interest);
            return  st;
        }
    }

    public static class StartStop {
        private final Long start;
        private final Long stop;

        public StartStop(Long start, Long stop){
            this.start = start;
            this.stop = stop;
        }

        public Long getStart(){
            return this.start;
        }

        public Long getStop(){
            return this.stop;
        }

    }
}
