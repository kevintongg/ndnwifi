package com.example.android.wifidirect.measure;
import android.provider.BaseColumns;

/**
 * Created by tj on 2/17/16.
 */
public class TimerDBConfig implements BaseColumns{

    public static final String TABLE_NAME = "interest_timer";
    public static final String ID = "id";
    public static final String INTEREST = "interest";
    public static final String START_TS = "start_ts";
    public static final String STOP_TS = "stop_ts";
    public static final String DATABASE_NAME = "fujitsu.db";

}
