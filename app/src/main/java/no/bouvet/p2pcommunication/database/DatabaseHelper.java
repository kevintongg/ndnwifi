package no.bouvet.p2pcommunication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by micha on 3/20/2018.
 */

public class DatabaseHelper  extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ndn.db";
    private static final String TAG = "Database Helper";

    private static final String DATABASE_CREATE =
            "CREATE TABLE " +
                    DatabaseConfig.TABLE_NAME + " (" +
                    DatabaseConfig.NAME + " TEXT PRIMARY KEY, " +
                    DatabaseConfig.DIRECTORY + " TEXT NOT NULL, " +
                    DatabaseConfig.LAST_USED_TS + " DATETIME DEFAULT CURRENT_TIMESTAMP " + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseConfig.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DatabaseConfig.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB HELPER Created", "Create table command: " + DATABASE_CREATE);
        Log.d("DB HELPER Created", "Created the database");
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);

    }
}
