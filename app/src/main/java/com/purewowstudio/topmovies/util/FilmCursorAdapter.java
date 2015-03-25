package com.purewowstudio.topmovies.util;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.purewowstudio.topmovies.R;
import com.purewowstudio.topmovies.data.FilmDataContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Phillip on 22/03/2015.
 */
public class FilmCursorAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;

    public FilmCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View view, Context context, Cursor cursor) {

        ImageView cover = (ImageView) view.findViewById(R.id.imageRow);
        String url = cursor.getString(cursor.getColumnIndex(FilmDataContract.FilmEntry.COLUMN_FILM_PROFILE));
        Picasso.with(view.getContext()).load(url).error(R.drawable.poster_default).fit().centerInside().into(cover);

        TextView textViewNumber = (TextView) view.findViewById(R.id.film_title_number);
        int id = cursor.getInt(cursor.getColumnIndex(FilmDataContract.FilmEntry._ID));
        textViewNumber.setText(id + ". ");

        TextView textViewTitle = (TextView) view.findViewById(R.id.film_title_text);
        String title = cursor.getString(cursor.getColumnIndex(FilmDataContract.FilmEntry.COLUMN_FILM_TITLE));
        textViewTitle.setText(title);

        TextView textViewRating = (TextView) view.findViewById(R.id.film_mpaa_rating);
        String rating = cursor.getString(cursor.getColumnIndex(FilmDataContract.FilmEntry.COLUMN_FILM_RATING));
        textViewRating.setText("(" + rating + ")");

        TextView textViewRuntime = (TextView) view.findViewById(R.id.film_runtime);
        String runtime = cursor.getString(cursor.getColumnIndex(FilmDataContract.FilmEntry.COLUMN_FILM_RUNTIME));
        textViewRuntime.setText(runtime + context.getString(R.string.minutes));

        TextView textViewCritics = (TextView) view.findViewById(R.id.film_critics_score);
        String critics = cursor.getString(cursor.getColumnIndex(FilmDataContract.FilmEntry.COLUMN_FILM_CRITICS));
        textViewCritics.setText(context.getString(R.string.critics) + critics + "/100");

        TextView textViewAudience = (TextView) view.findViewById(R.id.film_audience_score);
        String audience = cursor.getString(cursor.getColumnIndex(FilmDataContract.FilmEntry.COLUMN_FILM_AUDIENCE));
        textViewAudience.setText(context.getString(R.string.audience) + audience + "/100");

    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.list_item, parent, false);
    }
}
