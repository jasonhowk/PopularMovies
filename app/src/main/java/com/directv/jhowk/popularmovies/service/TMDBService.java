package com.directv.jhowk.popularmovies.service;

import android.content.Context;
import android.util.Log;

import com.directv.jhowk.popularmovies.model.PopularMovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * TMDB.org (http://themoviedb.org) Service Interface.
 *
 * Created by Jason Howk.
 */
public class TMDBService {
    private static final String LOG_TAG = TMDBService.class.getSimpleName();

    // API Constants.
    private static final String BASE_URL = "http://api.themoviedb.org/3"; // Will be replaced with call to configuration.
    private static final String API_KEY = "api_key";
    private static final String CONFIGURATION_URL = "configuration";
    private static final String POPULAR_URL = "movie/popular";
    private static final String TOP_RATED_URL = "movie/top_rated";

    private static TMDBService sTMDBService;
    private Context mContext;
    private String mApiKey;

    
    private TMDBService(Context context, String apiKey) {
        mContext = context;
        mApiKey = apiKey;
    }

    /**
     * Retrieves the current TMDB data store.
     *
     * @param context The application context. Must not be NULL.
     * @param apiKey The API key. Must NOT be null.
     * @return The operational data store configured with context and api key.
     * @throws Exception
     */
    public static TMDBService get(Context context, String apiKey) throws Exception {
        if (sTMDBService == null) {
            if (context != null && apiKey != null) {
                sTMDBService = new TMDBService(context.getApplicationContext(), apiKey);
            } else {
                throw new Exception("Unable to instantiate store. Missing/invalid required parameters.");
            }
        }
        return sTMDBService;
    }

    ///////////////////////////////////////////////////////////////////////////
    // themoviedb.org API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get the list of popular movies on The Movie Database. This list refreshes
     * every day.
     *
     * http://api.themoviedb.org/3/movie/popular?api_key=xxxx
     * @return Collection of PopularMovie
     */
    public Collection<PopularMovie> getMoviesMostPopular() throws Exception {
        Log.d(LOG_TAG, "getMoviesMostPopular: Calling /movie/popular");
        return parsePopular(getURL(POPULAR_URL));
    }

    /***
     * Get the list of top rated movies. By default, this list will only include
     * movies that have 50 or more votes. This list refreshes every day.
     *
     * http://api.themoviedb.org/3/movie/top_rated?api_key=xxxx
     * @return String JSON result string (for now.)
     */
    public String getMoviesTopRated() {
        Log.d(LOG_TAG,"Calling /movie/top_rated");
        return getURL(TOP_RATED_URL);
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE
    ///////////////////////////////////////////////////////////////////////////

    private String getURL(String relativePath) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String sResult = null;

        Log.d(LOG_TAG,"Staring URL connection.");

        try {
            String sAbsoluteURL = String.format("%s/%s?%s=%s",BASE_URL,relativePath,API_KEY,mApiKey);
            URL url = new URL(sAbsoluteURL);
            Log.d(LOG_TAG, "URL: " + url.toString());

            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Empty string.
                return null;
            }

            sResult = buffer.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream.");
                }
            }
        }
        return sResult;
    }

    private ArrayList<PopularMovie> parsePopular(String jsonString) throws JSONException {
        Log.d(LOG_TAG, "parsePopular: Parsing most popular response.");
        ArrayList<PopularMovie> popularMovies = new ArrayList<>();
        // Parse Data.
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length() - 1; i++) {
                popularMovies.add(new PopularMovie(jsonArray.getJSONObject(i)));
            }
        }
        return popularMovies;
    }
}