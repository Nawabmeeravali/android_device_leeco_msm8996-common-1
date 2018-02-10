package com.ota.beta.updates;

import android.app.Application;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import java.util.Set;

public class OtaUpdates extends Application {
    private static ArrayMap<Integer, Long> mAddonsDownloads = new ArrayMap();

    public static void putAddonDownload(int key, long value) {
        Log.d("OtaUpdates", "Putting Addon with Key: " + key + " and Value: " + value);
        mAddonsDownloads.put(Integer.valueOf(key), Long.valueOf(value));
    }

    public static long getAddonDownload(int key) {
        Log.d("OtaUpdates", "Getting Addon with Key: " + key);
        return ((Long) mAddonsDownloads.get(Integer.valueOf(key))).longValue();
    }

    public static long getAddonDownloadValueAtIndex(int index) {
        return ((Long) mAddonsDownloads.get(mAddonsDownloads.valueAt(index))).longValue();
    }

    public static void removeAddonDownload(int key) {
        mAddonsDownloads.remove(Integer.valueOf(key));
    }

    public static Set<Integer> getAddonDownloadKeySet() {
        return mAddonsDownloads.keySet();
    }
}
