package com.example.android.wifidirect.db;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.wifidirect.ndn.Caching;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by tj on 3/31/16.
 */
public class DBCleanControl extends Fragment{
    public static String TAG = "dbcleancontrol";
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void cleanUp() {
        final Runnable clean = new Runnable() {
            @Override
            public void run() {
                // TODO call the cleaning function
                Log.d(TAG, "performing a cleanup");
                CleanupTask task = new CleanupTask();
                task.execute();
            }
        };
        final ScheduledFuture cleanHandle = scheduler.scheduleAtFixedRate(clean, 60 * 30, 60 * 30, TimeUnit.SECONDS);
    }

    public class CleanupTask extends AsyncTask<String, Integer, Long> {

        protected Long doInBackground(String... strings){
            long staleness = 60L * 30 * 1000; // milliseconds
            Context context = getActivity().getApplicationContext();
            Caching db = new Caching(context);
            db.removeStaleInterests(staleness);
            return 0L;
        }

    }
}
