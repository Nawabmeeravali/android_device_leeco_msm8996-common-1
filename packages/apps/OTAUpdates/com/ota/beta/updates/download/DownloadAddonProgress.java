package com.ota.beta.updates.download;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.util.Log;
import com.ota.beta.updates.activities.AddonActivity.AddonsArrayAdapter;
import com.ota.beta.updates.utils.Constants;

public class DownloadAddonProgress extends AsyncTask<Long, Integer, Void> implements Constants {
    public final String TAG = getClass().getSimpleName();
    private DownloadManager mDownloadManager;
    private int mIndex;
    private boolean mIsRunning = true;

    public DownloadAddonProgress(Context context, DownloadManager downloadManager, int index) {
        this.mDownloadManager = downloadManager;
        this.mIndex = index;
    }

    protected void onCancelled() {
        this.mIsRunning = false;
    }

    protected Void doInBackground(Long... params) {
        int previousValue = 0;
        long mDownloadId = params[0].longValue();
        while (this.mIsRunning) {
            Query q = new Query();
            q.setFilterById(new long[]{mDownloadId});
            Cursor cursor = this.mDownloadManager.query(q);
            cursor.moveToFirst();
            try {
                if (cursor.getInt(cursor.getColumnIndex("status")) == 8 || cursor.getInt(cursor.getColumnIndex("status")) == 16) {
                    this.mIsRunning = false;
                }
                int progressPercent = (int) ((((long) cursor.getInt(cursor.getColumnIndex("bytes_so_far"))) * 100) / ((long) cursor.getInt(cursor.getColumnIndex("total_size"))));
                if (progressPercent != previousValue) {
                    publishProgress(new Integer[]{Integer.valueOf(progressPercent), Integer.valueOf(bytesDownloaded), Integer.valueOf(bytesInTotal)});
                    previousValue = progressPercent;
                }
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(this.TAG, " " + e.getMessage());
                this.mIsRunning = false;
            } catch (ArithmeticException e2) {
                Log.e(this.TAG, " " + e2.getMessage());
                this.mIsRunning = false;
            }
            cursor.close();
        }
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        if (this.mIsRunning) {
            AddonsArrayAdapter.updateProgress(this.mIndex, progress[0].intValue(), false);
        } else {
            AddonsArrayAdapter.updateProgress(this.mIndex, 0, true);
        }
    }
}
