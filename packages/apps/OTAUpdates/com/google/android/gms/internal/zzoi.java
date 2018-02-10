package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.NoOptions;
import com.google.android.gms.common.api.Api.Client;
import com.google.android.gms.common.api.Api.ClientKey;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zze;
import com.google.android.gms.nearby.bootstrap.zza;

public class zzoi implements zza {
    public static final ClientKey<zzoh> zzNX = new ClientKey();
    public static final Api.zza<zzoh, NoOptions> zzNY = new C07901();

    static class C07901 implements Api.zza<zzoh, NoOptions> {
        C07901() {
        }

        public int getPriority() {
            return Integer.MAX_VALUE;
        }

        public /* synthetic */ Client zza(Context context, Looper looper, zze com_google_android_gms_common_internal_zze, Object obj, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return zzp(context, looper, com_google_android_gms_common_internal_zze, (NoOptions) obj, connectionCallbacks, onConnectionFailedListener);
        }

        public zzoh zzp(Context context, Looper looper, zze com_google_android_gms_common_internal_zze, NoOptions noOptions, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return new zzoh(context, looper, connectionCallbacks, onConnectionFailedListener, com_google_android_gms_common_internal_zze);
        }
    }
}
