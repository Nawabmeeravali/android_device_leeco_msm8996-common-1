package com.ota.beta.updates.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.ota.beta.updates.C0994R;
import com.ota.beta.updates.utils.Constants;
import com.ota.beta.updates.utils.Utils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class LoadUpdateManifest extends AsyncTask<Void, Void, Void> implements Constants {
    private static final String MANIFEST = "update_manifest.xml";
    public final String TAG = getClass().getSimpleName();
    private Context mContext;
    private ProgressDialog mLoadingDialog;
    boolean shouldUpdateForegroundApp;

    public LoadUpdateManifest(Context context, boolean input) {
        this.mContext = context;
        this.shouldUpdateForegroundApp = input;
    }

    protected void onPreExecute() {
        if (this.shouldUpdateForegroundApp) {
            this.mLoadingDialog = new ProgressDialog(this.mContext);
            this.mLoadingDialog.setIndeterminate(true);
            this.mLoadingDialog.setCancelable(false);
            this.mLoadingDialog.setMessage(this.mContext.getResources().getString(C0994R.string.loading));
            this.mLoadingDialog.show();
        }
        File manifest = new File(this.mContext.getFilesDir().getPath(), MANIFEST);
        if (manifest.exists()) {
            manifest.delete();
        }
    }

    protected Void doInBackground(Void... v) {
        Exception e;
        try {
            URL url = new URL(Utils.getProp(Constants.OTA_MANIFEST).trim());
            url.openConnection().connect();
            InputStream input = new BufferedInputStream(url.openStream());
            try {
                OutputStream output = this.mContext.openFileOutput(MANIFEST, 0);
                byte[] data = new byte[1024];
                while (true) {
                    int count = input.read(data);
                    if (count == -1) {
                        break;
                    }
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                new RomXmlParser().parse(new File(this.mContext.getFilesDir(), MANIFEST), this.mContext);
            } catch (Exception e2) {
                e = e2;
                InputStream inputStream = input;
                Log.d(this.TAG, "Exception: " + e.getMessage());
                return null;
            }
        } catch (Exception e3) {
            e = e3;
            Log.d(this.TAG, "Exception: " + e.getMessage());
            return null;
        }
        return null;
    }

    protected void onPostExecute(Void result) {
        Intent intent;
        if (this.shouldUpdateForegroundApp) {
            this.mLoadingDialog.cancel();
            intent = new Intent(Constants.MANIFEST_LOADED);
        } else {
            intent = new Intent(Constants.MANIFEST_CHECK_BACKGROUND);
        }
        this.mContext.sendBroadcast(intent);
        super.onPostExecute(result);
    }
}
