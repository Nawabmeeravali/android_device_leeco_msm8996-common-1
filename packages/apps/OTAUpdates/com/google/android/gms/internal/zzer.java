package com.google.android.gms.internal;

import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.URLUtil;
import com.google.android.gms.C0116R;
import com.google.android.gms.ads.internal.zzo;
import java.util.Map;

@zzgd
public class zzer extends zzeu {
    private final Context mContext;
    private final Map<String, String> zzyn;

    class C06782 implements OnClickListener {
        final /* synthetic */ zzer zzyN;

        C06782(zzer com_google_android_gms_internal_zzer) {
            this.zzyN = com_google_android_gms_internal_zzer;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.zzyN.zzae("User canceled the download.");
        }
    }

    public zzer(zzid com_google_android_gms_internal_zzid, Map<String, String> map) {
        super(com_google_android_gms_internal_zzid, "storePicture");
        this.zzyn = map;
        this.mContext = com_google_android_gms_internal_zzid.zzgB();
    }

    public void execute() {
        if (this.mContext == null) {
            zzae("Activity context is not available");
        } else if (zzo.zzbv().zzK(this.mContext).zzcS()) {
            final String str = (String) this.zzyn.get("iurl");
            if (TextUtils.isEmpty(str)) {
                zzae("Image url cannot be empty.");
            } else if (URLUtil.isValidUrl(str)) {
                final String zzad = zzad(str);
                if (zzo.zzbv().zzav(zzad)) {
                    Builder zzJ = zzo.zzbv().zzJ(this.mContext);
                    zzJ.setTitle(zzo.zzby().zzc(C0116R.string.store_picture_title, "Save image"));
                    zzJ.setMessage(zzo.zzby().zzc(C0116R.string.store_picture_message, "Allow Ad to store image in Picture gallery?"));
                    zzJ.setPositiveButton(zzo.zzby().zzc(C0116R.string.accept, "Accept"), new OnClickListener(this) {
                        final /* synthetic */ zzer zzyN;

                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                ((DownloadManager) this.zzyN.mContext.getSystemService("download")).enqueue(this.zzyN.zzf(str, zzad));
                            } catch (IllegalStateException e) {
                                this.zzyN.zzae("Could not store picture.");
                            }
                        }
                    });
                    zzJ.setNegativeButton(zzo.zzby().zzc(C0116R.string.decline, "Decline"), new C06782(this));
                    zzJ.create().show();
                    return;
                }
                zzae("Image type not recognized: " + zzad);
            } else {
                zzae("Invalid image url: " + str);
            }
        } else {
            zzae("Feature is not supported by the device.");
        }
    }

    String zzad(String str) {
        return Uri.parse(str).getLastPathSegment();
    }

    Request zzf(String str, String str2) {
        Request request = new Request(Uri.parse(str));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, str2);
        zzo.zzbx().zza(request);
        return request;
    }
}
