package com.ota.beta.updates.tasks;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.ota.beta.updates.C0994R;
import in.uncod.android.bypass.Bypass;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class Changelog extends AsyncTask<Void, Void, String> {
    private static final String CHANGELOG = "Changelog.md";
    private static final String TAG = "AboutActivity.Changelog";
    private Activity mActivity;
    private String mChangelog;
    private File mChangelogFile;
    private Context mContext;
    private ProgressDialog mLoadingDialog;
    private boolean mRemote;
    private String mTitle;

    public Changelog(Activity activity, Context context, String dialogTitle, String changelog, boolean remote) {
        this.mContext = context;
        this.mActivity = activity;
        this.mChangelog = changelog;
        this.mTitle = dialogTitle;
        this.mRemote = remote;
    }

    protected void onPreExecute() {
        this.mLoadingDialog = new ProgressDialog(this.mContext);
        this.mLoadingDialog.setIndeterminate(true);
        this.mLoadingDialog.setCancelable(false);
        this.mLoadingDialog.setMessage(this.mContext.getResources().getString(C0994R.string.loading));
        this.mLoadingDialog.show();
        this.mChangelogFile = new File(this.mContext.getFilesDir().getPath(), CHANGELOG);
        if (this.mChangelogFile.exists()) {
            this.mChangelogFile.delete();
        }
    }

    protected String doInBackground(Void... params) {
        Exception e;
        InputStreamReader inputReader;
        char[] tmp;
        InputStreamReader inputReader2;
        int numRead;
        String text;
        if (!this.mRemote) {
            return null;
        }
        StringBuilder data;
        try {
            URL url = new URL(this.mChangelog);
            url.openConnection().connect();
            InputStream input = new BufferedInputStream(url.openStream());
            try {
                OutputStream output = this.mContext.openFileOutput(CHANGELOG, 0);
                data = new byte[1024];
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
            } catch (Exception e2) {
                e = e2;
                InputStream inputStream = input;
            }
        } catch (Exception e3) {
            e = e3;
            Log.d(TAG, "Exception: " + e.getMessage());
            inputReader = null;
            data = new StringBuilder();
            tmp = new char[2048];
            inputReader2 = new FileReader(this.mChangelogFile);
            while (true) {
                try {
                    numRead = inputReader2.read(tmp);
                    if (numRead < 0) {
                        break;
                    }
                    data.append(tmp, 0, numRead);
                } catch (IOException e4) {
                    inputReader = inputReader2;
                } catch (Throwable th) {
                    th = th;
                    inputReader = inputReader2;
                }
            }
            text = data.toString();
            if (inputReader2 == null) {
                inputReader = inputReader2;
                return text;
            }
            try {
                inputReader2.close();
                inputReader = inputReader2;
                return text;
            } catch (IOException e5) {
                inputReader = inputReader2;
                return text;
            }
        }
        inputReader = null;
        try {
            data = new StringBuilder();
            tmp = new char[2048];
            inputReader2 = new FileReader(this.mChangelogFile);
            while (true) {
                numRead = inputReader2.read(tmp);
                if (numRead < 0) {
                    break;
                }
                data.append(tmp, 0, numRead);
            }
            text = data.toString();
            if (inputReader2 == null) {
                inputReader2.close();
                inputReader = inputReader2;
                return text;
            }
            inputReader = inputReader2;
            return text;
        } catch (IOException e6) {
            try {
                text = this.mContext.getString(C0994R.string.changelog_error);
                if (inputReader == null) {
                    return text;
                }
                try {
                    inputReader.close();
                    return text;
                } catch (IOException e7) {
                    return text;
                }
            } catch (Throwable th2) {
                Throwable th3;
                th3 = th2;
                if (inputReader != null) {
                    try {
                        inputReader.close();
                    } catch (IOException e8) {
                    }
                }
                throw th3;
            }
        }
    }

    protected void onPostExecute(String result) {
        this.mLoadingDialog.cancel();
        showChangelogDialog(this.mRemote ? result : this.mChangelog);
        super.onPostExecute(result);
    }

    private void showChangelogDialog(String changelogText) {
        View view = this.mActivity.getLayoutInflater().inflate(C0994R.layout.ota_changelog_layout, null);
        ((TextView) view.findViewById(C0994R.id.title)).setText(new Bypass(this.mContext).markdownToSpannable(changelogText));
        Builder dialog = new Builder(this.mContext);
        dialog.setTitle(this.mTitle);
        dialog.setView(view);
        dialog.setPositiveButton(C0994R.string.done, null);
        dialog.show();
    }
}
