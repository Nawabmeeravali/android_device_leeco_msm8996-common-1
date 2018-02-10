package com.ota.beta.updates.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;
import com.ota.beta.updates.Addon;
import com.ota.beta.updates.C0994R;
import com.ota.beta.updates.RomUpdate;
import com.ota.beta.updates.download.DownloadAddon;
import com.ota.beta.updates.tasks.AddonXmlParser;
import com.ota.beta.updates.utils.Constants;
import com.ota.beta.updates.utils.Preferences;
import com.ota.beta.updates.utils.Utils;
import in.uncod.android.bypass.Bypass;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AddonActivity extends Activity implements Constants {
    public static final String TAG = "AddonActivity";
    public static Context mContext;
    private static DownloadAddon mDownloadAddon;
    private static ListView mListview;
    private static Builder mNetworkDialog;

    public static class AddonsArrayAdapter extends ArrayAdapter<Addon> {

        class C09971 implements OnClickListener {
            C09971() {
            }

            public void onClick(DialogInterface dialog, int which) {
                AddonActivity.mContext.startActivity(new Intent(AddonActivity.mContext, SettingsActivity.class));
            }
        }

        public AddonsArrayAdapter(Context context, ArrayList<Addon> users) {
            super(context, 0, users);
        }

        public static void updateProgress(int index, int progress, boolean finished) {
            View v = AddonActivity.mListview.getChildAt(index - AddonActivity.mListview.getFirstVisiblePosition());
            if (v != null) {
                ProgressBar progressBar = (ProgressBar) v.findViewById(C0994R.id.progress_bar);
                if (finished) {
                    progressBar.setProgress(0);
                } else {
                    progressBar.setProgress(progress);
                }
            }
        }

        public static void updateButtons(int index, boolean finished) {
            View v = AddonActivity.mListview.getChildAt((index - 1) - AddonActivity.mListview.getFirstVisiblePosition());
            if (v != null) {
                Button download = (Button) v.findViewById(C0994R.id.download_button);
                Button cancel = (Button) v.findViewById(C0994R.id.cancel_button);
                Button delete = (Button) v.findViewById(C0994R.id.delete_button);
                if (finished) {
                    download.setVisibility(0);
                    download.setText(AddonActivity.mContext.getResources().getString(C0994R.string.finished));
                    download.setClickable(false);
                    delete.setVisibility(0);
                    cancel.setVisibility(8);
                    return;
                }
                download.setVisibility(0);
                download.setText(AddonActivity.mContext.getResources().getString(C0994R.string.download));
                download.setClickable(true);
                cancel.setVisibility(8);
                delete.setVisibility(8);
            }
        }

        private void showNetworkDialog() {
            AddonActivity.mNetworkDialog = new Builder(AddonActivity.mContext);
            AddonActivity.mNetworkDialog.setTitle(C0994R.string.available_wrong_network_title).setMessage(C0994R.string.available_wrong_network_message).setPositiveButton(C0994R.string.ok, null).setNeutralButton(C0994R.string.settings, new C09971());
            AddonActivity.mNetworkDialog.show();
        }

        private void deleteConfirm(final File file, final Addon item) {
            Builder deleteConfirm = new Builder(AddonActivity.mContext);
            deleteConfirm.setTitle(C0994R.string.delete);
            deleteConfirm.setMessage(new StringBuilder(String.valueOf(AddonActivity.mContext.getResources().getString(C0994R.string.delete_confirm))).append("\n\n").append(file.getName()).toString());
            deleteConfirm.setPositiveButton(C0994R.string.ok, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (file.exists()) {
                        file.delete();
                        AddonsArrayAdapter.updateButtons(item.getId(), false);
                    }
                }
            });
            deleteConfirm.setNegativeButton(C0994R.string.cancel, null);
            deleteConfirm.show();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final Addon item = (Addon) getItem(position);
            final int index = position;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(C0994R.layout.card_addons_list_item, parent, false);
            }
            TextView desc = (TextView) convertView.findViewById(C0994R.id.description);
            TextView updatedOn = (TextView) convertView.findViewById(C0994R.id.updatedOn);
            TextView filesize = (TextView) convertView.findViewById(C0994R.id.size);
            final Button download = (Button) convertView.findViewById(C0994R.id.download_button);
            final Button cancel = (Button) convertView.findViewById(C0994R.id.cancel_button);
            Button delete = (Button) convertView.findViewById(C0994R.id.delete_button);
            ((TextView) convertView.findViewById(C0994R.id.title)).setText(item.getTitle());
            desc.setText(new Bypass(AddonActivity.mContext).markdownToSpannable(item.getDesc()));
            desc.setMovementMethod(LinkMovementMethod.getInstance());
            String UpdatedOnStr = convertView.getResources().getString(C0994R.string.addons_updated_on);
            String date = item.getPublishedAt();
            Locale locale = Locale.getDefault();
            DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", locale);
            try {
                date = new SimpleDateFormat("dd, MMMM yyyy", locale).format(simpleDateFormat.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            updatedOn.setText(new StringBuilder(String.valueOf(UpdatedOnStr)).append(" ").append(date).toString());
            filesize.setText(Utils.formatDataFromBytes((long) item.getFilesize()));
            final File file = new File(AddonActivity.SD_CARD + File.separator + AddonActivity.OTA_DOWNLOAD_DIR, item.getTitle() + ".zip");
            if (file.length() == ((long) item.getFilesize())) {
                download.setVisibility(0);
                download.setText(AddonActivity.mContext.getResources().getString(C0994R.string.finished));
                download.setClickable(false);
                delete.setVisibility(0);
                cancel.setVisibility(8);
            } else {
                download.setVisibility(0);
                download.setText(AddonActivity.mContext.getResources().getString(C0994R.string.download));
                download.setClickable(true);
                cancel.setVisibility(8);
                delete.setVisibility(8);
            }
            download.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    boolean isMobile = Utils.isMobileNetwork(AddonActivity.mContext);
                    boolean isSettingWiFiOnly = Preferences.getNetworkType(AddonActivity.mContext).equals("2");
                    if (isMobile && isSettingWiFiOnly) {
                        AddonsArrayAdapter.this.showNetworkDialog();
                        return;
                    }
                    AddonActivity.mDownloadAddon.startDownload(AddonActivity.mContext, item.getDownloadLink(), item.getTitle(), item.getId(), index);
                    download.setVisibility(8);
                    cancel.setVisibility(0);
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AddonActivity.mDownloadAddon.cancelDownload(AddonActivity.mContext, index);
                    download.setVisibility(0);
                    cancel.setVisibility(8);
                    AddonsArrayAdapter.updateProgress(index, 0, true);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AddonsArrayAdapter.this.deleteConfirm(file, item);
                }
            });
            return convertView;
        }
    }

    private class LoadAddonManifest extends AsyncTask<Object, Void, ArrayList<Addon>> {
        private static final String MANIFEST = "addon_manifest.xml";
        public final String TAG = getClass().getSimpleName();
        private Context mContext;
        private ProgressDialog mLoadingDialog;

        public LoadAddonManifest(Context context) {
            this.mContext = context;
        }

        protected void onPreExecute() {
            this.mLoadingDialog = new ProgressDialog(this.mContext);
            this.mLoadingDialog.setIndeterminate(true);
            this.mLoadingDialog.setCancelable(false);
            this.mLoadingDialog.setMessage(this.mContext.getResources().getString(C0994R.string.loading));
            this.mLoadingDialog.show();
            File manifest = new File(this.mContext.getFilesDir().getPath(), MANIFEST);
            if (manifest.exists()) {
                manifest.delete();
            }
        }

        protected ArrayList<Addon> doInBackground(Object... param) {
            Exception e;
            try {
                URL url = new URL((String) param[0]);
                url.openConnection().connect();
                InputStream input = new BufferedInputStream(url.openStream());
                try {
                    OutputStream output = this.mContext.openFileOutput(MANIFEST, 0);
                    byte[] data = new byte[1024];
                    while (true) {
                        int count = input.read(data);
                        if (count == -1) {
                            output.flush();
                            output.close();
                            input.close();
                            return AddonXmlParser.parse(new File(this.mContext.getFilesDir(), MANIFEST));
                        }
                        output.write(data, 0, count);
                    }
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
        }

        protected void onPostExecute(ArrayList<Addon> result) {
            this.mLoadingDialog.cancel();
            if (result != null) {
                AddonActivity.this.setupListView(result);
            }
            super.onPostExecute(result);
        }
    }

    @SuppressLint({"NewApi"})
    public void onCreate(Bundle savedInstanceState) {
        mContext = this;
        setTheme(Preferences.getTheme(mContext));
        boolean isLollipop = Utils.isLollipop();
        super.onCreate(savedInstanceState);
        setContentView(C0994R.layout.ota_addons);
        if (isLollipop) {
            Toolbar toolbar = (Toolbar) findViewById(C0994R.id.toolbar_addons);
            setActionBar(toolbar);
            toolbar.setTitle(getResources().getString(C0994R.string.app_name));
        }
        mListview = (ListView) findViewById(C0994R.id.listview);
        mDownloadAddon = new DownloadAddon();
        String isRomhut = "";
        String urlDomain = RomUpdate.getUrlDomain(mContext);
        if (!urlDomain.equals("null")) {
            isRomhut = urlDomain.contains("romhut.com") ? "?order_by=name&order_direction=asc" : "";
        }
        new LoadAddonManifest(mContext).execute(new Object[]{new StringBuilder(String.valueOf(RomUpdate.getAddonsUrl(mContext))).append(isRomhut).toString()});
    }

    public void setupListView(ArrayList<Addon> addonsList) {
        AddonsArrayAdapter adapter = new AddonsArrayAdapter(mContext, addonsList);
        if (mListview != null) {
            mListview.setAdapter(adapter);
        }
    }
}
