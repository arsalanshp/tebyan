package net.tebyan.filesharingapp.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent notificationTickServiceIntent = new Intent(context,
				NotificationTickService.class);
		context.startService(notificationTickServiceIntent);
	}

}
