package com.directv.jhowk.popularmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.directv.jhowk.popularmovies.model.TMDBContentItem;

import java.util.ArrayList;

/**
 * Created by Jason Howk
 */
public abstract class TMDBBaseLoader extends AsyncTaskLoader<ArrayList<TMDBContentItem>> {
    private static final String LOG_TAG = TMDBBaseLoader.class.getSimpleName();
    private ArrayList<TMDBContentItem> mContentItems;
    
    public TMDBBaseLoader(Context context) {
        super(context);
    }

    ///////////////////////////////////////////////////////////////////////////
    // AsyncTaskLoader Callbacks
    ///////////////////////////////////////////////////////////////////////////
    @Override
    abstract public ArrayList<TMDBContentItem> loadInBackground();

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

        if (mContentItems != null) {
            mContentItems = null;
        }
    }

    @Override
    public void onCanceled(ArrayList<TMDBContentItem> data) {
        Log.d(LOG_TAG, "onCanceled");
        super.onCanceled(data);
    }
}
