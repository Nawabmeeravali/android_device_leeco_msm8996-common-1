package com.ota.beta.updates.download;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import com.ota.beta.updates.C0994R;
import com.ota.beta.updates.RomUpdate;
import com.ota.beta.updates.utils.Constants;
import com.ota.beta.updates.utils.Preferences;
import com.ota.beta.updates.utils.Utils;
import java.io.File;

public class DownloadRom implements Constants {
    public static final String TAG = "DownloadRomUpdate";

    public void startDownload(Context context) {
        String url = RomUpdate.getDirectUrl(context);
        String fileName = RomUpdate.getFilename(context) + ".zip";
        String description = context.getResources().getString(C0994R.string.downloading);
        File file = RomUpdate.getFullFile(context);
        Request request = new Request(Uri.parse(url));
        if (Preferences.getNetworkType(context).equals("2")) {
            request.setAllowedNetworkTypes(2);
        }
        request.setTitle(fileName);
        request.setDescription(description);
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(0);
        request.setDestinationInExternalPublicDir(OTA_DOWNLOAD_DIR, fileName);
        Utils.deleteFile(file);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService("download");
        Preferences.setDownloadID(context, downloadManager.enqueue(request));
        Preferences.setIsDownloadRunning(context, true);
        new DownloadRomProgress(context, downloadManager).execute(new Long[]{Long.valueOf(mDownloadID)});
        Preferences.setMD5Passed(context, false);
        Preferences.setHasMD5Run(context, false);
    }

    public void cancelDownload(Context context) {
        long mDownloadID = Preferences.getDownloadID(context);
        ((DownloadManager) context.getSystemService("download")).remove(new long[]{mDownloadID});
        Preferences.setIsDownloadRunning(context, false);
    }
}
