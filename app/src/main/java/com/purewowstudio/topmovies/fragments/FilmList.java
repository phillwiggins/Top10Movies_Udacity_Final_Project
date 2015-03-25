package com.purewowstudio.topmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.purewowstudio.topmovies.R;
import com.purewowstudio.topmovies.SettingsActivity;
import com.purewowstudio.topmovies.data.FilmDataContract;
import com.purewowstudio.topmovies.data.FilmProvider;
import com.purewowstudio.topmovies.sync.FilmSyncAdapter;
import com.purewowstudio.topmovies.util.FilmCursorAdapter;

/**
 * Created by Phillip on 18/03/2015.
 */
public class FilmList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView filmList;
    Cursor fCursor;
    FilmCursorAdapter filmCursorAdapter;
    private static final int FILM_LOADER = 0;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    public FilmList() {
    }

    public interface Callback {
        public void onItemSelected(Uri filmUri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);

        filmList = (ListView) rootView.findViewById(R.id.listview);
        filmCursorAdapter = new FilmCursorAdapter(getActivity(), fCursor, 0);
        filmList.setAdapter(filmCursorAdapter);

        filmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(FilmDataContract.buildFilm(position));
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.action_force) {
            forceFilmTask();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FILM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void forceFilmTask() {
        startFilmTask();
    }

    public void startFilmTask() {
        FilmSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        startFilmTask();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] fProjection = FilmDataContract.FilmEntry.COLUMNS;

        return new CursorLoader(getActivity(),
                FilmProvider.CONTENT_URI,
                fProjection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        filmCursorAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            filmList.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        filmCursorAdapter.swapCursor(null);
    }

}