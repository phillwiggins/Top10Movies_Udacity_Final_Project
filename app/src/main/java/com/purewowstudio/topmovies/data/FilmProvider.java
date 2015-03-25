package com.purewowstudio.topmovies.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.purewowstudio.topmovies.models.Film;

/**
 * Created by Phillip on 19/03/2015.
 */
public class FilmProvider extends ContentProvider {

    public static final String TABLE_NAME = "film_data";
    public static final String AUTHORITY = "com.purewowstudio.topmovies.data.FilmProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    private ContentResolver myCR;
    public static final int FILMS = 1;
    public static final int FILMS_ID = 2;

    public static final UriMatcher sURIMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, TABLE_NAME, FILMS);
        sURIMatcher.addURI(AUTHORITY, TABLE_NAME + "/#",
                FILMS_ID);
    }

    private DatabaseHelper mDB;

    public boolean onCreate() {
        mDB = new DatabaseHelper(getContext(), null, null, 1);
        myCR = getContext().getContentResolver();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FilmDataContract.TABLE_NAME);
        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
            case FILMS_ID:
                queryBuilder.appendWhere(FilmDataContract.FilmEntry._ID + "="
                        + uri.getLastPathSegment());
                break;
            case FILMS:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(),
                projection, selection, selectionArgs, null, null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),
                uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int uriType = sURIMatcher.match(uri);

        SQLiteDatabase sqlDB = mDB.getWritableDatabase();

        long id = 0;
        switch (uriType) {
            case FILMS:
                id = sqlDB.insert(FilmDataContract.TABLE_NAME,
                        null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "
                        + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(FilmDataContract.TABLE_NAME + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case FILMS:
                rowsDeleted = sqlDB.delete(FilmDataContract.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case FILMS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(FilmDataContract.TABLE_NAME,
                            FilmDataContract.FilmEntry._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(FilmDataContract.TABLE_NAME,
                            FilmDataContract.FilmEntry._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType) {
            case FILMS:
                rowsUpdated =
                        sqlDB.update(FilmDataContract.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs);
                break;
            case FILMS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated =
                            sqlDB.update(FilmDataContract.TABLE_NAME,
                                    values,
                                    FilmDataContract.FilmEntry._ID + "=" + id,
                                    null);
                } else {
                    rowsUpdated =
                            sqlDB.update(FilmDataContract.TABLE_NAME,
                                    values,
                                    FilmDataContract.FilmEntry._ID + "=" + id
                                            + " and "
                                            + selection,
                                    selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " +
                        uri);
        }
        getContext().getContentResolver().notifyChange(uri,
                null);
        return rowsUpdated;
    }


    public boolean contentDelete(String filmName) {

        boolean result = false;
        String selection = "title = \"" + filmName + "\"";
        int rowsDeleted = myCR.delete(FilmProvider.CONTENT_URI,
                selection, null);
        if (rowsDeleted > 0)
            result = true;
        return result;
    }

    public Film contentFindFilm(String filmName) {
        String[] projection = FilmDataContract.FilmEntry.COLUMNS;

        String selection = "title = \"" + filmName + "\"";

        Cursor cursor = myCR.query(FilmProvider.CONTENT_URI,
                projection, selection, null,
                null);

        Film film = new Film();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            film.setId(Integer.parseInt(cursor.getString(0)));
            film.setTitle(cursor.getString(1));
            film.setRating(cursor.getString(2));
            film.setRuntime(cursor.getString(3));
            film.setCritics(cursor.getString(4));
            film.setAudience(cursor.getString(5));
            film.setSynopsis(cursor.getString(6));
            film.setProfile(cursor.getString(7));

            cursor.close();
        } else {
            film = null;
        }
        return film;
    }

    public void contentAddFilm(Film film) {

        ContentValues values = new ContentValues();
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_TITLE, film.getTitle());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_RATING, film.getRating());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_RUNTIME, film.getRuntime());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_CRITICS, film.getCritics());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_AUDIENCE, film.getAudience());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_SYNOPSIS, film.getSynopsis());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_PROFILE, film.getProfile());

        myCR.insert(FilmProvider.CONTENT_URI, values);
    }

}
