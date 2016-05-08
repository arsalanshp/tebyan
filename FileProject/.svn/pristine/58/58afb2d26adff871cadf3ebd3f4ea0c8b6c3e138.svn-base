package net.tebyan.filesharingapp.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by macbookpro on 2/11/16.
 */
public class TickReciever extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {
         Intent serviceIntent = new Intent(context, NotificationTickService.class);
        context.startService(serviceIntent);
    }
}
