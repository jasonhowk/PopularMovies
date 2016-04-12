package com.directv.jhowk.popularmovies;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.directv.jhowk.popularmovies.adapter.TMDBImageAdapter;
import com.directv.jhowk.popularmovies.loader.TMDBImageLoader;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks {
    private static final String LOG_TAG = PopularMoviesActivityFragment.class.getSimpleName();

    private static final int IMAGE_LOADER_ID = 1;
    private static final int POSTER_WIDTH = 500;

    private GridView mGridView;

    public PopularMoviesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        getLoaderManager().initLoader(IMAGE_LOADER_ID, null, this);//.forceLoad();

        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout)fragment.findViewById(R.id.swipeLayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(),"Refreshing list...", Toast.LENGTH_SHORT).show();
                // TEMP: Just wait 3sec and dismiss.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                },3000);
            }
        });

        mGridView = (GridView)fragment.findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(),"" + position, Toast.LENGTH_SHORT).show();
            }
        });

        return fragment;
    }

    ///////////////////////////////////////////////////////////////////////////
    // LoaderCallbacks
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onLoadFinished(android.support.v4.content.Loader loader, Object data) {
        Log.d(LOG_TAG, "onLoadFinished: Got on load finished.");
        if (data != null) {
            Log.d(LOG_TAG, "onLoadFinished: LOADER DATA: " + data.toString());
            switch (loader.getId()) {
                case IMAGE_LOADER_ID:
                    Log.d(LOG_TAG, "onLoadFinished: Loader finished... ");
                    getLoaderManager().destroyLoader(IMAGE_LOADER_ID);
            }

            /**
             * Set the number of columns.  We calculate as the auto_fit param does not
             * work without a defined column width.  So we can either set a width or
             * just calculate the number of columns manually.  Columns were chosen as
             * to attempt to maintain the relative poster size
             */
            Log.d(LOG_TAG, "onLoadFinished: WIDTH: " + getView().getWidth());
            double maxPosters = Math.floor(getView().getWidth() / POSTER_WIDTH);
            Log.d(LOG_TAG, "onLoadFinished: MAX POSTERS:" + maxPosters);

            mGridView.setNumColumns((int) maxPosters);
            mGridView.setAdapter(new TMDBImageAdapter(getContext(), (ArrayList) data));
        } else {
            Toast.makeText(getContext(),"Unable to download data.  Please try again.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader loader) {
        Log.d(LOG_TAG, "onLoaderReset: Got on loader reset.");
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader: Got on create loader.");
        switch (id) {
            case IMAGE_LOADER_ID:
                Log.d(LOG_TAG, "onCreateLoader: Start image loader...");
                return new TMDBImageLoader(getActivity().getApplicationContext());
        }
        return null;
    }
}
