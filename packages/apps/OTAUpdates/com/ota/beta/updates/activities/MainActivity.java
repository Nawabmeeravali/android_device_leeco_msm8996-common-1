package com.ota.beta.updates.activities;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ota.beta.updates.C0994R;
import com.ota.beta.updates.RomUpdate;
import com.ota.beta.updates.tasks.Changelog;
import com.ota.beta.updates.tasks.LoadUpdateManifest;
import com.ota.beta.updates.utils.Constants;
import com.ota.beta.updates.utils.Preferences;
import com.ota.beta.updates.utils.Utils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements Constants {
    private static Context mContext;
    public static ProgressBar mProgressBar;
    public final String TAG = getClass().getSimpleName();
    private boolean isLollipop;
    private AdRequest mAdRequest;
    private AdView mAdView;
    private Builder mCompatibilityDialog;
    private Builder mDonateDialog;
    private Builder mPlayStoreDialog;
    private BroadcastReceiver mReceiver = new C10051();

    class C10051 extends BroadcastReceiver {
        C10051() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.ota.update.MANIFEST_LOADED")) {
                MainActivity.this.updateDonateLinkLayout();
                MainActivity.this.updateAddonsLayout();
                MainActivity.this.updateRomInformation();
                MainActivity.this.updateRomUpdateLayouts();
                MainActivity.this.updateWebsiteLayout();
            }
        }
    }

    class C10062 implements OnClickListener {
        C10062() {
        }

        public void onClick(DialogInterface dialog, int which) {
            ((Activity) MainActivity.mContext).finish();
        }
    }

    class C10073 implements OnClickListener {
        C10073() {
        }

        public void onClick(DialogInterface dialog, int which) {
            MainActivity.this.finish();
        }
    }

    class C10084 implements OnClickListener {
        C10084() {
        }

        public void onClick(DialogInterface dialog, int which) {
            String url = "";
            if (((AlertDialog) dialog).getListView().getCheckedItemPosition() == 0) {
                url = RomUpdate.getDonateLink(MainActivity.mContext);
            } else {
                url = RomUpdate.getBitCoinLink(MainActivity.mContext);
            }
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            try {
                MainActivity.this.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                MainActivity.this.mPlayStoreDialog.show();
            }
        }
    }

    class C10095 implements OnClickListener {
        C10095() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    class C10106 implements OnClickListener {
        C10106() {
        }

        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("https://play.google.com/store/search?q=bitcoin%20wallet&c=apps"));
            MainActivity.this.startActivity(intent);
        }
    }

    class C10117 implements OnClickListener {
        C10117() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    public class CompatibilityTask extends AsyncTask<Void, Boolean, Boolean> implements Constants {
        public final String TAG = getClass().getSimpleName();
        private Context mContext;
        private String mPropName;

        public CompatibilityTask(Context context) {
            this.mContext = context;
            this.mPropName = this.mContext.getResources().getString(C0994R.string.prop_name);
        }

        protected Boolean doInBackground(Void... v) {
            return Utils.doesPropExist(this.mPropName);
        }

        protected void onPostExecute(Boolean result) {
            if (result.booleanValue()) {
                new LoadUpdateManifest(this.mContext, true).execute(new Void[0]);
            } else {
                try {
                    MainActivity.this.mCompatibilityDialog.show();
                } catch (BadTokenException ex) {
                    Log.e(this.TAG, ex.getMessage());
                }
            }
            super.onPostExecute(result);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        mContext = this;
        setTheme(Preferences.getTheme(mContext));
        this.isLollipop = Utils.isLollipop();
        super.onCreate(savedInstanceState);
        setContentView(C0994R.layout.ota_main);
        if (this.isLollipop) {
            Toolbar toolbar = (Toolbar) findViewById(C0994R.id.toolbar_main);
            setActionBar(toolbar);
            toolbar.setTitle(getResources().getString(C0994R.string.app_name));
        } else {
            ActionBar actionBar = getActionBar();
            actionBar.setTitle(C0994R.string.app_name);
            actionBar.setCustomView(LayoutInflater.from(this).inflate(C0994R.layout.ota_main_actionbar_top, null), new LayoutParams(-2, -2, 8388629));
            actionBar.setDisplayShowCustomEnabled(true);
        }
        if (Preferences.getFirstRun(mContext).booleanValue()) {
            Preferences.setFirstRun(mContext, false);
            showWhatsNew();
        }
        if (!Preferences.getOldChangelog(mContext).equals(getResources().getString(C0994R.string.app_version))) {
            showWhatsNew();
        }
        new File(SD_CARD + File.separator + OTA_DOWNLOAD_DIR + File.separator + Constants.INSTALL_AFTER_FLASH_DIR).mkdirs();
        createDialogs();
        if (Utils.isConnected(mContext)) {
            new CompatibilityTask(mContext).execute(new Void[0]);
        } else {
            new Builder(mContext).setTitle(C0994R.string.main_not_connected_title).setMessage(C0994R.string.main_not_connected_message).setPositiveButton(C0994R.string.ok, new C10062()).show();
        }
        Utils.setHasFileDownloaded(mContext);
        updateDonateLinkLayout();
        updateAddonsLayout();
        updateRomInformation();
        updateRomUpdateLayouts();
        updateWebsiteLayout();
        if (Preferences.getAdsEnabled(mContext).booleanValue()) {
            try {
                this.mAdView = (AdView) findViewById(C0994R.id.adView);
                this.mAdRequest = new AdRequest.Builder().build();
                this.mAdView.loadAd(this.mAdRequest);
            } catch (NullPointerException e) {
                Log.e(this.TAG, e.getMessage());
            }
        }
    }

    public void onStart() {
        super.onStart();
        registerReceiver(this.mReceiver, new IntentFilter(Constants.MANIFEST_LOADED));
    }

    public void onStop() {
        super.onStop();
        unregisterReceiver(this.mReceiver);
    }

    public void onResume() {
        super.onResume();
        if (this.mAdView != null) {
            this.mAdView.resume();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.mAdView != null) {
            this.mAdView.pause();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.isLollipop) {
            getMenuInflater().inflate(C0994R.menu.ota_menu_main, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.isLollipop) {
            switch (item.getItemId()) {
                case C0994R.id.menu_changelog:
                    openChangelog(null);
                    return true;
                case C0994R.id.menu_settings:
                    openSettings(null);
                    return true;
            }
        }
        return false;
    }

    private void createDialogs() {
        this.mCompatibilityDialog = new Builder(mContext);
        this.mCompatibilityDialog.setCancelable(false);
        this.mCompatibilityDialog.setTitle(C0994R.string.main_not_compatible_title);
        this.mCompatibilityDialog.setMessage(C0994R.string.main_not_compatible_message);
        this.mCompatibilityDialog.setPositiveButton(C0994R.string.ok, new C10073());
        this.mDonateDialog = new Builder(this);
        this.mDonateDialog.setTitle(getResources().getString(C0994R.string.donate)).setSingleChoiceItems(new String[]{"PayPal", "BitCoin"}, 0, null).setPositiveButton(getResources().getString(C0994R.string.ok), new C10084()).setNegativeButton(getResources().getString(C0994R.string.cancel), new C10095());
        this.mPlayStoreDialog = new Builder(mContext);
        this.mPlayStoreDialog.setCancelable(true);
        this.mPlayStoreDialog.setTitle(C0994R.string.main_playstore_title);
        this.mPlayStoreDialog.setMessage(C0994R.string.main_playstore_message);
        this.mPlayStoreDialog.setPositiveButton(C0994R.string.ok, new C10106());
        this.mPlayStoreDialog.setNegativeButton(getResources().getString(C0994R.string.cancel), new C10117());
    }

    private void updateRomUpdateLayouts() {
        CardView updateAvailable = (CardView) findViewById(C0994R.id.layout_main_update_available);
        CardView updateNotAvailable = (CardView) findViewById(C0994R.id.layout_main_no_update_available);
        updateAvailable.setVisibility(8);
        updateNotAvailable.setVisibility(8);
        TextView updateAvailableSummary = (TextView) findViewById(C0994R.id.main_tv_update_available_summary);
        TextView updateNotAvailableSummary = (TextView) findViewById(C0994R.id.main_tv_no_update_available_summary);
        mProgressBar = (ProgressBar) findViewById(C0994R.id.bar_main_progress_bar);
        mProgressBar.setVisibility(8);
        if (RomUpdate.getUpdateAvailability(mContext) || (!RomUpdate.getUpdateAvailability(mContext) && Utils.isUpdateIgnored(mContext))) {
            updateAvailable.setVisibility(0);
            TextView updateAvailableTitle = (TextView) findViewById(C0994R.id.main_tv_update_available_title);
            String htmlColorOpen;
            if (Preferences.getDownloadFinished(mContext)) {
                updateAvailableTitle.setText(getResources().getString(C0994R.string.main_update_finished));
                htmlColorOpen = "";
                if (!this.isLollipop) {
                    htmlColorOpen = "<font color='#33b5e5'>";
                } else if (Preferences.getCurrentTheme(mContext) == 0) {
                    htmlColorOpen = "<font color='#009688'>";
                } else {
                    htmlColorOpen = "<font color='#80cbc4'>";
                }
                updateAvailableSummary.setText(Html.fromHtml(new StringBuilder(String.valueOf(RomUpdate.getVersionName(mContext))).append("<br />").append(htmlColorOpen).append(getResources().getString(C0994R.string.main_download_completed_details)).append("</font>").toString()));
                return;
            } else if (Preferences.getIsDownloadOnGoing(mContext)) {
                updateAvailableTitle.setText(getResources().getString(C0994R.string.main_update_progress));
                mProgressBar.setVisibility(0);
                htmlColorOpen = "";
                if (!this.isLollipop) {
                    htmlColorOpen = "<font color='#33b5e5'>";
                } else if (Preferences.getCurrentTheme(mContext) == 0) {
                    htmlColorOpen = "<font color='#009688'>";
                } else {
                    htmlColorOpen = "<font color='#80cbc4'>";
                }
                updateAvailableSummary.setText(Html.fromHtml(new StringBuilder(String.valueOf(htmlColorOpen)).append(getResources().getString(C0994R.string.main_tap_to_view_progress)).append("</font>").toString()));
                return;
            } else {
                updateAvailableTitle.setText(getResources().getString(C0994R.string.main_update_available));
                htmlColorOpen = "";
                if (!this.isLollipop) {
                    htmlColorOpen = "<font color='#33b5e5'>";
                } else if (Preferences.getCurrentTheme(mContext) == 0) {
                    htmlColorOpen = "<font color='#009688'>";
                } else {
                    htmlColorOpen = "<font color='#80cbc4'>";
                }
                updateAvailableSummary.setText(Html.fromHtml(new StringBuilder(String.valueOf(RomUpdate.getVersionName(mContext))).append("<br />").append(htmlColorOpen).append(getResources().getString(C0994R.string.main_tap_to_download)).append("</font>").toString()));
                return;
            }
        }
        updateNotAvailable.setVisibility(0);
        boolean is24 = DateFormat.is24HourFormat(mContext);
        Date now = new Date();
        Locale locale = Locale.getDefault();
        String time = "";
        if (is24) {
            time = new SimpleDateFormat("d, MMMM HH:mm", locale).format(now);
        } else {
            time = new SimpleDateFormat("d, MMMM hh:mm a", locale).format(now);
        }
        Preferences.setUpdateLastChecked(this, time);
        updateNotAvailableSummary.setText(getString(C0994R.string.main_last_checked) + " " + time);
    }

    private void updateAddonsLayout() {
        CardView addonsLink = (CardView) findViewById(C0994R.id.layout_main_addons);
        addonsLink.setVisibility(8);
        if (RomUpdate.getAddonsCount(mContext) > 0) {
            addonsLink.setVisibility(0);
        }
    }

    private void updateDonateLinkLayout() {
        CardView donateLink = (CardView) findViewById(C0994R.id.layout_main_dev_donate_link);
        donateLink.setVisibility(8);
        if (!RomUpdate.getDonateLink(mContext).trim().equals("null") || !RomUpdate.getBitCoinLink(mContext).trim().equals("null")) {
            donateLink.setVisibility(0);
        }
    }

    private void updateWebsiteLayout() {
        CardView webLink = (CardView) findViewById(C0994R.id.layout_main_dev_website);
        webLink.setVisibility(8);
        if (!RomUpdate.getWebsite(mContext).trim().equals("null")) {
            webLink.setVisibility(0);
        }
    }

    private void updateRomInformation() {
        String htmlColorOpen = "";
        if (!this.isLollipop) {
            htmlColorOpen = "<font color='#33b5e5'>";
        } else if (Preferences.getCurrentTheme(mContext) == 0) {
            htmlColorOpen = "<font color='#009688'>";
        } else {
            htmlColorOpen = "<font color='#80cbc4'>";
        }
        String htmlColorClose = "</font>";
        ((TextView) findViewById(C0994R.id.tv_main_rom_name)).setText(Html.fromHtml(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(getApplicationContext().getResources().getString(C0994R.string.main_rom_name))).append(" ").toString())).append(htmlColorOpen).append(Utils.getProp(Constants.OTA_ROMNAME)).append(htmlColorClose).toString()));
        ((TextView) findViewById(C0994R.id.tv_main_rom_version)).setText(Html.fromHtml(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(getApplicationContext().getResources().getString(C0994R.string.main_rom_version))).append(" ").toString())).append(htmlColorOpen).append(Utils.getProp(Constants.OTA_VERSION)).append(htmlColorClose).toString()));
        ((TextView) findViewById(C0994R.id.tv_main_rom_date)).setText(Html.fromHtml(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(getApplicationContext().getResources().getString(C0994R.string.main_rom_build_date))).append(" ").toString())).append(htmlColorOpen).append(Utils.getProp("ro.build.date")).append(htmlColorClose).toString()));
        ((TextView) findViewById(C0994R.id.tv_main_android_version)).setText(Html.fromHtml(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(getApplicationContext().getResources().getString(C0994R.string.main_android_verison))).append(" ").toString())).append(htmlColorOpen).append(Utils.getProp("ro.build.version.release")).append(htmlColorClose).toString()));
        TextView romDeveloper = (TextView) findViewById(C0994R.id.tv_main_rom_developer);
        romDeveloper.setVisibility(!RomUpdate.getDeveloper(this).equals("null") ? 0 : 8);
        romDeveloper.setText(Html.fromHtml(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(getApplicationContext().getResources().getString(C0994R.string.main_rom_developer))).append(" ").toString())).append(htmlColorOpen).append(RomUpdate.getDeveloper(this)).append(htmlColorClose).toString()));
    }

    public void openCheckForUpdates(View v) {
        new LoadUpdateManifest(mContext, true).execute(new Void[0]);
    }

    public void openDownload(View v) {
        startActivity(new Intent(mContext, AvailableActivity.class));
    }

    public void openAddons(View v) {
        startActivity(new Intent(mContext, AddonActivity.class));
    }

    public void openDonationPage(View v) {
        boolean payPalLinkAvailable = RomUpdate.getDonateLink(mContext).trim().equals("null");
        boolean bitCoinLinkAvailable = RomUpdate.getBitCoinLink(mContext).trim().equals("null");
        if (!payPalLinkAvailable && !bitCoinLinkAvailable) {
            this.mDonateDialog.show();
        } else if (!payPalLinkAvailable && bitCoinLinkAvailable) {
            url = RomUpdate.getDonateLink(mContext);
            intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } else if (payPalLinkAvailable && !bitCoinLinkAvailable) {
            url = RomUpdate.getBitCoinLink(mContext);
            intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    public void openWebsitePage(View v) {
        String url = RomUpdate.getWebsite(mContext);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void openSettings(View v) {
        startActivity(new Intent(mContext, SettingsActivity.class));
    }

    public void openChangelog(View v) {
        new Changelog(this, mContext, getResources().getString(C0994R.string.changelog), RomUpdate.getChangelog(mContext), false).execute(new Void[0]);
    }

    private void showWhatsNew() {
        new Changelog(this, mContext, getResources().getString(C0994R.string.changelog), getResources().getString(C0994R.string.changelog_url), true).execute(new Void[0]);
    }

    public static void updateProgress(int progress, int downloaded, int total, Activity activity) {
        if (mProgressBar != null) {
            mProgressBar.setProgress(progress);
        }
    }
}
