package org.sacids.afyadataV2.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.sacids.afyadataV2.android.tasks.DownloadFeedbackTask;

public class FeedbackReceiver extends BroadcastReceiver {

    public FeedbackReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Invoke background service to process data
        Intent service = new Intent(context, DownloadFeedbackTask.class);
        context.startService(service);
    }
}
