package com.directv.jhowk.popularmovies.service;

import android.net.Uri;
import android.util.Log;

import com.directv.jhowk.popularmovies.model.TMDBContentItem;

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

/**
 * TMDB.org (http://themoviedb.org) Service Interface.
 * <p/>
 * Created by Jason Howk.
 */
public class TMDBService {
    private static final String LOG_TAG = TMDBService.class.getSimpleName();

    // API Constants.
    private static final String BASE_URL = "http://api.themoviedb.org/3"; // Will be replaced with call to configuration.
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w500"; // Will be replaced with call to configuration.
    private static final String BASE_BACKDROP_URL = "http://image.tmdb.org/t/p/w780"; // Will be replaced with call to configuration.
    private static final String API_KEY = "api_key";

    private static final String CONFIGURATION_URL = "configuration";
    private static final String POPULAR_URL = "movie/popular";
    private static final String TOP_RATED_URL = "movie/top_rated";
    private static final String NOW_PLAYING_URL = "movie/now_playing";
    private static final String UPCOMING_URL = "movie/upcoming";

    private static final String APPEND_TO_RESPONSE = "append_to_response";
    private static final String ATR_SECTIONS = "trailers,reviews";


    private static TMDBService sTMDBService;
    private final String mApiKey;


    private TMDBService(String apiKey) {
        mApiKey = apiKey;
    }

    /**
     * Retrieves the current TMDB data store.
     *
     * @param apiKey The API key. Must NOT be null.
     * @return The operational data store configured with context and api key.
     * @throws Exception
     */
    public static TMDBService get(@SuppressWarnings("SameParameterValue") String apiKey) throws Exception {
        if (sTMDBService == null) {
            if (apiKey != null) {
                sTMDBService = new TMDBService(apiKey);
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
     * <p/>
     * http://api.themoviedb.org/3/movie/popular?api_key=xxxx
     *
     * @return Collection of TMDBContentItems
     * @throws Exception
     */
    public ArrayList<TMDBContentItem> getMoviesMostPopular() throws Exception {
        Log.d(LOG_TAG, "getMoviesMostPopular: Calling /movie/popular");
        Uri tmdbUri = getBaseUriBuilder()
                .appendEncodedPath(POPULAR_URL)
                .appendQueryParameter(APPEND_TO_RESPONSE,ATR_SECTIONS)
                .build();
        return parseResult(getDataForUri(tmdbUri));
    }

    /***
     * Get the list of top rated movies. By default, this list will only include
     * movies that have 50 or more votes. This list refreshes every day.
     * <p/>
     * http://api.themoviedb.org/3/movie/top_rated?api_key=xxxx
     *
     * @return Collection of TMDBContentItems
     * @throws Exception
     */
    public ArrayList<TMDBContentItem> getMoviesTopRated() throws Exception {
        Log.d(LOG_TAG, "getMoviesTopRated: Calling /movie/top_rated");
        Uri tmdbUri = getBaseUriBuilder()
                .appendEncodedPath(TOP_RATED_URL)
                .build();
        return parseResult(getDataForUri(tmdbUri));
    }

    /**
     * Get the list of movies playing that have been, or are being released this week.
     * This list refreshes every day.
     *
     * @return Collection of TMDBContentItems
     * @throws Exception
     */
    public ArrayList<TMDBContentItem> getMoviesNowPlaying() throws Exception {
        Log.d(LOG_TAG, "getMoviesNowPlaying: Calling /movie/now_playing");
        Uri tmdbUri = getBaseUriBuilder()
                .appendEncodedPath(NOW_PLAYING_URL)
                .build();
        return parseResult(getDataForUri(tmdbUri));
    }

    /**
     * Get the list of upcoming movies by release date. This list refreshes every day.
     *
     * @return Collection of TMDBContentItems
     * @throws Exception
     */
    public ArrayList<TMDBContentItem> getMoviesUpcoming() throws Exception {
        Log.d(LOG_TAG, "getMoviesUpcoming: Calling /movie/upcoming");
        Uri tmdbUri = getBaseUriBuilder()
                .appendEncodedPath(UPCOMING_URL)
                .build();
        return parseResult(getDataForUri(tmdbUri));
    }

    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    public static String getImagesBaseURL() {
        return BASE_IMAGE_URL;
    }

    public static String getBackdropBaseURL() {
        return BASE_BACKDROP_URL;
    }

    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE
    ///////////////////////////////////////////////////////////////////////////

    private Uri.Builder getBaseUriBuilder() {
        // NOTE: For now, the append_to_response is located here as all
        // calling methods are using it.  Once the service grows, it should be
        // moved into calling methods.
        Uri.Builder tmdbBaseUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY, mApiKey)
                .appendQueryParameter(APPEND_TO_RESPONSE,ATR_SECTIONS);
        return tmdbBaseUri;
    }

    private String getDataForUri(Uri resourceUri) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String sResult = null;

        Log.d(LOG_TAG, "Staring URL connection.");

        try {
            URL url = new URL(resourceUri.toString());
            Log.d(LOG_TAG, "URL: " + url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            if (builder.length() == 0) {
                // Empty string.
                return null;
            }

            sResult = builder.toString();
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

    private ArrayList<TMDBContentItem> parseResult(String jsonString) throws JSONException {
        Log.d(LOG_TAG, "parsePopular: Parsing most popular response.");
        ArrayList<TMDBContentItem> tmdbContentItems = new ArrayList<>();
        // Parse Data.
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                tmdbContentItems.add(new TMDBContentItem(jsonArray.getJSONObject(i)));
            }
        }
        return tmdbContentItems;
    }
}
