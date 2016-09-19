package com.directv.jhowk.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
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
import com.directv.jhowk.popularmovies.loader.TMDBSectionLoader;
import com.directv.jhowk.popularmovies.model.TMDBContentItem;
import com.directv.jhowk.popularmovies.service.FavoriteService;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<TMDBContentItem>>, AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = PopularMoviesActivityFragment.class.getSimpleName();
    public static final String EXTRA_CONTENT_ITEM = "com.directv.jhowk.popularMovies.model.TMDBContentItem";
    private static final String PREFERENCE_SECTION_POSITION = "com.directv.jhowk.popularMovies.preference.section.position";
    private static final int TMDB_SECTION_LOADER_ID = 1;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GridView mGridView;
    private TypedArray mSectionsArray;
    private ArrayList<TMDBContentItem> mContentItems;
    private TMDBImageAdapter mImageAdapter;
    private SharedPreferences mPreferences;
    private View mPlaceholderView;

    private @StringRes int mSelectedSectionId;

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
        Spinner spinner = (Spinner) getActivity().findViewById(R.id.nav_spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.sections_rid, R.layout.nav_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        // Load last menu selection from preferences.
        Log.d(LOG_TAG, "onActivityCreated: Loading preferences...");
        int position = mPreferences.getInt(PREFERENCE_SECTION_POSITION, -1);
        if (position >= 0) {
            Log.d(LOG_TAG, "onActivityCreated: Setting spinner selection based on preferences.");
            spinner.setSelection(position);
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
            getLoaderManager().destroyLoader(TMDB_SECTION_LOADER_ID);

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
        return new TMDBSectionLoader(getActivity().getApplicationContext(), mSelectedSectionId);
    }

    ///////////////////////////////////////////////////////////////////////////
    // AdapterView OnItemSelectedListener
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int resId = mSectionsArray.getResourceId(position, -1);
        Log.d(LOG_TAG, "onItemSelected: selected resid:" + resId);
        if (resId == R.string.favorites) {
            Log.d(LOG_TAG, "onItemSelected: Favorites selected.");
            if (FavoriteService.getInstance(getActivity().getApplicationContext()).count() > 0) {
                Log.d(LOG_TAG, "onItemSelected: Load favorites...");
                mGridView.setVisibility(View.VISIBLE);
            } else {
                mGridView.setVisibility(View.GONE);
                if (mPlaceholderView == null) {
                    Log.d(LOG_TAG, "onItemSelected: Creating placeholder view.");
                    LayoutInflater inflater = (LayoutInflater) this.getActivity().getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    ViewGroup tmpParent = (ViewGroup) getView().findViewById(R.id.fragment).getParent();
                    mPlaceholderView = inflater.inflate(R.layout.no_content, tmpParent, false);
                    tmpParent.addView(mPlaceholderView,tmpParent.indexOfChild(mGridView));
                } else {
                    mPlaceholderView.setVisibility(View.VISIBLE);
                }
            }
        } else if (resId > 0) {
            if (mPlaceholderView != null) {
                Log.d(LOG_TAG, "onItemSelected: Setting placeholder view to GONE.");
                mPlaceholderView.setVisibility(View.GONE);
            }
            mGridView.setVisibility(View.VISIBLE);
            mSelectedSectionId = resId;
            getLoaderManager().restartLoader(TMDB_SECTION_LOADER_ID, null, this);
            // We have a valid resource.  Save.
            Log.d(LOG_TAG, "onItemSelected: setting preference.");
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putInt(PREFERENCE_SECTION_POSITION, position);
            editor.apply();
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
        getLoaderManager().restartLoader(TMDB_SECTION_LOADER_ID, null, this);
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
