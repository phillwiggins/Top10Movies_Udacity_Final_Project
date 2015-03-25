package com.purewowstudio.topmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.purewowstudio.topmovies.models.Film;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Phillip on 18/03/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(context, FilmDataContract.DATABASE_NAME, factory, FilmDataContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FilmDataContract.FilmEntry.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(FilmDataContract.FilmEntry.DELETE_TABLE);
        onCreate(db);
    }

    public void addFilm(Film film) {

        ContentValues values = new ContentValues();
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_TITLE, film.getTitle());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_RATING, film.getRating());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_RUNTIME, film.getRuntime());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_CRITICS, film.getCritics());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_AUDIENCE, film.getAudience());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_SYNOPSIS, film.getSynopsis());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_PROFILE, film.getProfile());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(FilmDataContract.TABLE_NAME,
                null,
                values);
        db.close();
    }

    public Film getFilm(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(FilmDataContract.TABLE_NAME,
                        FilmDataContract.FilmEntry.COLUMNS,
                        "_id = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        null,
                        null);

        if (cursor != null)
            cursor.moveToFirst();

        Film film = new Film();
        film.setTitle(cursor.getString(1));
        film.setRating(cursor.getString(2));
        film.setRuntime(cursor.getString(3));
        film.setCritics(cursor.getString(4));
        film.setAudience(cursor.getString(5));
        film.setSynopsis(cursor.getString(6));
        film.setProfile(cursor.getString(7));

        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> films = new LinkedList<Film>();

        String query = "SELECT  * FROM " + FilmDataContract.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Film film = null;
        if (cursor.moveToFirst()) {
            do {
                film = new Film();
                film.setId(Integer.parseInt(cursor.getString(0)));
                film.setTitle(cursor.getString(1));
                film.setRating(cursor.getString(2));
                film.setRuntime(cursor.getString(3));
                film.setCritics(cursor.getString(4));
                film.setAudience(cursor.getString(5));
                film.setSynopsis(cursor.getString(6));
                film.setProfile(cursor.getString(7));

                films.add(film);
            } while (cursor.moveToNext());
        }

        return films;
    }

    public int updateFilm(Film film) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_TITLE, film.getTitle());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_RATING, film.getRating());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_RUNTIME, film.getRuntime());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_CRITICS, film.getCritics());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_AUDIENCE, film.getAudience());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_SYNOPSIS, film.getSynopsis());
        values.put(FilmDataContract.FilmEntry.COLUMN_FILM_PROFILE, film.getProfile());

        int i = db.update(FilmDataContract.FilmEntry.TABLE_NAME,
                values,
                "_id+ = ?",
                new String[]{String.valueOf(film.getId())});

        db.close();
        return i;
    }

    public int getFilmsCount() {
        String countQuery = "SELECT  * FROM " + FilmDataContract.FilmEntry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FilmDataContract.FilmEntry.TABLE_NAME, null, null);
    }
}