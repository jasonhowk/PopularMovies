package com.directv.jhowk.popularmovies.loader;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.directv.jhowk.popularmovies.BuildConfig;
import com.directv.jhowk.popularmovies.model.TMDBContentItem;
import com.directv.jhowk.popularmovies.service.TMDBService;

import java.util.ArrayList;

/**
 * Created by Jason Howk
 */
public class TMDBPopularLoader extends AsyncTaskLoader<ArrayList<TMDBContentItem>>{
    private static final String LOG_TAG = TMDBPopularLoader.class.getSimpleName();
    private ArrayList<TMDBContentItem> mContentItems;

    public TMDBPopularLoader(Context context) {
        super(context);
        Log.d(LOG_TAG, "TMDBPopularLoader: Creating instance.");
    }

    @Override
    public ArrayList<TMDBContentItem> loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground: Attempting to load the popular results.");
        ArrayList<TMDBContentItem> result = null;

        try {
            TMDBService tmdbService = TMDBService.get(getContext().getApplicationContext(), BuildConfig.TMDB_API_KEY);
            result = tmdbService.getMoviesMostPopular();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.d(LOG_TAG, "onStartLoading: On start loading...");
        Log.d(LOG_TAG, "onStartLoading: mcontentItems:" + mContentItems);
        if (mContentItems != null) {
            Log.d(LOG_TAG, "onStartLoading: Returning cached results.");
            deliverResult(mContentItems);
        }

        if (takeContentChanged() || mContentItems == null){
            Log.d(LOG_TAG, "onStartLoading: Force loading...");
            forceLoad();
        }
    }


    @Override
    public void deliverResult(ArrayList<TMDBContentItem> data) {
        Log.d(LOG_TAG, "deliverResult: Delivering results...");

        if (isReset()) {
            Log.d(LOG_TAG, "deliverResult: Reset.  NOT returning data...");
            return;
        }

        mContentItems = data;

        if (isStarted()) {
            super.deliverResult(mContentItems);
        }


    }
}
