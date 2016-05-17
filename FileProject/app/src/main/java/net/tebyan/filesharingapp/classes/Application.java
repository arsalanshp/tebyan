package net.tebyan.filesharingapp.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;

/**
 * Created by F.piri on 1/17/2016.
 */
public class Application extends android.app.Application {
    public static String CurrentFolder;
    public static String ParrentFolder;
    public static String UploadFolder;
    public static String fileIdClicked = "";
    public static boolean fileCopied = false;
    public static boolean fileMoved = false;
    public static String fileIdToPase = "";
    public static String fileIdSelected = "";
    public static int serviceInterval = 1;
    public static AppCompatActivity currentActivity;

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                "net.tebyan.filesharingapp.activities", Context.MODE_PRIVATE);
        return prefs.getString("TOKEN", "null").substring(1, prefs.getString("TOKEN", "null").length() - 1);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        CurrentFolder = "";
        ParrentFolder = "";
        currentActivity = null;
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF",
                "fonts/IRAN Sans Light.ttf");
    }
}
