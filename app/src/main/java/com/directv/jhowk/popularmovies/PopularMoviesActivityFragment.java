package com.directv.jhowk.popularmovies;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.directv.jhowk.popularmovies.adapter.TMDBImageAdapter;
import com.directv.jhowk.popularmovies.loader.TMDBPopularLoader;
import com.directv.jhowk.popularmovies.loader.TMDBTopRatedLoader;
import com.directv.jhowk.popularmovies.model.TMDBContentItem;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<TMDBContentItem>>, AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = PopularMoviesActivityFragment.class.getSimpleName();

    public static final String EXTRA_CONTENT_ITEM = "com.directv.jhowk.popularMovies.model.TMDBContentItem";

    private static final int MOVIE_POPULAR_LOADER_ID = 1;
    private static final int MOVIE_TOP_RATED_LOADER_ID = 2;

    private static final int POSTER_WIDTH = 500;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GridView mGridView;
    private Spinner mSpinner;
    private TypedArray mSectionsArray;
    private @IdRes int mCurrentLoaderResId;
    private ArrayList<TMDBContentItem> mContentItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        getLoaderManager().initLoader(MOVIE_POPULAR_LOADER_ID, null, this);//.forceLoad();

        mSwipeRefreshLayout = (SwipeRefreshLayout) fragment.findViewById(R.id.swipeLayout);
        mGridView = (GridView) fragment.findViewById(R.id.gridview);

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Spinner
        mSectionsArray = getResources().obtainTypedArray(R.array.sections_rid);
        mSpinner = (Spinner) getActivity().findViewById(R.id.nav_spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sections_rid, R.layout.nav_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(arrayAdapter);
        mSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSectionsArray.recycle();
    }

    ///////////////////////////////////////////////////////////////////////////
    // LoaderManager.LoaderCallbacks
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onLoadFinished(Loader<ArrayList<TMDBContentItem>> loader, ArrayList<TMDBContentItem> data) {
        Log.d(LOG_TAG, "onLoadFinished: Got on load finished.");
        if (data != null) {
            Log.d(LOG_TAG, "onLoadFinished: Data received. ");

            // Configure grid listeners.
            configureGridListeners();

            // Destroy loader.
            switch (loader.getId()) {
                case MOVIE_POPULAR_LOADER_ID:
                    getLoaderManager().destroyLoader(MOVIE_POPULAR_LOADER_ID);
                    break;
                case MOVIE_TOP_RATED_LOADER_ID:
                    getLoaderManager().destroyLoader(MOVIE_TOP_RATED_LOADER_ID);
                    break;
            }

            /**
             * Set the number of columns.  We calculate as the auto_fit param does not
             * work without a defined column width.  So we can either set a width or
             * just calculate the number of columns manually.  Columns were chosen as
             * to attempt to maintain the relative poster size
             */
            if (getView() != null) {
                Log.d(LOG_TAG, "onLoadFinished: WIDTH: " + getView().getWidth());
                double maxPosters = Math.floor(getView().getWidth() / POSTER_WIDTH);
                Log.d(LOG_TAG, "onLoadFinished: MAX POSTERS:" + maxPosters);
                mGridView.setNumColumns((int) maxPosters);
            }
            mContentItems = data;
            mGridView.setAdapter(new TMDBImageAdapter(getContext(), mContentItems));
        } else {
            Toast.makeText(getContext(), "Unable to download data.  Please try again.", Toast.LENGTH_SHORT).show();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader loader) {
        Log.d(LOG_TAG, "onLoaderReset: Got on loader reset.");
    }

    @Override
    public Loader<ArrayList<TMDBContentItem>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader: Got on create loader.");
        Loader loader = null;
        switch (id) {
            case MOVIE_POPULAR_LOADER_ID:
                Log.d(LOG_TAG, "onCreateLoader: Start popular loader...");
                loader = new TMDBPopularLoader(getActivity().getApplicationContext());
                break;
            case MOVIE_TOP_RATED_LOADER_ID:
                Log.d(LOG_TAG, "onCreateLoader: Start top rated loader...");
                loader = new TMDBTopRatedLoader(getActivity().getApplicationContext());
                break;
        }
        mCurrentLoaderResId = id;
        return loader;
    }

    ///////////////////////////////////////////////////////////////////////////
    // AdapterView OnItemSelectedListener
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int resId = mSectionsArray.getResourceId(position, -1);
        Log.d(LOG_TAG, "onItemSelected: selected resid:" + resId);
        switch (resId) {
            case R.string.popular:
                Log.d(LOG_TAG, "onItemSelected: Popular selected.");
                getLoaderManager().restartLoader(MOVIE_POPULAR_LOADER_ID, null, this);
                break;
            case R.string.topRated:
                Log.d(LOG_TAG, "onItemSelected: Top Rated selected.");
                getLoaderManager().restartLoader(MOVIE_TOP_RATED_LOADER_ID, null, this);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(LOG_TAG, "onNothingSelected.");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private Methods
    ///////////////////////////////////////////////////////////////////////////
    private void refresh() {
        Log.d(LOG_TAG, "refresh: Restarting loader...");
        getLoaderManager().restartLoader(mCurrentLoaderResId, null, this);
    }

    private void configureGridListeners() {
        // Swipe Refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(LOG_TAG, "onRefresh: Refreshing data.");
                refresh();
            }
        });

        // GridView
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity() ,PopularMoviesDetailActivity.class);
                Log.d(LOG_TAG, "onItemClick: Creating intent for detail: " + mContentItems.get(position).getTitle());
                intent.putExtra(EXTRA_CONTENT_ITEM, mContentItems.get(position));
                startActivity(intent);
            }
        });
    }
}
