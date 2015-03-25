package com.purewowstudio.topmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Phillip on 23/03/2015.
 */
public class FilmSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static FilmSyncAdapter sFilmSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sFilmSyncAdapter == null) {
                sFilmSyncAdapter = new FilmSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sFilmSyncAdapter.getSyncAdapterBinder();
    }
}
