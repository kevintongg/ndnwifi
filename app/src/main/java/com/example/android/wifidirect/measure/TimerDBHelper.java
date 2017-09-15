package com.example.android.wifidirect.measure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.android.wifidirect.ndn.Caching;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import static com.example.android.wifidirect.measure.TimerDBConfig.*;

public class TimerDBHelper {
    private Caching db;

    public TimerDBHelper(Context context){
        db = new Caching(context);
    }

    public void addTimestamp(String interest, Long start, Long stop) {
        String lcWord = interest.toLowerCase();
        db.insertTimerTimestamp(lcWord, start, stop);
    }

}