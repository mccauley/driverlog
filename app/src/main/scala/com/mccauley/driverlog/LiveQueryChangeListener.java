package com.mccauley.driverlog;

import android.app.Activity;

import com.couchbase.lite.LiveQuery;

public class LiveQueryChangeListener implements LiveQuery.ChangeListener {

    private final Activity activity;
    private final LiveQueryAdapter liveQueryAdapter;

    public LiveQueryChangeListener(Activity activity, LiveQueryAdapter liveQueryAdapter) {
        this.activity = activity;
        this.liveQueryAdapter = liveQueryAdapter;
    }

    @Override
    public void changed(final LiveQuery.ChangeEvent event) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                liveQueryAdapter.setEnumerator(event.getRows());
                liveQueryAdapter.notifyDataSetChanged();
            }
        });
    }
}
