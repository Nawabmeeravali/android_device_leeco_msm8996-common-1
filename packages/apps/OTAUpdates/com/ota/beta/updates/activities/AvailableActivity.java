package com.ota.beta.updates.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.ota.beta.updates.C0994R;
import com.ota.beta.updates.RomUpdate;
import com.ota.beta.updates.download.DownloadRom;
import com.ota.beta.updates.download.DownloadRomProgress;
import com.ota.beta.updates.tasks.GenerateRecoveryScript;
import com.ota.beta.updates.utils.Constants;
import com.ota.beta.updates.utils.Preferences;
import com.ota.beta.updates.utils.Tools;
import com.ota.beta.updates.utils.Utils;
import in.uncod.android.bypass.Bypass;

public class AvailableActivity extends Activity implements Constants, OnClickListener {
    public static final String TAG = "AvailableActivity";
    private static Button mCancelButton;
    private static Button mCheckMD5Button;
    private static Context mContext;
    private static Button mDeleteButton;
    private static Button mDownloadButton;
    private static Button mInstallButton;
    public static ProgressBar mProgressBar;
    public static TextView mProgressCounterText;
    private Builder mDeleteDialog;
    private DownloadRom mDownloadRom;
    private Builder mNetworkDialog;
    private Builder mRebootDialog;
    private Builder mRebootManualDialog;

    class C10021 implements DialogInterface.OnClickListener {
        C10021() {
        }

        public void onClick(DialogInterface dialog, int which) {
            Utils.deleteFile(RomUpdate.getFullFile(AvailableActivity.mContext));
            Preferences.setHasMD5Run(AvailableActivity.mContext, false);
            Preferences.setDownloadFinished(AvailableActivity.mContext, false);
            AvailableActivity.this.setupUpdateNameInfo();
            AvailableActivity.setupProgress(AvailableActivity.mContext);
            AvailableActivity.this.setupMd5Info();
            if (Utils.isLollipop()) {
                AvailableActivity.setupMenuToolbar(AvailableActivity.mContext);
            } else {
                AvailableActivity.invalidateMenu();
            }
        }
    }

    class C10032 implements DialogInterface.OnClickListener {
        C10032() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (Preferences.getORSEnabled(AvailableActivity.mContext)) {
                new GenerateRecoveryScript(AvailableActivity.mContext).execute(new Void[0]);
            } else {
                Tools.recovery(AvailableActivity.mContext);
            }
        }
    }

    class C10043 implements DialogInterface.OnClickListener {
        C10043() {
        }

        public void onClick(DialogInterface dialog, int which) {
            AvailableActivity.mContext.startActivity(new Intent(AvailableActivity.mContext, SettingsActivity.class));
        }
    }

    public class MD5Check extends AsyncTask<Object, Boolean, Boolean> {
        public final String TAG = getClass().getSimpleName();
        Context mContext;
        ProgressDialog mMD5CheckDialog;

        public MD5Check(Context context) {
            this.mContext = context;
        }

        protected void onPreExecute() {
            this.mMD5CheckDialog = new ProgressDialog(this.mContext);
            this.mMD5CheckDialog.setCancelable(false);
            this.mMD5CheckDialog.setIndeterminate(true);
            this.mMD5CheckDialog.setMessage(this.mContext.getString(C0994R.string.available_checking_md5));
            this.mMD5CheckDialog.show();
        }

        protected Boolean doInBackground(Object... params) {
            return Boolean.valueOf(Tools.shell("md5sum " + RomUpdate.getFullFile(this.mContext).getAbsolutePath() + " | cut -d ' ' -f 1", false).trim().equalsIgnoreCase(RomUpdate.getMd5(this.mContext).trim()));
        }

        protected void onPostExecute(Boolean result) {
            this.mMD5CheckDialog.cancel();
            if (result.booleanValue()) {
                Toast.makeText(this.mContext, this.mContext.getString(C0994R.string.available_md5_ok), 1).show();
            } else {
                Toast.makeText(this.mContext, this.mContext.getString(C0994R.string.available_md5_failed), 1).show();
            }
            Preferences.setMD5Passed(this.mContext, result.booleanValue());
            if (Utils.isLollipop()) {
                AvailableActivity.setupMenuToolbar(this.mContext);
            } else {
                AvailableActivity.invalidateMenu();
            }
            super.onPostExecute(result);
        }
    }

    @SuppressLint({"NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        setTheme(Preferences.getTheme(mContext));
        super.onCreate(savedInstanceState);
        setContentView(C0994R.layout.ota_available);
        if (Utils.isLollipop()) {
            ((Toolbar) findViewById(C0994R.id.toolbar_available_bottom)).setTitle("");
        }
        this.mDownloadRom = new DownloadRom();
        mProgressBar = (ProgressBar) findViewById(C0994R.id.bar_available_progress_bar);
        mProgressCounterText = (TextView) findViewById(C0994R.id.tv_available_progress_counter);
        if (Utils.isLollipop()) {
            mCheckMD5Button = (Button) findViewById(C0994R.id.menu_available_check_md5);
            mDeleteButton = (Button) findViewById(C0994R.id.menu_available_delete);
            mInstallButton = (Button) findViewById(C0994R.id.menu_available_install);
            mDownloadButton = (Button) findViewById(C0994R.id.menu_available_download);
            mCancelButton = (Button) findViewById(C0994R.id.menu_available_cancel);
            mCheckMD5Button.setOnClickListener(this);
            mDeleteButton.setOnClickListener(this);
            mInstallButton.setOnClickListener(this);
            mDownloadButton.setOnClickListener(this);
            mCancelButton.setOnClickListener(this);
        }
        setupDialogs();
        setupUpdateNameInfo();
        setupProgress(mContext);
        setupMd5Info();
        setupRomHut();
        setupChangeLog();
        if (Utils.isLollipop()) {
            setupMenuToolbar(mContext);
        }
        if (Preferences.getIsDownloadOnGoing(mContext)) {
            new DownloadRomProgress(mContext, (DownloadManager) mContext.getSystemService("download")).execute(new Long[0]);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0994R.menu.ota_menu_available, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case C0994R.id.menu_available_check_md5:
                new MD5Check(mContext).execute(new Object[0]);
                Preferences.setHasMD5Run(mContext, true);
                return true;
            case C0994R.id.menu_available_delete:
                this.mDeleteDialog.show();
                return true;
            case C0994R.id.menu_available_download:
                download();
                return true;
            case C0994R.id.menu_available_cancel:
                this.mDownloadRom.cancelDownload(mContext);
                setupUpdateNameInfo();
                setupProgress(mContext);
                invalidateOptionsMenu();
                return true;
            case C0994R.id.menu_available_install:
                if (Tools.isRootAvailable()) {
                    this.mRebootDialog.show();
                    return true;
                }
                this.mRebootManualDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(2097152);
        startActivity(intent);
        super.onBackPressed();
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem downloadMenuItem = menu.findItem(C0994R.id.menu_available_download);
        MenuItem cancelMenuItem = menu.findItem(C0994R.id.menu_available_cancel);
        MenuItem installMenuItem = menu.findItem(C0994R.id.menu_available_install);
        MenuItem md5MenuItem = menu.findItem(C0994R.id.menu_available_check_md5);
        MenuItem deleteMenuItem = menu.findItem(C0994R.id.menu_available_delete);
        boolean downloadFinished = Preferences.getDownloadFinished(mContext);
        boolean downloadIsRunning = Preferences.getIsDownloadOnGoing(mContext);
        boolean md5HasRun = Preferences.getHasMD5Run(mContext);
        boolean md5Passed = Preferences.getMD5Passed(mContext);
        if (downloadFinished) {
            if (RomUpdate.getMd5(mContext).equals("null")) {
                md5MenuItem.setEnabled(false);
            } else if (md5HasRun && md5Passed) {
                md5MenuItem.setEnabled(false);
                md5MenuItem.setTitle(C0994R.string.available_md5_ok);
            } else if (md5HasRun && !md5Passed) {
                md5MenuItem.setEnabled(false);
                md5MenuItem.setTitle(C0994R.string.available_md5_failed);
            } else if (!md5HasRun) {
                md5MenuItem.setEnabled(true);
            }
            deleteMenuItem.setEnabled(true);
            downloadMenuItem.setVisible(false);
            cancelMenuItem.setVisible(false);
            installMenuItem.setVisible(true);
        } else if (downloadIsRunning) {
            downloadMenuItem.setVisible(false);
            cancelMenuItem.setVisible(true);
            installMenuItem.setVisible(false);
        } else {
            downloadMenuItem.setVisible(true);
            cancelMenuItem.setVisible(false);
            installMenuItem.setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0994R.id.menu_available_check_md5:
                new MD5Check(mContext).execute(new Object[0]);
                Preferences.setHasMD5Run(mContext, true);
                return;
            case C0994R.id.menu_available_delete:
                this.mDeleteDialog.show();
                return;
            case C0994R.id.menu_available_download:
                download();
                return;
            case C0994R.id.menu_available_cancel:
                this.mDownloadRom.cancelDownload(mContext);
                setupUpdateNameInfo();
                setupProgress(mContext);
                setupMenuToolbar(mContext);
                return;
            case C0994R.id.menu_available_install:
                if (Tools.isRootAvailable()) {
                    this.mRebootDialog.show();
                    return;
                } else {
                    this.mRebootManualDialog.show();
                    return;
                }
            default:
                return;
        }
    }

    private void setupDialogs() {
        this.mDeleteDialog = new Builder(mContext);
        this.mDeleteDialog.setTitle(C0994R.string.are_you_sure).setMessage(C0994R.string.available_delete_confirm_message).setPositiveButton(C0994R.string.ok, new C10021()).setNegativeButton(C0994R.string.cancel, null);
        this.mRebootDialog = new Builder(mContext);
        this.mRebootDialog.setTitle(C0994R.string.are_you_sure).setMessage(C0994R.string.available_reboot_confirm).setPositiveButton(C0994R.string.ok, new C10032()).setNegativeButton(C0994R.string.cancel, null);
        this.mNetworkDialog = new Builder(mContext);
        this.mNetworkDialog.setTitle(C0994R.string.available_wrong_network_title).setMessage(C0994R.string.available_wrong_network_message).setPositiveButton(C0994R.string.ok, null).setNeutralButton(C0994R.string.settings, new C10043());
        this.mRebootManualDialog = new Builder(mContext);
        this.mRebootManualDialog.setTitle(C0994R.string.available_reboot_manual_title).setMessage(C0994R.string.available_reboot_manual_message).setPositiveButton(C0994R.string.cancel, null);
    }

    public static void setupMenuToolbar(Context context) {
        boolean downloadFinished = Preferences.getDownloadFinished(context);
        boolean downloadIsRunning = Preferences.getIsDownloadOnGoing(context);
        boolean md5HasRun = Preferences.getHasMD5Run(context);
        boolean md5Passed = Preferences.getMD5Passed(context);
        mDeleteButton.setEnabled(false);
        mCheckMD5Button.setEnabled(false);
        if (downloadFinished) {
            if (RomUpdate.getMd5(context).equals("null")) {
                mCheckMD5Button.setClickable(false);
            } else if (md5HasRun && md5Passed) {
                mCheckMD5Button.setEnabled(false);
                mCheckMD5Button.setText(C0994R.string.available_md5_ok);
            } else if (md5HasRun && !md5Passed) {
                mCheckMD5Button.setEnabled(false);
                mCheckMD5Button.setText(C0994R.string.available_md5_failed);
            } else if (!md5HasRun) {
                mCheckMD5Button.setEnabled(true);
            }
            mDeleteButton.setEnabled(true);
            mDownloadButton.setVisibility(8);
            mCancelButton.setVisibility(8);
            mInstallButton.setVisibility(0);
        } else if (downloadIsRunning) {
            mDownloadButton.setVisibility(8);
            mCancelButton.setVisibility(0);
            mInstallButton.setVisibility(8);
        } else {
            mDownloadButton.setVisibility(0);
            mCancelButton.setVisibility(8);
            mInstallButton.setVisibility(8);
        }
    }

    private void setupChangeLog() {
        TextView changelogView = (TextView) findViewById(C0994R.id.tv_available_changelog_content);
        changelogView.setText(new Bypass(this).markdownToSpannable(RomUpdate.getChangelog(mContext)));
        changelogView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupRomHut() {
        String domainText = RomUpdate.getUrlDomain(mContext);
        boolean isRomHut = domainText.contains("romhut.com");
        if (domainText != null) {
            TextView domainTextView = (TextView) findViewById(C0994R.id.tv_available_romhut);
            domainTextView.setText(new StringBuilder(String.valueOf(isRomHut ? "Sponsored by " : "")).append(domainText).toString());
            if (Utils.isLollipop()) {
                int color;
                if (Preferences.getCurrentTheme(mContext) == 0) {
                    color = getResources().getColor(C0994R.color.material_deep_teal_500);
                } else {
                    color = getResources().getColor(C0994R.color.material_deep_teal_200);
                }
                domainTextView.setTextColor(color);
                return;
            }
            domainTextView.setTextColor(getResources().getColor(C0994R.color.holo_blue_light));
        }
    }

    private void setupUpdateNameInfo() {
        boolean isDownloadOnGoing = Preferences.getIsDownloadOnGoing(mContext);
        TextView updateNameInfoText = (TextView) findViewById(C0994R.id.tv_available_update_name);
        String downloading = getResources().getString(C0994R.string.available_downloading);
        String filename = RomUpdate.getVersionName(mContext);
        if (Utils.isLollipop()) {
            int color;
            if (Preferences.getCurrentTheme(mContext) == 0) {
                color = getResources().getColor(C0994R.color.material_deep_teal_500);
            } else {
                color = getResources().getColor(C0994R.color.material_deep_teal_200);
            }
            updateNameInfoText.setTextColor(color);
        } else {
            updateNameInfoText.setTextColor(getResources().getColor(C0994R.color.holo_blue_light));
        }
        if (isDownloadOnGoing) {
            updateNameInfoText.setText(downloading);
        } else {
            updateNameInfoText.setText(filename);
        }
    }

    private void setupMd5Info() {
        TextView md5Text = (TextView) findViewById(C0994R.id.tv_available_md5);
        String md5Prefix = getResources().getString(C0994R.string.available_md5);
        String md5 = RomUpdate.getMd5(mContext);
        if (md5.equals("null")) {
            md5Text.setText(new StringBuilder(String.valueOf(md5Prefix)).append(" N/A").toString());
        } else {
            md5Text.setText(new StringBuilder(String.valueOf(md5Prefix)).append(" ").append(md5).toString());
        }
    }

    private void download() {
        String httpUrl = RomUpdate.getHttpUrl(mContext);
        String directUrl = RomUpdate.getDirectUrl(mContext);
        String error = getResources().getString(C0994R.string.available_url_error);
        boolean isMobile = Utils.isMobileNetwork(mContext);
        boolean isSettingWiFiOnly = Preferences.getNetworkType(mContext).equals("2");
        if (isMobile && isSettingWiFiOnly) {
            this.mNetworkDialog.show();
            return;
        }
        boolean directUrlEmpty;
        boolean httpUrlEmpty;
        if (directUrl.equals("null") || directUrl.isEmpty()) {
            directUrlEmpty = true;
        } else {
            directUrlEmpty = false;
        }
        if (httpUrl.equals("null") || httpUrl.isEmpty()) {
            httpUrlEmpty = true;
        } else {
            httpUrlEmpty = false;
        }
        if (directUrlEmpty) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(httpUrl));
            startActivity(intent);
        } else if (httpUrlEmpty || !directUrlEmpty) {
            this.mDownloadRom.startDownload(mContext);
            setupUpdateNameInfo();
            if (Utils.isLollipop()) {
                setupMenuToolbar(mContext);
            } else {
                invalidateMenu();
            }
        } else {
            Toast.makeText(mContext, error, 1).show();
        }
    }

    public static void setupProgress(Context context) {
        Resources res = context.getResources();
        if (Preferences.getDownloadFinished(context)) {
            String ready = context.getResources().getString(C0994R.string.available_ready_to_install);
            int color = res.getColor(C0994R.color.holo_blue_light);
            if (Utils.isLollipop()) {
                if (Preferences.getCurrentTheme(context) == 0) {
                    color = context.getResources().getColor(C0994R.color.material_deep_teal_500);
                } else {
                    color = context.getResources().getColor(C0994R.color.material_deep_teal_200);
                }
            }
            if (mProgressCounterText != null) {
                mProgressCounterText.setTextColor(color);
                mProgressCounterText.setText(ready);
            }
            if (mProgressBar != null) {
                mProgressBar.setProgress(100);
                return;
            }
            return;
        }
        String fileSizeStr = Utils.formatDataFromBytes((long) RomUpdate.getFileSize(context));
        if (mProgressCounterText != null) {
            mProgressCounterText.setText(fileSizeStr);
        }
        if (mProgressBar != null) {
            mProgressBar.setProgress(0);
        }
    }

    public static void updateProgress(int progress, int downloaded, int total, Context context) {
        mProgressBar.setProgress(progress);
        mProgressCounterText.setText(new StringBuilder(String.valueOf(Utils.formatDataFromBytes((long) downloaded))).append("/").append(Utils.formatDataFromBytes((long) total)).toString());
    }

    public static void invalidateMenu() {
        ((Activity) mContext).invalidateOptionsMenu();
    }
}
