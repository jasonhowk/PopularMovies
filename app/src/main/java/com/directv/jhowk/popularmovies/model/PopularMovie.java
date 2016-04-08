package com.directv.jhowk.popularmovies.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

/**
 * Object representing a movie identified in TMDB.org's popular query.
 *
 * Created by Jason Howk
 */
public class PopularMovie {
    // JSON API Keys
    private static final String POSTER_PATH = "poster_path";
    private static final String ADULT = "adult";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String GENRE_IDS = "genre_ids";
    private static final String ID = "id";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String ORIGINAL_LANGUAGE = "original_language";
    private static final String TITLE = "title";
    private static final String BACKDROP_PATH = "backdrop_path";
    private static final String POPULARITY = "popularity";
    private static final String VOTE_COUNT = "vote_count";
    private static final String VIDEO = "video";
    private static final String VOTE_AVERAGE = "vote_average";

    private JSONObject mJSONObject;
    private String mId;
    private String mTitle;
    private String mOriginalTitle;
    private String mOriginalLanguage;
    private Boolean mAdult;
    private String mOverview;
    private String mPosterPath;
    private Date mReleaseDate;
    private Vector<Integer> mGenreIds;
    private String mBackdropPath;
    private Float mPopularity;
    private Long mVoteCount;
    private Boolean mVideo;
    private Float mVoteAverage;

    public PopularMovie(JSONObject JSONObject) {
        mJSONObject = JSONObject;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            mId = mJSONObject.getString(ID);
            mTitle = mJSONObject.getString(TITLE);
            mOriginalTitle = mJSONObject.getString(ORIGINAL_TITLE);
            mOriginalLanguage = mJSONObject.getString(ORIGINAL_LANGUAGE);
            mPosterPath = mJSONObject.getString(POSTER_PATH);
            mBackdropPath = mJSONObject.getString(BACKDROP_PATH);
            mAdult = mJSONObject.getBoolean(ADULT);
            mOverview = mJSONObject.getString(OVERVIEW);
            mReleaseDate = dateFormat.parse(mJSONObject.getString(RELEASE_DATE),new ParsePosition(0));
            mPopularity = Float.parseFloat(mJSONObject.getString(POPULARITY));
            mVoteCount = mJSONObject.getLong(VOTE_COUNT);
            mVideo = mJSONObject.getBoolean(VIDEO);
            mVoteAverage = Float.parseFloat(mJSONObject.getString(VOTE_AVERAGE));
            // TODO: finish genre ids.
//             mGenreIds = mJSONObject.getJSONArray(GENRE_IDS).
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public JSONObject getJSONObject() {
        return mJSONObject;
    }

    public void setJSONObject(JSONObject JSONObject) {
        mJSONObject = JSONObject;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        mOriginalLanguage = originalLanguage;
    }

    public Boolean getAdult() {
        return mAdult;
    }

    public void setAdult(Boolean adult) {
        mAdult = adult;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public Date getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        mReleaseDate = releaseDate;
    }

    public Vector<Integer> getGenreIds() {
        return mGenreIds;
    }

    public void setGenreIds(Vector<Integer> genreIds) {
        mGenreIds = genreIds;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        mBackdropPath = backdropPath;
    }

    public Float getPopularity() {
        return mPopularity;
    }

    public void setPopularity(Float popularity) {
        mPopularity = popularity;
    }

    public Long getVoteCount() {
        return mVoteCount;
    }

    public void setVoteCount(Long voteCount) {
        mVoteCount = voteCount;
    }

    public Boolean getVideo() {
        return mVideo;
    }

    public void setVideo(Boolean video) {
        mVideo = video;
    }

    public Float getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(Float voteAverage) {
        mVoteAverage = voteAverage;
    }
}
