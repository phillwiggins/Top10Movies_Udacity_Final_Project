package com.purewowstudio.topmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Phillip on 18/03/2015.
 */
public final class FilmDataContract {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "film_database.db";
    public static final String TABLE_NAME = "film_data";

    private FilmDataContract() {
    }

    public static abstract class FilmEntry implements BaseColumns {
        public static final String TABLE_NAME = "film_data";
        public static final String _ID = "_id";
        public static final String COLUMN_FILM_TITLE = "title";
        public static final String COLUMN_FILM_RATING = "mpaa_rating";
        public static final String COLUMN_FILM_RUNTIME = "runtime";
        public static final String COLUMN_FILM_CRITICS = "critics_score";
        public static final String COLUMN_FILM_AUDIENCE = "audience_score";
        public static final String COLUMN_FILM_SYNOPSIS = "synopsis";
        public static final String COLUMN_FILM_PROFILE = "profile";

        public static final String[] COLUMNS = {_ID, COLUMN_FILM_TITLE, COLUMN_FILM_RATING, COLUMN_FILM_RUNTIME, COLUMN_FILM_CRITICS,
                COLUMN_FILM_AUDIENCE, COLUMN_FILM_SYNOPSIS, COLUMN_FILM_PROFILE};

        public static final String SQL_CREATE_TABLE = "CREATE TABLE "
                + FilmDataContract.TABLE_NAME + " (_id INTEGER PRIMARY KEY,"
                + FilmDataContract.FilmEntry.COLUMN_FILM_TITLE + " TEXT,"
                + FilmDataContract.FilmEntry.COLUMN_FILM_RATING + " TEXT,"
                + FilmDataContract.FilmEntry.COLUMN_FILM_RUNTIME + " TEXT,"
                + FilmDataContract.FilmEntry.COLUMN_FILM_CRITICS + " TEXT,"
                + FilmDataContract.FilmEntry.COLUMN_FILM_AUDIENCE + " TEXT,"
                + FilmDataContract.FilmEntry.COLUMN_FILM_SYNOPSIS + " TEXT,"
                + FilmDataContract.FilmEntry.COLUMN_FILM_PROFILE + " TEXT" + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + FilmDataContract.TABLE_NAME;
    }

    public static Uri buildFilm(int position) {
        return FilmProvider.CONTENT_URI.buildUpon().appendQueryParameter(FilmEntry._ID, Integer.toString(position)).build();
    }
}
