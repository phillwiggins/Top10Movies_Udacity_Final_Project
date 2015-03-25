package com.purewowstudio.topmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.purewowstudio.topmovies.MainActivity;
import com.purewowstudio.topmovies.R;
import com.purewowstudio.topmovies.data.FilmDataContract;
import com.purewowstudio.topmovies.data.FilmProvider;
import com.purewowstudio.topmovies.models.Film;
import com.purewowstudio.topmovies.util.JSONParser;
import com.purewowstudio.topmovies.util.MyApplication;

/**
 * Created by Phillip on 23/03/2015.
 */
public class FilmSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final int SYNC_INTERVAL = 60 * 60 * 24 * 7;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final int FILM_NOTIFICATION_ID = 12;

    public int LIMIT_FILMS = 10;
    String KEY = "apikey";
    String LIMIT = "limit";
    private static final String URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?";
    private static final String API_KEY = "8vkm6v5gsesquraesc92nkx6";
    private ContentResolver myCR;

    public FilmSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Uri RottenUrl = Uri.parse(URL).buildUpon()
                .appendQueryParameter(KEY, API_KEY)
                .appendQueryParameter(LIMIT, Integer.toString(LIMIT_FILMS))
                .build();

        JSONParser jParser = new JSONParser();
        Film[] json = jParser.getJSONFromUrl(RottenUrl.toString());
        if (json[0] != null) {

            myCR = getContext().getContentResolver();
            ContentValues values = new ContentValues();

            getContext().getContentResolver().delete(FilmProvider.CONTENT_URI, FilmDataContract.FilmEntry._ID + " <= ?",
                    new String[]{Integer.toString(10)});

            for (int i = 0; i < json.length; i++) {
                values.put(FilmDataContract.FilmEntry.COLUMN_FILM_TITLE, json[i].getTitle());
                values.put(FilmDataContract.FilmEntry.COLUMN_FILM_RATING, json[i].getRating());
                values.put(FilmDataContract.FilmEntry.COLUMN_FILM_RUNTIME, json[i].getRuntime());
                values.put(FilmDataContract.FilmEntry.COLUMN_FILM_CRITICS, json[i].getCritics());
                values.put(FilmDataContract.FilmEntry.COLUMN_FILM_AUDIENCE, json[i].getAudience());
                values.put(FilmDataContract.FilmEntry.COLUMN_FILM_SYNOPSIS, json[i].getSynopsis());
                values.put(FilmDataContract.FilmEntry.COLUMN_FILM_PROFILE, json[i].getProfile());
                myCR.insert(FilmProvider.CONTENT_URI, values);
            }
            notifyNewFilm();
        } else {
            //Chill
        }

    }

    private void notifyNewFilm() {
        Context context = MyApplication.getAppContext();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

        if (displayNotifications) {

            Uri mUri = FilmProvider.CONTENT_URI;
            String notificationProjection[] = {FilmDataContract.FilmEntry.COLUMN_FILM_TITLE,
                    FilmDataContract.FilmEntry.COLUMN_FILM_RATING, FilmDataContract.FilmEntry.COLUMN_FILM_CRITICS};
            String selection = "_id = \"" + 0 + "\"";

            Cursor cursor = context.getContentResolver().query(mUri, notificationProjection, selection, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String FilmTitle = cursor.getString(1);
                    String FilmRating = cursor.getString(2);
                    String FilmCritics = cursor.getString(4);

                    Resources resources = context.getResources();

                    String title = context.getString(R.string.app_name);

                    String contentText = context.getString(R.string.notification_top) + FilmTitle +
                            " (" + FilmRating + ") - " + context.getString(R.string.notification_middle) + FilmCritics + "/100 !";

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setSmallIcon(R.drawable.clapperboard6)
                                    .setColor(resources.getColor(R.color.film_primary))
                                    .setContentTitle(title)
                                    .setContentText(contentText);

                    Intent resultIntent = new Intent(context, MainActivity.class);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(FILM_NOTIFICATION_ID, mBuilder.build());

                }
                cursor.close();

            } else {

            }
        }
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {

        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if (null == accountManager.getPassword(newAccount)) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        FilmSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
