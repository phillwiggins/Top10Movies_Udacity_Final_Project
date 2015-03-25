package com.purewowstudio.topmovies.sync;

/**
 * Created by Phillip on 23/03/2015.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FilmAuthenticatorService extends Service {
    private FilmAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new FilmAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}

