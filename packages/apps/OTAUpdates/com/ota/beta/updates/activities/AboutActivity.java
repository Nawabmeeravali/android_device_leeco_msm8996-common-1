package com.ota.beta.updates.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.ota.beta.updates.C0994R;
import com.ota.beta.updates.tasks.Changelog;
import com.ota.beta.updates.utils.Preferences;
import com.ota.beta.updates.utils.Utils;

public class AboutActivity extends Activity {
    private AdView mAdView;
    private Context mContext;

    class C09951 implements OnClickListener {
        C09951() {
        }

        public void onClick(DialogInterface dialog, int which) {
            String url = "";
            if (((AlertDialog) dialog).getListView().getCheckedItemPosition() == 0) {
                url = "http://goo.gl/ZKSY4";
            } else {
                url = "http://goo.gl/o4c6ES";
            }
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            AboutActivity.this.startActivity(intent);
        }
    }

    class C09962 implements OnClickListener {
        C09962() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    @SuppressLint({"NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        this.mContext = this;
        setTheme(Preferences.getTheme(this.mContext));
        super.onCreate(savedInstanceState);
        setContentView(C0994R.layout.ota_about);
        if (Utils.isLollipop()) {
            Toolbar toolbar = (Toolbar) findViewById(C0994R.id.toolbar_about);
            setActionBar(toolbar);
            toolbar.setTitle(getResources().getString(C0994R.string.app_name));
        }
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        TextView creditsTitle = (TextView) findViewById(C0994R.id.about_tv_credits_title);
        TextView changelogTitle = (TextView) findViewById(C0994R.id.about_tv_changelog_title);
        TextView creditsSummary = (TextView) findViewById(C0994R.id.about_tv_credits_summary);
        ((TextView) findViewById(C0994R.id.about_tv_donate_title)).setTypeface(typeFace);
        changelogTitle.setTypeface(typeFace);
        creditsTitle.setTypeface(typeFace);
        String openHTML = "";
        if (!Utils.isLollipop()) {
            openHTML = "<font color='#33b5e5'>";
        } else if (Preferences.getCurrentTheme(this) == 0) {
            openHTML = "<font color='#009688'>";
        } else {
            openHTML = "<font color='#80cbc4'>";
        }
        String closeHTML = "</font>";
        String newLine = "<br />";
        creditsSummary.setText(Html.fromHtml(new StringBuilder(String.valueOf(openHTML)).append("Matt Booth").append(closeHTML).append(" - Anything not mentioned below").append(newLine).append(openHTML).append("Roman Nurik").append(closeHTML).append(" - Android Asset Studio Framework").append(newLine).append(openHTML).append("Jeff Gilfelt").append(closeHTML).append(" - Android Action Bar Style Generator").append(newLine).append(openHTML).append("Ficeto (AllianceROM)").append(closeHTML).append(" - Shell tools").append(newLine).append(openHTML).append("StackOverflow").append(closeHTML).append(" - Many, many people").toString()));
        ((TextView) findViewById(C0994R.id.about_tv_version_title)).setTypeface(typeFace);
        TextView versionSummary = (TextView) findViewById(C0994R.id.about_tv_version_summary);
        String appVer = getResources().getString(C0994R.string.about_app_version);
        versionSummary.setText(new StringBuilder(String.valueOf(appVer)).append(" v").append(getResources().getString(C0994R.string.app_version)).toString());
        if (Preferences.getAdsEnabled(this).booleanValue()) {
            this.mAdView = (AdView) findViewById(C0994R.id.adView);
            this.mAdView.loadAd(new Builder().build());
        }
    }

    private void setupDonateDialog() {
        new AlertDialog.Builder(this).setTitle("").setSingleChoiceItems(new String[]{"PayPal", "BitCoin"}, 0, null).setPositiveButton(getResources().getString(C0994R.string.ok), new C09951()).setNegativeButton(getResources().getString(C0994R.string.cancel), new C09962()).show();
    }

    public void openAppDonate(View v) {
        setupDonateDialog();
    }

    public void openChangelog(View v) {
        new Changelog(this, this.mContext, getResources().getString(C0994R.string.changelog), getResources().getString(C0994R.string.changelog_url), true).execute(new Void[0]);
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
}
