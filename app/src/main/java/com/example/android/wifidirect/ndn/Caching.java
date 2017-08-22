package com.example.android.wifidirect.ndn;

/**
 * Created by Owner on 2/4/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class Caching extends SQLiteOpenHelper {
    public static String TAG = "caching";
    public static final int DATABASE_VERSION = 2;
    private static String[] projection = {DatabaseConfiguration.NAME, DatabaseConfiguration.DIRECTORY};

    private static final String DATABASE_CREATE =
            "CREATE TABLE " +
                    DatabaseConfiguration.TABLE_NAME + " (" +
                    DatabaseConfiguration.NAME + " TEXT PRIMARY KEY, " +
                    DatabaseConfiguration.DIRECTORY + " TEXT NOT NULL, " +
                    DatabaseConfiguration.LAST_USED_TS + " DATETIME DEFAULT CURRENT_TIMESTAMP " + ")";

    private static final String timerTableCreate =
            "CREATE TABLE " +
                    DatabaseConfiguration.TIMER_TABLE_NAME + " (" +
                    DatabaseConfiguration.TIMER_ID + " INTEGER PRIMARY KEY, " +
                    DatabaseConfiguration.TIMER_INTEREST + " TEXT NOT NULL, " +
                    DatabaseConfiguration.TIMER_START_TS + " INTEGER NOT NULL, " +
                    DatabaseConfiguration.TIMER_STOP_TS + " INTEGER NOT NULL  " + ")";

    private static final String bandwidthTableCreate =
            "CREATE TABLE " +
                    DatabaseConfiguration.BANDWIDTH_TABLE_NAME + " (" +
                    DatabaseConfiguration.BANDWIDTH_ID + " INTEGER PRIMARY KEY, " +
                    DatabaseConfiguration.BANDWIDTH_TS + " INTEGER NOT NULL, " +
                    DatabaseConfiguration.BANDWIDTH_BYTE_SIZE + " INTEGER NOT NULL  " + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseConfiguration.TABLE_NAME;

    private static final String timerDeleteEntries =
            "DROP TABLE IF EXISTS " + DatabaseConfiguration.TIMER_TABLE_NAME;

    private static final String bandwidthDeleteEntries =
            "DROP TABLE IF EXISTS " + DatabaseConfiguration.BANDWIDTH_TABLE_NAME;

    public Caching(Context context) {
        super(context, DatabaseConfiguration.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB HELPER Created", "Create table command: " + DATABASE_CREATE);
        Log.d("DB HELPER Created", "Created the database");
        db.execSQL(DATABASE_CREATE);
        db.execSQL(timerTableCreate);
        db.execSQL(bandwidthTableCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(timerDeleteEntries);
        db.execSQL(bandwidthDeleteEntries);
    }

    public void insertString(String key, String value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseConfiguration.NAME, key);
        cv.put(DatabaseConfiguration.DIRECTORY, value);
        db.insert(DatabaseConfiguration.TABLE_NAME, null, cv);
        db.close();
    }

    public boolean addRow(String name, String directory) {
        String lcWord = name.toLowerCase();
        if (containsKey(lcWord)) {
            return false;
        } else {
            insertString(lcWord, directory);
            return true;
        }
    }

    public boolean containsKey(String entry) {
        SQLiteDatabase db = getReadableDatabase();

        String q = "Select * from " + DatabaseConfiguration.TABLE_NAME + " where " + DatabaseConfiguration.NAME + " == " + "'" + entry + "'";
        Log.i("Caching line 115: ", q);
        Cursor cursor = db.rawQuery(q, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }

    public String getDirectory(String key) {
        SQLiteDatabase db = getReadableDatabase();

        String q = "Select * from " + DatabaseConfiguration.TABLE_NAME + " where " +
                DatabaseConfiguration.NAME + " == " + "'" + key + "'";
        Log.i("Line 97 query", q);
        Cursor cursor = db.rawQuery(q, null);
        cursor.moveToFirst();
        String directory = cursor.getString(cursor.getColumnIndex(DatabaseConfiguration.DIRECTORY));
        Log.i("Total Col in cursor", directory);
        cursor.close();
        db.close();
        return directory;
    }

    public void updateLastAccessTimestamp(String name){
        String where = DatabaseConfiguration.NAME + " = ?";
        String[] whereArgs = {name};
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseConfiguration.LAST_USED_TS, dateTime());
        db.update(DatabaseConfiguration.TABLE_NAME, cv, where, whereArgs);
        db.close();
    }

    private String dateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String pastTimestamp(long millisAgo){
        long currentMillis = new Date().getTime();
        Date oldestTimestamp = new Date(currentMillis - millisAgo);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(oldestTimestamp);
    }

    public void removeStaleInterests(long maxMimaxMillisStale){
        // TODO must also remove the files from the filesystem
        String where = DatabaseConfiguration.LAST_USED_TS + " < ?";
        String timestamp = pastTimestamp(maxMimaxMillisStale);
        Log.d(TAG, timestamp);
        String[] whereArgs = {timestamp};
        SQLiteDatabase db = getWritableDatabase();
        int deleteCount = db.delete(DatabaseConfiguration.TABLE_NAME, where, whereArgs);
        Log.i(TAG, String.format("removed %d stale interest records from the database",deleteCount));
        db.close();
    }


    public void insertTimerTimestamp(String interest, Long start, Long stop) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseConfiguration.TIMER_INTEREST, interest);
        cv.put(DatabaseConfiguration.TIMER_START_TS, start);
        cv.put(DatabaseConfiguration.TIMER_STOP_TS, stop);
        long rowsAdded = db.insert(DatabaseConfiguration.TIMER_TABLE_NAME, null, cv);
        Log.d(TAG, String.format("%d rows were added", rowsAdded));
        db.close();
    }

    public void insertBandwidthEntry(int dataSize, long timestamp){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseConfiguration.BANDWIDTH_BYTE_SIZE, dataSize);
        cv.put(DatabaseConfiguration.BANDWIDTH_TS, timestamp);
        long rowsAdded = db.insert(DatabaseConfiguration.BANDWIDTH_TABLE_NAME, null, cv);
        Log.d(TAG, String.format("%d rows were added", rowsAdded));
        db.close();
    }
}