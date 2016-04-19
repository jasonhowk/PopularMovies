package com.directv.jhowk.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
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
    public static final String PREFERENCE_SECTION_POSITION = "com.directv.jhowk.popularMovies.preference.section.position";

    private static final int MOVIE_POPULAR_LOADER_ID = 1;
    private static final int MOVIE_TOP_RATED_LOADER_ID = 2;

    private static final int POSTER_WIDTH = 500;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GridView mGridView;
    private Spinner mSpinner;
    private TypedArray mSectionsArray;
    private int mCurrentLoaderResId;
    private ArrayList<TMDBContentItem> mContentItems;
    private TMDBImageAdapter mImageAdapter;
    private SharedPreferences mPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate: Creating fragment.");
        // Preferences preferences.
        mPreferences = getActivity().getPreferences(Activity.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView: Creating view.");
        View fragment = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) fragment.findViewById(R.id.swipeLayout);
        mGridView = (GridView) fragment.findViewById(R.id.gridview);

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "onActivityCreated: Activity created...");

        // Spinner
        mSectionsArray = getResources().obtainTypedArray(R.array.sections_rid);
        mSpinner = (Spinner) getActivity().findViewById(R.id.nav_spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.sections_rid, R.layout.nav_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(arrayAdapter);
        mSpinner.setOnItemSelectedListener(this);

        // Load last menu selection from preferences.
        Log.d(LOG_TAG, "onActivityCreated: Loading preferences...");
        int position = mPreferences.getInt(PREFERENCE_SECTION_POSITION, -1);
        if (position >= 0) {
            Log.d(LOG_TAG, "onActivityCreated: Setting spinner selection based on preferences.");
            mSpinner.setSelection(position);
        }
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
             * work without a defined column width.  However, to roughly estimate a
             * comfortable number of column, a rudimentary algorithm was created that
             * calculates the number of colums directly based on the actual display width
             * and the density. i.e.
             *      Math.floor(getView().getWidth() / displayMetrics.densityDpi)
             */
            if (getView() != null) {
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                Log.d(LOG_TAG, "onLoadFinished: DENSITY DPI: " + displayMetrics.densityDpi);
                Log.d(LOG_TAG, "onLoadFinished: WIDTH: " + getView().getWidth());
                double maxPosters = Math.floor(getView().getWidth() / displayMetrics.densityDpi);
                Log.d(LOG_TAG, "onLoadFinished: MAX POSTERS:" + maxPosters);
                mGridView.setNumColumns((int) maxPosters);
            }
            mContentItems = data;
            if (mImageAdapter == null) {
                mImageAdapter = new TMDBImageAdapter(this.getActivity().getApplicationContext(), mContentItems);
                mGridView.setAdapter(mImageAdapter);
            } else {
                mImageAdapter.setContentItems(mContentItems);
            }

        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Unable to download data.  Please try again.", Toast.LENGTH_SHORT).show();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<TMDBContentItem>> loader) {
        Log.d(LOG_TAG, "onLoaderReset: Got on loader reset.");
    }

    @Override
    public Loader<ArrayList<TMDBContentItem>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader: Got on create loader.");
        Loader<ArrayList<TMDBContentItem>> loader = null;
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
                getLoaderManager().initLoader(MOVIE_POPULAR_LOADER_ID, null, this);
                break;
            case R.string.topRated:
                Log.d(LOG_TAG, "onItemSelected: Top Rated selected.");
                getLoaderManager().initLoader(MOVIE_TOP_RATED_LOADER_ID, null, this);
                break;
        }
        if (resId > 0) {
            // We have a valid resource.  Save.
            Log.d(LOG_TAG, "onItemSelected: setting preference.");
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putInt(PREFERENCE_SECTION_POSITION, position);
            editor.commit();
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
                Intent intent = new Intent(getActivity(), PopularMoviesDetailActivity.class);
                Log.d(LOG_TAG, "onItemClick: Creating intent for detail: " + mContentItems.get(position).getTitle());
                intent.putExtra(EXTRA_CONTENT_ITEM, mContentItems.get(position));
                startActivity(intent);
            }
        });
    }
}
