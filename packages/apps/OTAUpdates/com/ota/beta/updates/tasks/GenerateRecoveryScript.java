package com.ota.beta.updates.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.ota.beta.updates.C0994R;
import com.ota.beta.updates.RomUpdate;
import com.ota.beta.updates.utils.Constants;
import com.ota.beta.updates.utils.Preferences;
import com.ota.beta.updates.utils.Tools;
import java.io.File;

public class GenerateRecoveryScript extends AsyncTask<Void, String, Boolean> implements Constants {
    private static String NEW_LINE = "\n";
    private static String SCRIPT_FILE = "/cache/recovery/openrecoveryscript";
    public final String TAG = getClass().getSimpleName();
    private Context mContext;
    private String mFilename;
    private ProgressDialog mLoadingDialog;
    private StringBuilder mScript = new StringBuilder();
    private String mScriptOutput;

    public GenerateRecoveryScript(Context context) {
        this.mContext = context;
        this.mFilename = new StringBuilder(String.valueOf(RomUpdate.getFilename(this.mContext))).append(".zip").toString();
    }

    protected void onPreExecute() {
        this.mLoadingDialog = new ProgressDialog(this.mContext);
        this.mLoadingDialog.setCancelable(false);
        this.mLoadingDialog.setIndeterminate(true);
        this.mLoadingDialog.setMessage(this.mContext.getString(C0994R.string.rebooting));
        this.mLoadingDialog.show();
        if (Preferences.getWipeData(this.mContext)) {
            this.mScript.append("wipe data" + NEW_LINE);
        }
        if (Preferences.getWipeCache(this.mContext)) {
            this.mScript.append("wipe cache" + NEW_LINE);
        }
        if (Preferences.getWipeDalvik(this.mContext)) {
            this.mScript.append("wipe dalvik" + NEW_LINE);
        }
        this.mScript.append("install /sdcard" + File.separator + OTA_DOWNLOAD_DIR + File.separator + this.mFilename + NEW_LINE);
        File[] filesArr = new File("/sdcard" + File.separator + OTA_DOWNLOAD_DIR + File.separator + Constants.INSTALL_AFTER_FLASH_DIR).listFiles();
        if (filesArr != null && filesArr.length > 0) {
            for (File name : filesArr) {
                this.mScript.append(NEW_LINE + "install " + "/sdcard" + File.separator + OTA_DOWNLOAD_DIR + File.separator + Constants.INSTALL_AFTER_FLASH_DIR + File.separator + name.getName());
            }
        }
        if (Preferences.getDeleteAfterInstall(this.mContext)) {
            this.mScript.append(NEW_LINE + "cmd rm -rf " + "/sdcard" + File.separator + OTA_DOWNLOAD_DIR + File.separator + Constants.INSTALL_AFTER_FLASH_DIR + File.separator + this.mFilename + NEW_LINE);
        }
        this.mScriptOutput = this.mScript.toString();
    }

    protected Boolean doInBackground(Void... params) {
        if (Tools.shell("mkdir -p /cache/recovery/; echo $?", false).equals(Constants.THEME_LIGHT)) {
            Tools.shell("echo \"" + this.mScriptOutput + "\" > " + SCRIPT_FILE + "\n", false);
        } else {
            Tools.shell("mkdir -p /cache/recovery/; echo $?", true);
            Tools.shell("echo \"" + this.mScriptOutput + "\" > " + SCRIPT_FILE + "\n", true);
        }
        return Boolean.valueOf(true);
    }

    protected void onPostExecute(Boolean value) {
        this.mLoadingDialog.cancel();
        Tools.recovery(this.mContext);
    }
}
