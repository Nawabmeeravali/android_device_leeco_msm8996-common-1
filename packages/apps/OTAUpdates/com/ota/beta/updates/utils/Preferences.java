package com.ota.beta.updates.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings.System;
import android.util.Log;
import com.ota.beta.updates.C0994R;

public class Preferences implements Constants {
    public static String PREF_NAME = "OTAUpdateSettings";
    public static final String TAG = "Preferences";

    private Preferences() {
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, 0);
    }

    public static String getUpdateLastChecked(Context context, String time) {
        return getPrefs(context).getString(Constants.LAST_CHECKED, time);
    }

    public static boolean getDownloadFinished(Context context) {
        return getPrefs(context).getBoolean(Constants.IS_DOWNLOAD_FINISHED, false);
    }

    public static boolean getDeleteAfterInstall(Context context) {
        return getPrefs(context).getBoolean(Constants.DELETE_AFTER_INSTALL, false);
    }

    public static boolean getWipeData(Context context) {
        return getPrefs(context).getBoolean(Constants.WIPE_DATA, false);
    }

    public static boolean getWipeCache(Context context) {
        return getPrefs(context).getBoolean(Constants.WIPE_CACHE, true);
    }

    public static boolean getWipeDalvik(Context context) {
        return getPrefs(context).getBoolean(Constants.WIPE_DALVIK, true);
    }

    public static boolean getMD5Passed(Context context) {
        return getPrefs(context).getBoolean(Constants.MD5_PASSED, false);
    }

    public static boolean getHasMD5Run(Context context) {
        return getPrefs(context).getBoolean(Constants.MD5_RUN, false);
    }

    public static boolean getIsDownloadOnGoing(Context context) {
        return getPrefs(context).getBoolean(Constants.DOWNLOAD_RUNNING, false);
    }

    public static String getNetworkType(Context context) {
        return getPrefs(context).getString(Constants.NETWORK_TYPE, "2");
    }

    public static long getDownloadID(Context context) {
        return getPrefs(context).getLong(Constants.DOWNLOAD_ID, 0);
    }

    public static String getNotificationSound(Context context) {
        return getPrefs(context).getString(Constants.NOTIFICATIONS_SOUND, System.DEFAULT_NOTIFICATION_URI.toString());
    }

    public static Boolean getNotificationVibrate(Context context) {
        return Boolean.valueOf(getPrefs(context).getBoolean(Constants.NOTIFICATIONS_VIBRATE, true));
    }

    public static boolean getBackgroundService(Context context) {
        return getPrefs(context).getBoolean(Constants.UPDATER_BACK_SERVICE, true);
    }

    public static int getBackgroundFrequency(Context context) {
        return Integer.parseInt(getPrefs(context).getString(Constants.UPDATER_BACK_FREQ, "43200"));
    }

    public static boolean getORSEnabled(Context context) {
        Log.d(TAG, "ORS Enabled Preference " + getPrefs(context).getBoolean(Constants.UPDATER_ENABLE_ORS, false));
        return getPrefs(context).getBoolean(Constants.UPDATER_ENABLE_ORS, false);
    }

    public static int getCurrentTheme(Context context) {
        Boolean isDefaultThemeUsed = Utils.doesPropExist("ro.ota.default_theme");
        String getDefTheme = Utils.getProp("ro.ota.default_theme");
        Boolean isLollipop = Boolean.valueOf(Utils.isLollipop());
        if (!isDefaultThemeUsed.booleanValue() || getDefTheme.isEmpty()) {
            return normalTheme(context, isLollipop);
        }
        int defThemeInt = Integer.parseInt(getDefTheme);
        if (defThemeInt < 0 || defThemeInt > 2) {
            return normalTheme(context, isLollipop);
        }
        return Integer.parseInt(getPrefs(context).getString(Constants.CURRENT_THEME, getDefTheme));
    }

    private static int normalTheme(Context context, Boolean isLollipop) {
        if (isLollipop.booleanValue()) {
            return Integer.parseInt(getPrefs(context).getString(Constants.CURRENT_THEME, Constants.THEME_LIGHT));
        }
        return Integer.parseInt(getPrefs(context).getString(Constants.CURRENT_THEME, "2"));
    }

    public static int getTheme(Context context) {
        boolean isLollipop = Utils.isLollipop();
        switch (getCurrentTheme(context)) {
            case 0:
                return C0994R.style.Theme.RagnarLight;
            case 1:
                if (isLollipop) {
                    return C0994R.style.Theme.RagnarLight;
                }
                return C0994R.style.Theme.RagnarLight.DarkActionBar;
            case 2:
                return C0994R.style.Theme.RagnarDark;
            default:
                return isLollipop ? C0994R.style.Theme.RagnarLight : C0994R.style.Theme.RagnarDark;
        }
    }

    public static int getSettingsTheme(Context context) {
        switch (getCurrentTheme(context)) {
            case 0:
                return C0994R.style.Theme.RagnarLight.Settings;
            case 1:
                return C0994R.style.Theme.RagnarLight.DarkActionBar.Settings;
            case 2:
                return C0994R.style.Theme.RagnarDark.Settings;
            default:
                return Utils.isLollipop() ? C0994R.style.Theme.RagnarLight.Settings : C0994R.style.Theme.RagnarDark.Settings;
        }
    }

    public static String getIgnoredRelease(Context context) {
        return getPrefs(context).getString(Constants.IGNORE_RELEASE_VERSION, Constants.THEME_LIGHT);
    }

    public static Boolean getAdsEnabled(Context context) {
        return Boolean.valueOf(getPrefs(context).getBoolean(Constants.ADS_ENABLED, true));
    }

    public static String getOldChangelog(Context context) {
        return getPrefs(context).getString(Constants.OLD_CHANGELOG, context.getResources().getString(C0994R.string.app_version));
    }

    public static Boolean getFirstRun(Context context) {
        return Boolean.valueOf(getPrefs(context).getBoolean(Constants.FIRST_RUN, true));
    }

    public static Boolean getIsPro(Context context) {
        return Boolean.valueOf(getPrefs(context).getBoolean(Constants.IS_PRO, false));
    }

    public static void setUpdateLastChecked(Context context, String time) {
        Editor editor = getPrefs(context).edit();
        editor.putString(Constants.LAST_CHECKED, time);
        editor.commit();
    }

    public static void setDownloadFinished(Context context, boolean value) {
        Editor editor = getPrefs(context).edit();
        editor.putBoolean(Constants.IS_DOWNLOAD_FINISHED, value);
        editor.commit();
    }

    public static void setTheme(Context context, String value) {
        Editor editor = getPrefs(context).edit();
        editor.putString(Constants.CURRENT_THEME, value);
        editor.commit();
    }

    public static void setDeleteAfterInstall(Context context, boolean value) {
        Editor editor = getPrefs(context).edit();
        editor.putBoolean(Constants.DELETE_AFTER_INSTALL, value);
        editor.commit();
    }

    public static void setWipeData(Context context, boolean value) {
        Editor editor = getPrefs(context).edit();
        editor.putBoolean(Constants.WIPE_DATA, value);
        editor.commit();
    }

    public static void setWipeCache(Context context, boolean value) {
        Editor editor = getPrefs(context).edit();
        editor.putBoolean(Constants.WIPE_CACHE, value);
        editor.commit();
    }

    public static void setWipeDalvik(Context context, boolean value) {
        Editor editor = getPrefs(context).edit();
        editor.putBoolean(Constants.WIPE_DALVIK, value);
        editor.commit();
    }

    public static void setMD5Passed(Context context, boolean value) {
        Editor editor = getPrefs(context).edit();
        editor.putBoolean(Constants.MD5_PASSED, value);
        editor.commit();
    }

    public static void setHasMD5Run(Context context, boolean value) {
        Editor editor = getPrefs(context).edit();
        editor.putBoolean(Constants.MD5_RUN, value);
        editor.commit();
    }

    public static void setIsDownloadRunning(Context context, boolean value) {
        Editor editor = getPrefs(context).edit();
        editor.putBoolean(Constants.DOWNLOAD_RUNNING, value);
        editor.commit();
    }

    public static void setDownloadID(Context context, long value) {
        Editor editor = getPrefs(context).edit();
        editor.putLong(Constants.DOWNLOAD_ID, value);
        editor.commit();
    }

    public static void setBackgroundFrequency(Context context, String value) {
        Editor editor = getPrefs(context).edit();
        editor.putString(Constants.UPDATER_BACK_FREQ, value);
        editor.commit();
    }

    public static void setIgnoredRelease(Context context, String value) {
        Editor editor = getPrefs(context).edit();
        editor.putString(Constants.IGNORE_RELEASE_VERSION, value);
        editor.commit();
    }

    public static void setOldChangelog(Context context, String value) {
        Editor editor = getPrefs(context).edit();
        editor.putString(Constants.OLD_CHANGELOG, value);
        editor.commit();
    }

    public static void setFirstRun(Context context, boolean value) {
        Editor editor = getPrefs(context).edit();
        editor.putBoolean(Constants.FIRST_RUN, value);
        editor.commit();
    }

    public static void setIsPro(Context context, boolean value) {
        Editor editor = getPrefs(context).edit();
        editor.putBoolean(Constants.IS_PRO, value);
        editor.commit();
    }
}
