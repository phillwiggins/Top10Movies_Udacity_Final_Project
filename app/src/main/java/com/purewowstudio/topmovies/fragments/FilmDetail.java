package com.purewowstudio.topmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.purewowstudio.topmovies.R;
import com.purewowstudio.topmovies.data.FilmDataContract;
import com.purewowstudio.topmovies.data.FilmProvider;
import com.squareup.picasso.Picasso;

public class FilmDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ShareActionProvider mShareActionProvider;
    private String FilmString;
    private static final String FILM_SHARE_HASHTAG = " #Top10";
    private Uri mUri;
    private static final int DETAIL_LOADER = 0;

    public static final String DETAIL_URI = "URI";
    private ImageView DetailCover;
    private TextView DetailTextViewTitle, DetailTextRating, DetailTextRuntime, DetailTextCritics, DetailTextAudience, DetailTextSynopsis;

    private int COL_FILM_ID = 0;
    private int COL_FILM_TITLE = 1;
    private int COL_FILM_RATING = 2;
    private int COL_FILM_RUNTIME = 3;
    private int COL_FILM_CRITICS = 4;
    private int COL_FILM_AUDIENCE = 5;
    private int COL_FILM_SYNOPSIS = 6;
    private int COL_FILM_PROFILE = 7;

    public FilmDetail() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(FilmDetail.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        DetailCover = (ImageView) rootView.findViewById(R.id.detail_fragment_imageRow);
        DetailTextViewTitle = (TextView) rootView.findViewById(R.id.detail_fragment_title_text);
        DetailTextRating = (TextView) rootView.findViewById(R.id.detail_fragment_mpaa_rating);
        DetailTextRuntime = (TextView) rootView.findViewById(R.id.detail_fragment_runtime);
        DetailTextCritics = (TextView) rootView.findViewById(R.id.detail_fragment_critics_score);
        DetailTextAudience = (TextView) rootView.findViewById(R.id.detail_fragment_audience_score);
        DetailTextSynopsis = (TextView) rootView.findViewById(R.id.detail_fragment_synopsis);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.film_detail_fragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (FilmString != null) {
            mShareActionProvider.setShareIntent(createShareFilmIntent());
        }
    }

    private Intent createShareFilmIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, FilmString + FILM_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        int fix;
        int CursorNumber;

        if (null == mUri) {
            fix = 0;
        } else {
            CursorNumber = Integer.parseInt(mUri.toString().substring(mUri.toString().length() - 1));
            fix = CursorNumber + 1;
        }

        String selection = "_id = \"" + fix + "\"";

        if (null != mUri) {
            String[] fProjection = FilmDataContract.FilmEntry.COLUMNS;
            return new CursorLoader(
                    getActivity(),
                    FilmProvider.CONTENT_URI,
                    fProjection,
                    selection,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {

            String FilmTitle = data.getString(COL_FILM_TITLE);
            String FilmRating = data.getString(COL_FILM_RATING);
            String FilmRuntime = data.getString(COL_FILM_RUNTIME);
            String FilmCritics = data.getString(COL_FILM_CRITICS);
            String FilmAudience = data.getString(COL_FILM_AUDIENCE);
            String FilmSynopsis = data.getString(COL_FILM_SYNOPSIS);
            String FilmProfile = data.getString(COL_FILM_PROFILE);

            Picasso.with(getActivity()).load(FilmProfile).error(R.drawable.poster_default).fit().centerInside().into(DetailCover);

            DetailTextViewTitle.setText(FilmTitle);
            DetailTextRating.setText("(" + FilmRating + ")");
            DetailTextRuntime.setText(FilmRuntime+ " " + getString(R.string.minutes));
            DetailTextCritics.setText(getString(R.string.critics) + FilmCritics + "/100");
            DetailTextAudience.setText(getString(R.string.audience) + FilmAudience + "/100");
            DetailTextSynopsis.setText(FilmSynopsis);

            FilmString = getString(R.string.share_first) + FilmTitle + getString(R.string.share_middle) + FilmCritics + getString(R.string.share_end) + FILM_SHARE_HASHTAG;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}