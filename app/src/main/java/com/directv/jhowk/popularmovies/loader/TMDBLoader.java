package com.directv.jhowk.popularmovies.loader;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.Log;

import com.directv.jhowk.popularmovies.BuildConfig;
import com.directv.jhowk.popularmovies.R;
import com.directv.jhowk.popularmovies.model.TMDBContentItem;
import com.directv.jhowk.popularmovies.service.TMDBService;

import java.util.ArrayList;

/**
 * Created by 00r5478 on 4/19/16
 * Copyright (c) 2016 DIRECTV. All rights reserved.
 */
public class TMDBLoader extends TMDBBaseLoader{
    private static final String LOG_TAG = TMDBLoader.class.getSimpleName();
    private final @StringRes int mResId;

    public TMDBLoader(Context context, @StringRes int queryResId) {
        super(context);
        mResId = queryResId;
    }

    @Override
    public ArrayList<TMDBContentItem> loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground: Attempting to load the popular results.");
        ArrayList<TMDBContentItem> result = null;
        try {
            TMDBService tmdbService = TMDBService.get(BuildConfig.TMDB_API_KEY);
            switch (mResId) {
                case R.string.popular:
                    result = tmdbService.getMoviesMostPopular();
                    break;
                case R.string.topRated:
                    result = tmdbService.getMoviesTopRated();
                    break;
                case R.string.nowPlaying:
                    result = tmdbService.getMoviesNowPlaying();
                    break;
                case R.string.upcoming:
                    result = tmdbService.getMoviesUpcoming();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
