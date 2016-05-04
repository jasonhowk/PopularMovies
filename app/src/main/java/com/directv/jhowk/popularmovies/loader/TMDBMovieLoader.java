package com.directv.jhowk.popularmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.directv.jhowk.popularmovies.BuildConfig;
import com.directv.jhowk.popularmovies.model.TMDBContentItem;
import com.directv.jhowk.popularmovies.service.TMDBService;


/**
 * Created by Jason Howk
 */
public class TMDBMovieLoader extends AsyncTaskLoader<TMDBContentItem> {
    private static final String LOG_TAG = TMDBMovieLoader.class.getSimpleName();
    private TMDBContentItem mContentItem;
    private String mMovieId;

    public TMDBMovieLoader(Context context,String movieId) throws Exception{
        super(context);
        mMovieId = movieId;
        if (mMovieId == null) {
            throw new Exception("Movie id required.");
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // AsyncTaskLoader Callbacks
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public TMDBContentItem loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground: Attempting to load data for movie: " + mMovieId);
        TMDBContentItem movie = null;
        try {
            TMDBService tmdbService = TMDBService.get(BuildConfig.TMDB_API_KEY);
            movie = tmdbService.getMovie(mMovieId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movie;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.d(LOG_TAG, "onStartLoading: On start loading...");
        Log.d(LOG_TAG, "onStartLoading: contentItem:" + mContentItem);
        if (mContentItem != null) {
            Log.d(LOG_TAG, "onStartLoading: Returning cached results.");
            deliverResult(mContentItem);
        }

        if (takeContentChanged() || mContentItem == null){
            Log.d(LOG_TAG, "onStartLoading: Force loading...");
            forceLoad();
        }
    }


    @Override
    public void deliverResult(TMDBContentItem data) {
        Log.d(LOG_TAG, "deliverResult: Delivering results...");

        if (isReset()) {
            Log.d(LOG_TAG, "deliverResult: Reset.  NOT returning data...");
            return;
        }

        mContentItem = data;

        if (isStarted()) {
            super.deliverResult(mContentItem);
        }
    }

    @Override
    protected void onStopLoading() {
        Log.d(LOG_TAG, "onStopLoading");
        cancelLoad();
    }

    @Override
    protected void onReset() {
        Log.d(LOG_TAG, "onReset");
        super.onReset();

        onStopLoading();

        if (mContentItem != null) {
            mContentItem = null;
        }
    }

    @Override
    public void onCanceled(TMDBContentItem data) {
        Log.d(LOG_TAG, "onCanceled");
        super.onCanceled(data);
    }


}
