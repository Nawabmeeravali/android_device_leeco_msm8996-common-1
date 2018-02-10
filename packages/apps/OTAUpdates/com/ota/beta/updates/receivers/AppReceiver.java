package com.ota.beta.updates.receivers;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import com.google.android.gms.drive.DriveFile;
import com.ota.beta.updates.C0994R;
import com.ota.beta.updates.OtaUpdates;
import com.ota.beta.updates.RomUpdate;
import com.ota.beta.updates.activities.AddonActivity.AddonsArrayAdapter;
import com.ota.beta.updates.activities.AvailableActivity;
import com.ota.beta.updates.tasks.LoadUpdateManifest;
import com.ota.beta.updates.utils.Constants;
import com.ota.beta.updates.utils.Preferences;
import com.ota.beta.updates.utils.Utils;
import java.util.Iterator;

public class AppReceiver extends BroadcastReceiver implements Constants {
    public final String TAG = getClass().getSimpleName();

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle extras = intent.getExtras();
        long mRomDownloadID = Preferences.getDownloadID(context);
        if (action.equals("android.intent.action.DOWNLOAD_COMPLETE")) {
            long id = extras.getLong("extra_download_id");
            boolean isAddonDownload = false;
            int keyForAddonDownload = 0;
            Iterator<Integer> iterator = OtaUpdates.getAddonDownloadKeySet().iterator();
            while (iterator.hasNext() && !isAddonDownload) {
                int nextValue = ((Integer) iterator.next()).intValue();
                if (id == OtaUpdates.getAddonDownload(nextValue)) {
                    isAddonDownload = true;
                    keyForAddonDownload = nextValue;
                }
            }
            DownloadManager downloadManager;
            Query query;
            Cursor cursor;
            if (isAddonDownload) {
                downloadManager = (DownloadManager) context.getSystemService("download");
                query = new Query();
                query.setFilterById(new long[]{id});
                cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    if (8 != cursor.getInt(cursor.getColumnIndex("status"))) {
                        Log.d(this.TAG, "Removing Addon download with id " + keyForAddonDownload);
                        OtaUpdates.removeAddonDownload(keyForAddonDownload);
                        AddonsArrayAdapter.updateProgress(keyForAddonDownload, 0, true);
                        AddonsArrayAdapter.updateButtons(keyForAddonDownload, false);
                        return;
                    }
                    Log.d(this.TAG, "Removing Addon download with id " + keyForAddonDownload);
                    OtaUpdates.removeAddonDownload(keyForAddonDownload);
                    AddonsArrayAdapter.updateButtons(keyForAddonDownload, true);
                    return;
                }
                return;
            } else if (id == mRomDownloadID) {
                downloadManager = (DownloadManager) context.getSystemService("download");
                query = new Query();
                query.setFilterById(new long[]{id});
                cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    if (8 != cursor.getInt(cursor.getColumnIndex("status"))) {
                        Preferences.setDownloadFinished(context, false);
                        if (Utils.isLollipop()) {
                            AvailableActivity.setupMenuToolbar(context);
                            return;
                        } else {
                            AvailableActivity.invalidateMenu();
                            return;
                        }
                    }
                    Preferences.setDownloadFinished(context, true);
                    AvailableActivity.setupProgress(context);
                    if (Utils.isLollipop()) {
                        AvailableActivity.setupMenuToolbar(context);
                        return;
                    } else {
                        AvailableActivity.invalidateMenu();
                        return;
                    }
                }
                return;
            } else {
                return;
            }
        }
        if (action.equals("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED")) {
            long[] ids = extras.getLongArray("extra_click_download_ids");
            int length = ids.length;
            int i = 0;
            while (i < length) {
                if (ids[i] == mRomDownloadID) {
                    Intent i2 = new Intent(context, AvailableActivity.class);
                    i2.addFlags(DriveFile.MODE_READ_ONLY);
                    context.startActivity(i2);
                    i++;
                } else {
                    return;
                }
            }
        }
        if (action.equals(Constants.MANIFEST_CHECK_BACKGROUND)) {
            boolean updateAvailable = RomUpdate.getUpdateAvailability(context);
            String filename = RomUpdate.getFilename(context);
            if (updateAvailable) {
                Utils.setupNotification(context, filename);
                Utils.scheduleNotification(context, !Preferences.getBackgroundService(context));
            }
        }
        if (action.equals(Constants.START_UPDATE_CHECK)) {
            new LoadUpdateManifest(context, false).execute(new Void[0]);
        }
        if (action.equals("android.intent.action.BOOT_COMPLETED") && Preferences.getBackgroundService(context)) {
            Utils.scheduleNotification(context, !Preferences.getBackgroundService(context));
        }
        if (action.equals("com.ota.beta.update.IGNORE_RELEASE")) {
            Preferences.setIgnoredRelease(context, Integer.toString(RomUpdate.getVersionNumber(context)));
            NotificationManager mNotifyManager = (NotificationManager) context.getSystemService("notification");
            Builder builder = new Builder(context);
            builder.setContentTitle(context.getString(C0994R.string.main_release_ignored)).setSmallIcon(C0994R.drawable.ic_notif).setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0));
            mNotifyManager.notify(101, builder.build());
            final NotificationManager notificationManager = mNotifyManager;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    notificationManager.cancel(101);
                }
            }, 1500);
        }
    }
}
