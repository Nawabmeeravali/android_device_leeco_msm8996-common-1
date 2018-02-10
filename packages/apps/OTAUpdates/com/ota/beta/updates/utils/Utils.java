package com.ota.beta.updates.utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import com.ota.beta.updates.C0994R;
import com.ota.beta.updates.RomUpdate;
import com.ota.beta.updates.activities.AvailableActivity;
import com.ota.beta.updates.activities.MainActivity;
import com.ota.beta.updates.receivers.AppReceiver;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

public class Utils implements Constants {
    private static int GB = (MB * KB);
    private static int KB = 1024;
    private static final int KILOBYTE = 1024;
    private static int MB = (KB * KB);
    public static final String TAG = "Utils";
    private static DecimalFormat decimalFormat = new DecimalFormat("##0.#");

    static {
        decimalFormat.setMaximumIntegerDigits(3);
        decimalFormat.setMaximumFractionDigits(1);
    }

    public static Boolean doesPropExist(String propName) {
        boolean valid = false;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop").getInputStream()));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                } else if (line.contains("[" + propName + "]")) {
                    valid = true;
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Boolean.valueOf(valid);
    }

    public static String getProp(String propName) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new ProcessBuilder(new String[]{"/system/bin/getprop", propName}).redirectErrorStream(true).start().getInputStream()));
            String str = "";
            while (true) {
                str = br.readLine();
                if (str == null) {
                    break;
                }
                result = str;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String formatDataFromBytes(long size) {
        KB = 1024;
        String symbol = "B";
        if (size < ((long) KB)) {
            return new StringBuilder(String.valueOf(decimalFormat.format(size))).append(symbol).toString();
        }
        if (size < ((long) MB)) {
            return new StringBuilder(String.valueOf(decimalFormat.format((double) (((float) size) / ((float) KB))))).append('k').append(symbol).toString();
        }
        if (size < ((long) GB)) {
            return new StringBuilder(String.valueOf(decimalFormat.format((double) (((float) size) / ((float) MB))))).append('M').append(symbol).toString();
        }
        return new StringBuilder(String.valueOf(decimalFormat.format((double) (((float) size) / ((float) GB))))).append('G').append(symbol).toString();
    }

    public static void deleteFile(File file) {
        Tools.shell("rm -f " + file.getAbsolutePath(), false);
    }

    public static void setHasFileDownloaded(Context context) {
        File file = RomUpdate.getFullFile(context);
        int filesize = RomUpdate.getFileSize(context);
        boolean downloadIsRunning = Preferences.getIsDownloadOnGoing(context);
        boolean status = false;
        if (!(file.length() == 0 || file.length() != ((long) filesize) || downloadIsRunning)) {
            status = true;
        }
        Preferences.setDownloadFinished(context, status);
    }

    public static void setBackgroundCheck(Context context, boolean set) {
        scheduleNotification(context, !set);
    }

    public static void scheduleNotification(Context context, boolean cancel) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService("alarm");
        Intent intent = new Intent(context, AppReceiver.class);
        intent.setAction(Constants.START_UPDATE_CHECK);
        if (!cancel) {
            alarmManager.set(0, Calendar.getInstance().getTimeInMillis() + ((long) (Preferences.getBackgroundFrequency(context) * 1000)), PendingIntent.getBroadcast(context, 1673, intent, 134217728));
        } else if (alarmManager != null) {
            alarmManager.cancel(PendingIntent.getBroadcast(context, 1673, intent, 134217728));
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        if (cm == null) {
            return false;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } else {
            return false;
        }
    }

    public static boolean isMobileNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        if (cm == null) {
            return false;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getType() == 0;
        } else {
            return false;
        }
    }

    public static boolean isLollipop() {
        return VERSION.SDK_INT >= 21;
    }

    private static boolean versionBiggerThan(String current, String manifest) {
        int i;
        if (current.length() > manifest.length()) {
            for (i = 0; i < current.length() - manifest.length(); i++) {
                manifest = new StringBuilder(String.valueOf(manifest)).append(Constants.THEME_LIGHT).toString();
            }
        } else if (manifest.length() > current.length()) {
            for (i = 0; i < manifest.length() - current.length(); i++) {
                current = new StringBuilder(String.valueOf(current)).append(Constants.THEME_LIGHT).toString();
            }
        }
        if (Integer.parseInt(current) < Integer.parseInt(manifest)) {
            return true;
        }
        return false;
    }

    public static boolean isUpdateIgnored(Context context) {
        return Preferences.getIgnoredRelease(context).matches(Integer.toString(RomUpdate.getVersionNumber(context)));
    }

    public static void setUpdateAvailability(Context context) {
        boolean available;
        int otaBetaVersion = RomUpdate.getVersionNumber(context);
        String currentbetaVer = getProp(Constants.OTA_VERSION);
        String betamanifestVer = Integer.toString(otaBetaVersion);
        if (Preferences.getIgnoredRelease(context).matches(betamanifestVer)) {
            available = false;
        } else {
            available = versionBiggerThan(currentbetaVer, betamanifestVer);
        }
        RomUpdate.setUpdateAvailable(context, available);
    }

    public static void setupNotification(Context context, String filename) {
        NotificationManager mNotifyManager = (NotificationManager) context.getSystemService("notification");
        Builder mBuilder = new Builder(context);
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, 134217728);
        Intent skipIntent = new Intent(context, AppReceiver.class);
        skipIntent.setAction(Constants.IGNORE_RELEASE);
        Intent downloadIntent = new Intent(context, AvailableActivity.class);
        mBuilder.setContentTitle(context.getString(C0994R.string.update_available)).setContentText(filename).setSmallIcon(C0994R.drawable.ic_notif).setContentIntent(resultPendingIntent).setAutoCancel(true).setPriority(1).setDefaults(4).setVisibility(1).setSound(Uri.parse(Preferences.getNotificationSound(context))).addAction(C0994R.drawable.ic_action_download, context.getString(C0994R.string.download), PendingIntent.getActivity(context, 0, downloadIntent, 134217728)).addAction(C0994R.drawable.ic_action_close, context.getString(C0994R.string.ignore), PendingIntent.getBroadcast(context, 0, skipIntent, 134217728));
        if (Preferences.getNotificationVibrate(context).booleanValue()) {
            mBuilder.setDefaults(2);
        }
        mNotifyManager.notify(101, mBuilder.build());
    }

    public static boolean isPackageInstalled(String packagename, Context context) {
        try {
            context.getPackageManager().getPackageInfo(packagename, 1);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static String getRemovableMediaPath() {
        return Tools.shell("echo ${SECONDARY_STORAGE%%:*}", false);
    }
}
