package com.directv.jhowk.popularmovies.loader;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.Log;

import com.directv.jhowk.popularmovies.BuildConfig;
import com.directv.jhowk.popularmovies.R;
import com.directv.jhowk.popularmovies.model.TMDBContentItem;
import com.directv.jhowk.popularmovies.service.FavoriteService;
import com.directv.jhowk.popularmovies.service.TMDBService;

import java.util.ArrayList;

/**
 * Created by Jason Howk.
 */
public class TMDBSectionLoader extends TMDBBaseLoader{
    private static final String LOG_TAG = TMDBSectionLoader.class.getSimpleName();
    private final @StringRes int mResId;

    public TMDBSectionLoader(Context context, @StringRes int queryResId) {
        super(context);
        mResId = queryResId;
    }

    @Override
    public ArrayList<TMDBContentItem> loadInBackground() {
        Log.d(LOG_TAG, "loadInBackground: Attempting to load results.");
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
                case R.string.favorites:
                    Log.d(LOG_TAG, "loadInBackground: loading favorites");
                    FavoriteService favoriteService = FavoriteService.getInstance(getContext());
                    result = favoriteService.getAllFavorites();
                    Log.d(LOG_TAG, "loadInBackground: RESULTS: " + result);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
