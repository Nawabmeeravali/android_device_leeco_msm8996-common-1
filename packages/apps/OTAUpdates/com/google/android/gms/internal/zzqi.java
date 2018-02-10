package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzu;
import com.google.android.gms.tagmanager.zzbg;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class zzqi {
    private String zzaLc = "https://www.google-analytics.com";

    private String zzeQ(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            zzbg.zzaz("Cannot encode the string: " + str);
            return "";
        }
    }

    public void zzeU(String str) {
        this.zzaLc = str;
        zzbg.zzaA("The Ctfe server endpoint was changed to: " + str);
    }

    public String zzt(List<zzpy> list) {
        return this.zzaLc + "/gtm/android?" + zzu(list);
    }

    String zzu(List<zzpy> list) {
        boolean z = true;
        if (list.size() > 1) {
            z = false;
        }
        zzu.zzV(z);
        if (list.isEmpty()) {
            return "";
        }
        zzpy com_google_android_gms_internal_zzpy = (zzpy) list.get(0);
        String trim = !com_google_android_gms_internal_zzpy.zzAd().trim().equals("") ? com_google_android_gms_internal_zzpy.zzAd().trim() : "-1";
        StringBuilder stringBuilder = new StringBuilder();
        if (com_google_android_gms_internal_zzpy.zzAa() != null) {
            stringBuilder.append(com_google_android_gms_internal_zzpy.zzAa());
        } else {
            stringBuilder.append("id");
        }
        stringBuilder.append("=").append(zzeQ(com_google_android_gms_internal_zzpy.getContainerId())).append("&").append("pv").append("=").append(zzeQ(trim));
        if (com_google_android_gms_internal_zzpy.zzAc()) {
            stringBuilder.append("&gtm_debug=x");
        }
        return stringBuilder.toString();
    }
}
