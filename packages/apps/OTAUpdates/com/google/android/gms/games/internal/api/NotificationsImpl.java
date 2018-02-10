package com.google.android.gms.games.internal.api;

import android.os.Bundle;
import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zza.zzb;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Games.BaseGamesApiMethodImpl;
import com.google.android.gms.games.Notifications;
import com.google.android.gms.games.Notifications.ContactSettingLoadResult;
import com.google.android.gms.games.Notifications.GameMuteStatusChangeResult;
import com.google.android.gms.games.Notifications.GameMuteStatusLoadResult;
import com.google.android.gms.games.Notifications.InboxCountResult;
import com.google.android.gms.games.internal.GamesClientImpl;

public final class NotificationsImpl implements Notifications {

    class C05111 extends BaseGamesApiMethodImpl<GameMuteStatusChangeResult> {
        final /* synthetic */ String zzarm;

        public /* synthetic */ Result createFailedResult(Status x0) {
            return zzaf(x0);
        }

        protected void zza(GamesClientImpl gamesClientImpl) throws RemoteException {
            gamesClientImpl.zzd((zzb) this, this.zzarm, true);
        }

        public GameMuteStatusChangeResult zzaf(final Status status) {
            return new GameMuteStatusChangeResult(this) {
                final /* synthetic */ C05111 zzarn;

                public Status getStatus() {
                    return status;
                }
            };
        }
    }

    class C05132 extends BaseGamesApiMethodImpl<GameMuteStatusChangeResult> {
        final /* synthetic */ String zzarm;

        public /* synthetic */ Result createFailedResult(Status x0) {
            return zzaf(x0);
        }

        protected void zza(GamesClientImpl gamesClientImpl) throws RemoteException {
            gamesClientImpl.zzd((zzb) this, this.zzarm, false);
        }

        public GameMuteStatusChangeResult zzaf(final Status status) {
            return new GameMuteStatusChangeResult(this) {
                final /* synthetic */ C05132 zzaro;

                public Status getStatus() {
                    return status;
                }
            };
        }
    }

    class C05153 extends BaseGamesApiMethodImpl<GameMuteStatusLoadResult> {
        final /* synthetic */ String zzarm;

        public /* synthetic */ Result createFailedResult(Status x0) {
            return zzag(x0);
        }

        protected void zza(GamesClientImpl gamesClientImpl) throws RemoteException {
            gamesClientImpl.zzo((zzb) this, this.zzarm);
        }

        public GameMuteStatusLoadResult zzag(final Status status) {
            return new GameMuteStatusLoadResult(this) {
                final /* synthetic */ C05153 zzarp;

                public Status getStatus() {
                    return status;
                }
            };
        }
    }

    private static abstract class ContactSettingLoadImpl extends BaseGamesApiMethodImpl<ContactSettingLoadResult> {
        public /* synthetic */ Result createFailedResult(Status x0) {
            return zzah(x0);
        }

        public ContactSettingLoadResult zzah(final Status status) {
            return new ContactSettingLoadResult(this) {
                final /* synthetic */ ContactSettingLoadImpl zzars;

                public Status getStatus() {
                    return status;
                }
            };
        }
    }

    class C05164 extends ContactSettingLoadImpl {
        final /* synthetic */ boolean zzaqy;

        protected void zza(GamesClientImpl gamesClientImpl) throws RemoteException {
            gamesClientImpl.zzh((zzb) this, this.zzaqy);
        }
    }

    private static abstract class ContactSettingUpdateImpl extends BaseGamesApiMethodImpl<Status> {
        public /* synthetic */ Result createFailedResult(Status x0) {
            return zzb(x0);
        }

        public Status zzb(Status status) {
            return status;
        }
    }

    class C05175 extends ContactSettingUpdateImpl {
        final /* synthetic */ boolean zzarq;
        final /* synthetic */ Bundle zzarr;

        protected void zza(GamesClientImpl gamesClientImpl) throws RemoteException {
            gamesClientImpl.zza((zzb) this, this.zzarq, this.zzarr);
        }
    }

    private static abstract class InboxCountImpl extends BaseGamesApiMethodImpl<InboxCountResult> {
        public /* synthetic */ Result createFailedResult(Status x0) {
            return zzai(x0);
        }

        public InboxCountResult zzai(final Status status) {
            return new InboxCountResult(this) {
                final /* synthetic */ InboxCountImpl zzart;

                public Status getStatus() {
                    return status;
                }
            };
        }
    }

    class C05186 extends InboxCountImpl {
        protected void zza(GamesClientImpl gamesClientImpl) throws RemoteException {
            gamesClientImpl.zzh(this);
        }
    }

    public void clear(GoogleApiClient apiClient, int notificationTypes) {
        GamesClientImpl zzb = Games.zzb(apiClient, false);
        if (zzb != null) {
            zzb.zzfD(notificationTypes);
        }
    }

    public void clearAll(GoogleApiClient apiClient) {
        clear(apiClient, 31);
    }
}
