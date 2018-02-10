package com.ota.beta.updates.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import com.ota.beta.updates.C0994R;
import com.ota.beta.updates.utils.Constants;
import com.ota.beta.updates.utils.Preferences;
import com.ota.beta.updates.utils.Tools;
import com.ota.beta.updates.utils.Utils;
import java.io.File;

@SuppressLint({"SdCardPath"})
public class SettingsActivity extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener, OnSharedPreferenceChangeListener, Constants {
    private static final String NOTIFICATIONS_IGNORED_RELEASE = "notifications_ignored_release";
    public final String TAG = getClass().getSimpleName();
    private Preference mAboutActivity;
    private Context mContext;
    private SwitchPreference mIgnoredRelease;
    private Preference mInstallPrefs;
    private Builder mInstallPrefsDialog;
    private SparseBooleanArray mInstallPrefsItems = new SparseBooleanArray();
    private Preference mProPreference;
    private RingtonePreference mRingtonePreference;
    private Preference mStorageLocation;
    private ListPreference mThemePref;

    class C10121 implements OnMultiChoiceClickListener {
        C10121() {
        }

        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            SettingsActivity.this.mInstallPrefsItems.put(which, isChecked);
        }
    }

    class C10132 implements OnClickListener {
        C10132() {
        }

        public void onClick(DialogInterface dialog, int id) {
            Preferences.setWipeData(SettingsActivity.this.mContext, SettingsActivity.this.mInstallPrefsItems.get(0));
            Preferences.setWipeCache(SettingsActivity.this.mContext, SettingsActivity.this.mInstallPrefsItems.get(1));
            Preferences.setWipeDalvik(SettingsActivity.this.mContext, SettingsActivity.this.mInstallPrefsItems.get(2));
            Preferences.setDeleteAfterInstall(SettingsActivity.this.mContext, SettingsActivity.this.mInstallPrefsItems.get(3));
        }
    }

    @SuppressLint({"NewApi"})
    public void onCreate(Bundle savedInstanceState) {
        this.mContext = this;
        setTheme(Preferences.getSettingsTheme(this.mContext));
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(Preferences.PREF_NAME);
        PreferenceManager.setDefaultValues(this, C0994R.xml.preferences, false);
        addPreferencesFromResource(C0994R.xml.preferences);
        this.mInstallPrefs = findPreference(Constants.INSTALL_PREFS);
        this.mInstallPrefs.setOnPreferenceClickListener(this);
        this.mAboutActivity = findPreference(Constants.ABOUT_ACTIVITY_PREF);
        this.mAboutActivity.setOnPreferenceClickListener(this);
        this.mRingtonePreference = (RingtonePreference) findPreference(Constants.NOTIFICATIONS_SOUND);
        this.mThemePref = (ListPreference) findPreference(Constants.CURRENT_THEME);
        this.mThemePref.setValue(Integer.toString(Preferences.getCurrentTheme(this.mContext)));
        setThemeSummary();
        setRingtoneSummary(getPreferenceManager().getSharedPreferences().getString(Constants.NOTIFICATIONS_SOUND, System.DEFAULT_NOTIFICATION_URI.toString()));
        if (!Tools.isRootAvailable()) {
            ((SwitchPreference) findPreference(Constants.UPDATER_ENABLE_ORS)).setEnabled(false);
        }
        this.mIgnoredRelease = (SwitchPreference) findPreference(NOTIFICATIONS_IGNORED_RELEASE);
        this.mIgnoredRelease.setOnPreferenceChangeListener(this);
        String ignoredRelease = Preferences.getIgnoredRelease(this.mContext);
        if (ignoredRelease.equalsIgnoreCase(Constants.THEME_LIGHT)) {
            setNotIgnore(false);
        } else {
            this.mIgnoredRelease.setSummary(new StringBuilder(String.valueOf(getResources().getString(C0994R.string.notification_ignoring_release))).append(" ").append(ignoredRelease).toString());
            this.mIgnoredRelease.setChecked(true);
            this.mIgnoredRelease.setEnabled(true);
            this.mIgnoredRelease.setSelectable(true);
        }
        this.mProPreference = findPreference(Constants.ABOUT_PREF_PRO);
        this.mProPreference.setOnPreferenceClickListener(this);
        Boolean isPro = Boolean.valueOf(Utils.isPackageInstalled("com.ota.updatespro", this.mContext));
        if (isPro.booleanValue()) {
            boolean z;
            this.mProPreference.setLayoutResource(C0994R.layout.preference_pro);
            this.mProPreference.setTitle(C0994R.string.about_pro_title);
            this.mProPreference.setSummary(C0994R.string.about_pro_summary);
            Preference preference = this.mProPreference;
            if (isPro.booleanValue()) {
                z = false;
            } else {
                z = true;
            }
            preference.setSelectable(z);
        } else {
            this.mProPreference.setLayoutResource(C0994R.layout.preference_no_pro);
            this.mProPreference.setTitle(C0994R.string.about_pro_title);
            this.mProPreference.setSummary(C0994R.string.about_non_pro_summary);
        }
        Preferences.setIsPro(this.mContext, isPro.booleanValue());
        this.mStorageLocation = findPreference(Constants.STORAGE_LOCATION);
        this.mStorageLocation.setSelectable(false);
        this.mStorageLocation.setSummary(SD_CARD + File.separator + OTA_DOWNLOAD_DIR);
    }

    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        this.mRingtonePreference.setOnPreferenceChangeListener(this);
    }

    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
            if (key.equals(Constants.CURRENT_THEME)) {
                Preferences.setTheme(this.mContext, listPref.getValue());
                startActivity(new Intent(this.mContext, MainActivity.class));
            } else if (key.equals(Constants.UPDATER_BACK_FREQ)) {
                Utils.setBackgroundCheck(this.mContext, Preferences.getBackgroundService(this.mContext));
            }
        } else if ((pref instanceof SwitchPreference) && key.equals(Constants.UPDATER_BACK_SERVICE)) {
            Utils.setBackgroundCheck(this.mContext, Preferences.getBackgroundService(this.mContext));
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        String otaPackage = "com.ota.updatespro";
        if (preference == this.mInstallPrefs) {
            showInstallPrefs();
        } else if (preference == this.mAboutActivity) {
            startActivity(new Intent(this.mContext, AboutActivity.class));
        } else if (preference == this.mProPreference) {
            String url = "https://play.google.com/store/apps/details?id=" + otaPackage;
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == this.mRingtonePreference) {
            setRingtoneSummary((String) newValue);
            return true;
        } else if (preference != this.mIgnoredRelease || ((Boolean) newValue).booleanValue()) {
            return false;
        } else {
            setNotIgnore(true);
            return false;
        }
    }

    private void setNotIgnore(boolean set) {
        if (set) {
            Preferences.setIgnoredRelease(this.mContext, Constants.THEME_LIGHT);
        }
        this.mIgnoredRelease.setSummary(getResources().getString(C0994R.string.notification_not_ignoring_release));
        this.mIgnoredRelease.setChecked(false);
        this.mIgnoredRelease.setEnabled(false);
        this.mIgnoredRelease.setSelectable(false);
    }

    private void showInstallPrefs() {
        boolean[] defaultValues = new boolean[]{Preferences.getWipeData(this.mContext), Preferences.getWipeCache(this.mContext), Preferences.getWipeDalvik(this.mContext), Preferences.getDeleteAfterInstall(this.mContext)};
        this.mInstallPrefsItems.put(0, wipeData);
        this.mInstallPrefsItems.put(1, wipeCache);
        this.mInstallPrefsItems.put(2, wipeDalvik);
        this.mInstallPrefsItems.put(3, deleteAfterInstall);
        this.mInstallPrefsDialog = new Builder(this.mContext);
        this.mInstallPrefsDialog.setTitle(C0994R.string.twrp_ors_install_prefs);
        this.mInstallPrefsDialog.setMultiChoiceItems(C0994R.array.ors_install_entries, defaultValues, new C10121());
        this.mInstallPrefsDialog.setPositiveButton(C0994R.string.ok, new C10132());
        this.mInstallPrefsDialog.show();
    }

    private void setRingtoneSummary(String soundValue) {
        CharSequence title;
        Ringtone tone = null;
        Uri soundUri = TextUtils.isEmpty(soundValue) ? null : Uri.parse(soundValue);
        if (soundUri != null) {
            tone = RingtoneManager.getRingtone(this, soundUri);
        }
        RingtonePreference ringtonePreference = this.mRingtonePreference;
        if (tone != null) {
            title = tone.getTitle(this);
        } else {
            title = getResources().getString(C0994R.string.silent_ringtone);
        }
        ringtonePreference.setSummary(title);
    }

    private void setThemeSummary() {
        int currentTheme = Preferences.getCurrentTheme(this.mContext);
        int id = 0;
        for (int i = 0; i < this.mThemePref.getEntryValues().length; i++) {
            if (this.mThemePref.getEntryValues()[i].equals(Integer.toString(currentTheme))) {
                id = i;
                break;
            }
        }
        this.mThemePref.setSummary(this.mThemePref.getEntries()[id]);
    }
}
