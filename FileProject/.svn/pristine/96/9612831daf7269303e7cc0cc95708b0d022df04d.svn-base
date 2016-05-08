package net.tebyan.filesharingapp.classes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;
import net.tebyan.filesharingapp.model.Events;

public class NotificationService extends Service {

    private WakeLock mWakeLock;
    private String TAG = "TEBYANNET";

    /**
     * Simply return null, since our Service will not be communicating with any
     * other components. It just does its work silently.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * This is where we initialize. We call this when onStart/onStartCommand is
     * called by the system. We won't do anything with the intent here, and you
     * probably won't, either.
     */
    private void handleIntent(Intent intent) {
        // obtain the wake lock
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWakeLock.acquire();
        // check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        Log.e("NOTIF_SERVICE", "handleIntent");

        if (!cm.getBackgroundDataSetting()) {

            Log.e("NOTIF_SERVICE", "stopSelf");

            stopSelf();
            return;
        }

        // do the actual work, in a separate thread
        GetNotifications(getApplicationContext());
    }

    public void GetNotifications(final Context context) {
        Ion.with(context).load("POST", WebserviceUrl.RepositoryServiceUrl + "GetNotifications").as(Events.class).setCallback(new FutureCallback<Events>() {
            @Override
            public void onCompleted(Exception e, Events events) {
                if (events.Data != null && events.Error == null && e == null) {
                    Boolean applicationIsRunningOnNetworkActivity = false;
                    if (Application.currentActivity != null) {
                        applicationIsRunningOnNetworkActivity = MainActivity.class
                                .isAssignableFrom(Application.currentActivity
                                        .getClass());
                    }
                    if (applicationIsRunningOnNetworkActivity) {
                        MainActivity activity = (MainActivity) Application.currentActivity;
                        activity.setNotifCount(Integer.parseInt(events.Data.ShareMe));
                    } else {
                        if (Integer.parseInt(events.Data.ShareMe) > 0) {

                            Intent notificationIntent = new Intent(
                                    getApplicationContext(), MainActivity.class);

                            notificationIntent
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                            PendingIntent intent = PendingIntent.getActivity(
                                    getApplicationContext(), 0, notificationIntent, 0);

                            String text = getResources()
                                    .getString(R.string.app_name);

                            Uri alarmSound = RingtoneManager
                                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                                    getApplicationContext())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setNumber(Integer.parseInt(events.Data.ShareMe))
                                    .setContentTitle(text)
                                    .setContentIntent(intent)
                                    .setContentText(events.Data.ShareMe + getString(R.string.shared))
                                    .setSound(alarmSound);

                            if (events.ThumbUrl!=null) {
                                if (events.ThumbUrl != null) {
                                    Ion.with(getApplicationContext()).load(events.ThumbUrl).asBitmap().setCallback(new FutureCallback<Bitmap>() {
                                        @Override
                                        public void onCompleted(Exception e, Bitmap bitmap) {
                                            Drawable thumb = new BitmapDrawable(bitmap);
                                            mBuilder.setLargeIcon(bitmap);
                                        }
                                    });
                                }

                            }

                            int mNotificationId = 77;
                            // Gets an instance of the NotificationManager
                            // service
                            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            // Builds the notification and issues it.

                            Notification notification = mBuilder.build();

                            notification.flags |= Notification.FLAG_AUTO_CANCEL;

                            mNotifyMgr.notify(mNotificationId, notification);

                            stopSelf();
                        }
                    }
                } else {
                    Log.e("error", e.getMessage());
                    Toast.makeText(context, R.string.network_connection_fail, Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }


    /**
     * This is deprecated, but you have to implement it if you're planning on
     * supporting devices with an API level lower than 5 (Android 2.0).
     */
    @Override
    public void onStart(Intent intent, int startId) {
        Log.e("NOTIF_SERVICE", "onStart");
        handleIntent(intent);
    }

    /**
     * This is called on 2.0+ (API level 5 or higher). Returning
     * START_NOT_STICKY tells the system to not restart the service if it is
     * killed because of poor resource (memory/cpu) conditions.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("NOTIF_SERVICE", "onStartCommand");
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    /**
     * In onDestroy() we release our wake lock. This ensures that whenever the
     * Service stops (killed for resources, stopSelf() called, etc.), the wake
     * lock will be released.
     */
    @Override
    public void onDestroy() {
        Log.e("NOTIF_SERVICE", "onDestroy");
        super.onDestroy();
        mWakeLock.release();
    }
}
