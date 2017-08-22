package com.example.android.wifidirect.ndn;
import android.provider.BaseColumns;
/**
 * Created by tj on 2/17/16.
 */
public class DatabaseConfiguration implements BaseColumns{

    public static final String TABLE_NAME = "cache";
    public static final String NAME = "interest";
    public static final String DIRECTORY = "directory";
    public static final String LAST_USED_TS = "last_used_ts";
    public static final String DATABASE_NAME = "fujitsu.db";

    public static final String TIMER_TABLE_NAME = "interest_timer";
    public static final String TIMER_ID = "id";
    public static final String TIMER_INTEREST = "interest";
    public static final String TIMER_START_TS = "start_ts";
    public static final String TIMER_STOP_TS = "stop_ts";

    public static final String BANDWIDTH_TABLE_NAME = "bandwidth_usage";
    public static final String BANDWIDTH_ID = "id";
    public static final String BANDWIDTH_TS = "timestamp";
    public static final String BANDWIDTH_BYTE_SIZE = "byte_size";

}
