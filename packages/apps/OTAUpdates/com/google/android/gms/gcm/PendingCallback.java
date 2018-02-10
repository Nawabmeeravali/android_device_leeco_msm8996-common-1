package com.google.android.gms.gcm;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class PendingCallback implements Parcelable {
    public static final Creator<PendingCallback> CREATOR = new C05901();
    final IBinder zzZQ;

    static class C05901 implements Creator<PendingCallback> {
        C05901() {
        }

        public /* synthetic */ Object createFromParcel(Parcel x0) {
            return zzdW(x0);
        }

        public /* synthetic */ Object[] newArray(int x0) {
            return zzgi(x0);
        }

        public PendingCallback zzdW(Parcel parcel) {
            return new PendingCallback(parcel);
        }

        public PendingCallback[] zzgi(int i) {
            return new PendingCallback[i];
        }
    }

    public PendingCallback(Parcel in) {
        this.zzZQ = in.readStrongBinder();
    }

    public int describeContents() {
        return 0;
    }

    public IBinder getIBinder() {
        return this.zzZQ;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongBinder(this.zzZQ);
    }
}
