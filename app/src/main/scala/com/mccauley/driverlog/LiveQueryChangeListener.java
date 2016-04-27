package com.mccauley.driverlog;

import android.app.Activity;
import android.util.Log;

import com.couchbase.lite.LiveQuery;

import java.lang.ref.WeakReference;

public class LiveQueryChangeListener implements LiveQuery.ChangeListener {

    private final WeakReference<Activity> activityRef;
    private final WeakReference<LiveQueryAdapter> liveQueryAdapterRef;

    public LiveQueryChangeListener(Activity activity, LiveQueryAdapter liveQueryAdapter) {
        this.activityRef = new WeakReference<>(activity);
        this.liveQueryAdapterRef = new WeakReference<>(liveQueryAdapter);
    }

    @Override
    public void changed(final LiveQuery.ChangeEvent event) {
        if (activityRef.get() != null) {
            Activity activity = activityRef.get();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (liveQueryAdapterRef.get() != null) {
                        LiveQueryAdapter liveQueryAdapter = liveQueryAdapterRef.get();
                        Log.d("LiveQueryChangeListener", "changed");
                        liveQueryAdapter.setEnumerator(event.getRows());
                        Log.d("LiveQueryChangeListener", "setEnumerator");
                        liveQueryAdapter.notifyDataSetChanged();
                        Log.d("LiveQueryChangeListener", "notified changed");
                    }
                }
            });
        }
    }
}
