package com.ota.beta.updates.download;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.util.Log;
import com.ota.beta.updates.activities.AvailableActivity;
import com.ota.beta.updates.activities.MainActivity;
import com.ota.beta.updates.utils.Constants;
import com.ota.beta.updates.utils.Preferences;

public class DownloadRomProgress extends AsyncTask<Long, Integer, Void> implements Constants {
    public final String TAG = getClass().getSimpleName();
    private Context mContext;
    private DownloadManager mDownloadManager;

    public DownloadRomProgress(Context context, DownloadManager downloadManager) {
        this.mContext = context;
        this.mDownloadManager = downloadManager;
    }

    protected Void doInBackground(Long... params) {
        int previousValue = 0;
        while (Preferences.getIsDownloadOnGoing(this.mContext)) {
            long mDownloadID = Preferences.getDownloadID(this.mContext);
            Query q = new Query();
            q.setFilterById(new long[]{mDownloadID});
            Cursor cursor = this.mDownloadManager.query(q);
            cursor.moveToFirst();
            try {
                int bytesDownloaded = cursor.getInt(cursor.getColumnIndex("bytes_so_far"));
                int bytesInTotal = cursor.getInt(cursor.getColumnIndex("total_size"));
                if (cursor.getInt(cursor.getColumnIndex("status")) == 8) {
                    Preferences.setIsDownloadRunning(this.mContext, false);
                }
                int progressPercent = (int) ((((long) bytesDownloaded) * 100) / ((long) bytesInTotal));
                if (progressPercent != previousValue) {
                    publishProgress(new Integer[]{Integer.valueOf(progressPercent), Integer.valueOf(bytesDownloaded), Integer.valueOf(bytesInTotal)});
                    previousValue = progressPercent;
                }
            } catch (CursorIndexOutOfBoundsException e) {
                Preferences.setIsDownloadRunning(this.mContext, false);
            } catch (ArithmeticException e2) {
                Preferences.setIsDownloadRunning(this.mContext, false);
                Log.e(this.TAG, " " + e2.getStackTrace());
            }
            cursor.close();
        }
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        if (Preferences.getIsDownloadOnGoing(this.mContext)) {
            AvailableActivity.updateProgress(progress[0].intValue(), progress[1].intValue(), progress[2].intValue(), this.mContext);
            MainActivity.updateProgress(progress[0].intValue(), progress[1].intValue(), progress[2].intValue(), (Activity) this.mContext);
        }
    }
}
