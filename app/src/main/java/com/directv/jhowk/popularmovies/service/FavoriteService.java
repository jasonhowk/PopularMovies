package com.directv.jhowk.popularmovies.service;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.directv.jhowk.popularmovies.model.TMDBContentItem;
import com.directv.jhowk.popularmovies.provider.TMDBProviderContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by 00r5478 on 5/9/16
 * Copyright (c) 2016 DIRECTV. All rights reserved.
 */
public class FavoriteService {
    private static final String LOG_TAG = FavoriteService.class.getSimpleName();
    @SuppressLint("StaticFieldLeak") // this doesn't leak because we're grabbing the application context.
    private static FavoriteService sFavoriteService;
    private Context mContext;

    private Integer mCachedCount = 0;
    private Hashtable<String,TMDBContentItem> mCachedItems = null;

    private FavoriteService(Context context) {
        Log.d(LOG_TAG, "FavoriteService: Creating instance.");
        mContext = context;
        updateCache();
    }

    public static FavoriteService getInstance(Context context) {
        if (sFavoriteService == null) {
            sFavoriteService = new FavoriteService(context.getApplicationContext());

        }
        return sFavoriteService;
    }

    /**
     * Returns the current count of items identified as a favorite.
     * @return the total number of items.
     */
    public int count() {
        return mCachedCount;
    }

    /**
     * Returns all available favorites.
     * @return all favorites.
     */
    public ArrayList<TMDBContentItem> getAllFavorites() {
        return new ArrayList<>(mCachedItems.values());
    }

    public boolean isFavorite(String contentId) {
        if (mCachedItems.containsKey(contentId)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds an item as a favorite.
     * @param item The item to add.
     */
    public void addFavorite(TMDBContentItem item) {
        Log.d(LOG_TAG, "addFavorite: Adding item:" + item);
        ContentValues contentValues = new ContentValues();
        contentValues.put(TMDBProviderContract.TMDBFavorite.ID, item.getId());
        contentValues.put(TMDBProviderContract.TMDBFavorite.JSON, item.getJSONObject().toString());
        mContext.getContentResolver().insert(TMDBProviderContract.TMDBFavorite.CONTENT_URI,contentValues);

        updateCache();
    }

    public void removeFavorite(TMDBContentItem item) {
        Log.d(LOG_TAG, "removeFavorite: Removing item:" + item);
        String[]  mSelectionArgs = {item.getId()};
        mContext.getContentResolver().delete(
                TMDBProviderContract.TMDBFavorite.CONTENT_URI,
                TMDBProviderContract.TMDBFavorite._ID + " = ?",
                mSelectionArgs);

        updateCache();
    }

    private void updateCache() {
        Log.d(LOG_TAG, "updateCache: Updating cache.");
        String[] mProjection = {
                TMDBProviderContract.TMDBFavorite.ID,
                TMDBProviderContract.TMDBFavorite.JSON
        };

        Cursor mCursor = mContext.getContentResolver().query(
                TMDBProviderContract.TMDBFavorite.CONTENT_URI,
                mProjection,
                null,
                null,
                null);
        if (mCursor == null) {
            mCachedCount = null;
            mCachedItems = null;
        } else if (mCursor.getCount() < 1) {
            mCachedCount = 0;
            mCachedItems = new Hashtable<>();
        } else {
            Hashtable<String,TMDBContentItem> tmpHashTable = new Hashtable<>();

            while (mCursor.moveToNext()) {
                TMDBContentItem tmpItem;
                try {
                    String sJson = mCursor.getString(mCursor.getColumnIndexOrThrow((TMDBProviderContract.TMDBFavorite.JSON)));
                    tmpItem = new TMDBContentItem(new JSONObject(sJson));
                    tmpHashTable.put(tmpItem.getId(),tmpItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mCursor.close();

            mCachedItems = tmpHashTable;
            mCachedCount = mCachedItems.size();
        }
        Log.d(LOG_TAG, "updateCache: Cached Items:" + mCachedItems);
        Log.d(LOG_TAG, "updateCache: Cached Count:" + mCachedCount);
    }


}
