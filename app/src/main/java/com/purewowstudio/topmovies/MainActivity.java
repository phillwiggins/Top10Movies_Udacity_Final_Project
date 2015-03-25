package com.purewowstudio.topmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.purewowstudio.topmovies.fragments.FilmDetail;
import com.purewowstudio.topmovies.fragments.FilmList;
import com.purewowstudio.topmovies.sync.FilmSyncAdapter;

/**
 * Created by Phillip on 18/03/2015.
 */
public class MainActivity extends ActionBarActivity implements FilmList.Callback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.film_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.film_detail_container, new FilmDetail(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        FilmSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(FilmDetail.DETAIL_URI, contentUri);
            FilmDetail filmDetail = new FilmDetail();
            filmDetail.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.film_detail_container, filmDetail, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailFragment.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }

}

