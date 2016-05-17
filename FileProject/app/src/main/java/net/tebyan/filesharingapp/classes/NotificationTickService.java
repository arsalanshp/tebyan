package net.tebyan.filesharingapp.classes;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import net.tebyan.filesharingapp.R;
import net.tebyan.filesharingapp.activities.MainActivity;
import net.tebyan.filesharingapp.model.Events;

import java.util.List;

public class NotificationTickService extends IntentService {

    private static BroadcastReceiver tickReceiver;
    private static ConnectionChangeReceiver wifiWatcher;

    public NotificationTickService(String name) {
        super(name);
    }


    public NotificationTickService() {
        super("NotificationTickService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("ewrwqeq", "qweqweqwwq run shod");
        GetNotifications(getApplicationContext());
    }

    private void registerWifiWatcher() {
        if (wifiWatcher == null)
            wifiWatcher = new ConnectionChangeReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(wifiWatcher, intentFilter);
    }

    public void GetNotifications(final Context context) {
        Ion.with(context)
                .load(WebserviceUrl.RepositoryServiceUrl + "GetNotifications")
                .setHeader("userToken", Application.getToken(context))
                .setHeader("checkToken", "true")
                .as(Events.class)
                .setCallback(new FutureCallback<Events>() {
                    @Override
                    public void onCompleted(Exception e, Events events) {
                        if (events != null && events.Data != null && events.Error == null && e == null) {
                            Boolean applicationIsRunningOnNetworkActivity = false;
                            /*if (Application.currentActivity != null) {
                                applicationIsRunningOnNetworkActivity = MainActivity.class
                                        .isAssignableFrom(Application.currentActivity
                                                .getClass());
                            }*/
                            if (applicationIsRunningOnNetworkActivity) {
                                MainActivity activity = (MainActivity) Application.currentActivity;
                                activity.setNotifCount(Integer.parseInt(events.Data.ShareMe));
                            } else {
                                if (Integer.parseInt(events.Data.ShareMe) > 0) {

                                    Intent notificationIntent = new Intent(
                                            getApplicationContext(), MainActivity.class);
                                    notificationIntent.putExtra("FileID", "");
                                    notificationIntent.putExtra("deletedFiles", false);
                                    notificationIntent.putExtra("sharedWithMe", true);
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

                                    if (events.ThumbUrl != null) {
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
                                    Ion.with(context)
                                            .load(WebserviceUrl.RepositoryServiceUrl + "SetNoteShareReaded")
                                            .setHeader("userToken", Application.getToken(context))
                                            .setHeader("checkToken", "true")
                                            .asString()
                                            .setCallback(new FutureCallback<String>() {
                                                @Override
                                                public void onCompleted(Exception e, String result) {
                                                }
                                            });
                                }
                                if (Integer.parseInt(events.Data.NewFriendJoinedCount) > 0) {

                                    Intent notificationIntent = new Intent(
                                            getApplicationContext(), MainActivity.class);
                                    notificationIntent.putExtra("FileID", "");
                                    notificationIntent.putExtra("deletedFiles", false);
                                    notificationIntent.putExtra("sharedWithMe", true);
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

                                    if (events.ThumbUrl != null) {
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
                                    Ion.with(context)
                                            .load(WebserviceUrl.RepositoryServiceUrl + "SetNoteShareReaded")
                                            .setHeader("userToken", Application.getToken(context))
                                            .setHeader("checkToken", "true")
                                            .asString()
                                            .setCallback(new FutureCallback<String>() {
                                                @Override
                                                public void onCompleted(Exception e, String result) {
                                                }
                                            });
                                }
                            }
                        } else {
                            if (events != null) {
                                //Log.e("error", e.getMessage());
                                Toast.makeText(context, R.string.network_connection_fail, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                });
    }

    public boolean isForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager
                .getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equalsIgnoreCase(
                getPackageName().trim());
    }
}
