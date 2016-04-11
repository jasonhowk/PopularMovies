package com.directv.jhowk.popularmovies.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.directv.jhowk.popularmovies.BuildConfig;
import com.directv.jhowk.popularmovies.model.PopularMovie;
import com.directv.jhowk.popularmovies.service.TMDBService;

import java.util.Collection;

/**
 * Created by Jason Howk.
 */
public class TMDBImageLoader extends AsyncTaskLoader {
    private static final String LOG_TAG = TMDBImageLoader.class.getSimpleName();


    public TMDBImageLoader(Context context) {
        super(context);
        Log.d(LOG_TAG, "TMDBImageLoader: Creating loader.");
    }

    @Override
    public Collection<PopularMovie> loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground: Attempting to load the popular results.");
        Collection<PopularMovie> result = null;

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
        // TODO: display a spinner or something?
//        if (takeContentChanged()) {
        forceLoad();
//        }
    }

    @Override
    public void deliverResult(Object data) {
        super.deliverResult(data);
        Log.d(LOG_TAG, "deliverResult: Delivering results...");
    }
}
