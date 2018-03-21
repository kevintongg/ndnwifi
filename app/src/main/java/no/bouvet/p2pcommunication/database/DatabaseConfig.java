package no.bouvet.p2pcommunication.database;

import android.provider.BaseColumns;

/**
 * Created by micha on 3/20/2018.
 */

public class DatabaseConfig implements BaseColumns{
    public static final String TABLE_NAME = "cache";
    public static final String NAME = "interest";
    public static final String DIRECTORY = "directory";
    public static final String LAST_USED_TS = "last_used_ts";
    public static final String DATABASE_NAME = "ndn.db";
}
