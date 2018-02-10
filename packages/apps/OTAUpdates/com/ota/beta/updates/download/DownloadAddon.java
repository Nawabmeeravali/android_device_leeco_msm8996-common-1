package com.ota.beta.updates.download;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import com.ota.beta.updates.OtaUpdates;
import com.ota.beta.updates.utils.Constants;
import com.ota.beta.updates.utils.Preferences;

public class DownloadAddon implements Constants {
    public static final String TAG = "DownloadAddon";

    public void startDownload(Context context, String url, String fileName, int id, int index) {
        Request request = new Request(Uri.parse(url));
        if (Preferences.getNetworkType(context).equals("2")) {
            request.setAllowedNetworkTypes(2);
        }
        request.setTitle(fileName);
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(0);
        request.setDestinationInExternalPublicDir(OTA_DOWNLOAD_DIR, new StringBuilder(String.valueOf(fileName)).append(".zip").toString());
        DownloadManager downloadManager = (DownloadManager) context.getSystemService("download");
        OtaUpdates.putAddonDownload(index, downloadManager.enqueue(request));
        new DownloadAddonProgress(context, downloadManager, index).execute(new Long[]{Long.valueOf(mDownloadID)});
    }

    public void cancelDownload(Context context, int index) {
        long mDownloadID = OtaUpdates.getAddonDownload(index);
        ((DownloadManager) context.getSystemService("download")).remove(new long[]{mDownloadID});
    }
}
