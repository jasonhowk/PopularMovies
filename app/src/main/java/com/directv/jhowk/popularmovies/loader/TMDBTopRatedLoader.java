package com.directv.jhowk.popularmovies.loader;

import android.content.Context;
import android.util.Log;

import com.directv.jhowk.popularmovies.BuildConfig;
import com.directv.jhowk.popularmovies.model.TMDBContentItem;
import com.directv.jhowk.popularmovies.service.TMDBService;

import java.util.ArrayList;

/**
 * Created by Jason Howk
 */
public class TMDBTopRatedLoader extends TMDBBaseLoader {
    private static final String LOG_TAG = TMDBTopRatedLoader.class.getSimpleName();

    public TMDBTopRatedLoader(Context context) {
        super(context);
        Log.d(LOG_TAG, "TMDBTopRatedLoader: Creating loader.");
    }

    @Override
    public ArrayList<TMDBContentItem> loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground: Attempting to load top rated results.");
        ArrayList<TMDBContentItem> result = null;

        try {
            TMDBService tmdbService = TMDBService.get(getContext().getApplicationContext(), BuildConfig.TMDB_API_KEY);
            result = tmdbService.getMoviesTopRated();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
