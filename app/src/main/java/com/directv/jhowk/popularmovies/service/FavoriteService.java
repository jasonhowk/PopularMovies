package com.directv.jhowk.popularmovies.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.directv.jhowk.popularmovies.model.TMDBContentItem;
import com.directv.jhowk.popularmovies.provider.TMDBProviderContract;

import java.util.ArrayList;

/**
 * Created by 00r5478 on 5/9/16
 * Copyright (c) 2016 DIRECTV. All rights reserved.
 */
public class FavoriteService {
    private static final String LOG_TAG = FavoriteService.class.getSimpleName();
    private static FavoriteService sFavoriteService;
    private Context mContext;
    private Cursor mCursor;

    public static FavoriteService getInstance(Context context) {
        if (sFavoriteService == null) {
            sFavoriteService = new FavoriteService(context);
        }
        return sFavoriteService;
    }

    public FavoriteService(Context context) {
        Log.d(LOG_TAG, "FavoriteService: Creating instance.");
        mContext = context;
    }

    /**
     * Returns the current count of items identified as a favorite.
     * @return the total number of items.
     */
    public int count() {
        Log.d(LOG_TAG, "count");
        String[] mProjection = {
                TMDBProviderContract.TMDBFavorite.ID
        };

        Cursor mCursor = mContext.getContentResolver().query(
                Uri.withAppendedPath(TMDBProviderContract.AUTHORITY_URI,"favorite"),
                mProjection,
                null,
                null,
                null);
        if (mCursor == null) {
            Log.d(LOG_TAG, "count: Zero");
            return 0;
        } else {
            Log.d(LOG_TAG, "count: " + mCursor.getCount());
            return mCursor.getCount();
        }
    }

    /**
     * Returns all available favorites.
     * @return all favorites.
     */
    public ArrayList<TMDBContentItem> getAllFavorites() {
        Log.d(LOG_TAG, "getAllFavorites");
        if (this.count() == 0) {
            return null;
        } else {
            String[] mProjection = {
                    TMDBProviderContract.TMDBFavorite.ID,
                    TMDBProviderContract.TMDBFavorite.TITLE,
                    TMDBProviderContract.TMDBFavorite.OVERVIEW,
                    TMDBProviderContract.TMDBFavorite.BACKDROP_PATH,
                    TMDBProviderContract.TMDBFavorite.POSTER_PATH,
                    TMDBProviderContract.TMDBFavorite.RELEASE_DATE,
                    TMDBProviderContract.TMDBFavorite.VOTE_COUNT,
                    TMDBProviderContract.TMDBFavorite.VOTE_AVERAGE
            };

            Cursor mCursor = mContext.getContentResolver().query(
                    Uri.withAppendedPath(TMDBProviderContract.AUTHORITY_URI,"favorite"),
                    mProjection,
                    null,
                    null,
                    null);
            if (mCursor == null) {
                return null;
            } else if (mCursor.getCount() < 1) {
                return null;
            } else {
                Log.d(LOG_TAG, "getAllFavorites: Favorites existing...");
                ArrayList<TMDBContentItem> resultList = new ArrayList<>();

                while (mCursor.moveToNext()) {
                    TMDBContentItem tmpItem = new TMDBContentItem();
                    tmpItem.setId(mCursor.getString(mCursor.getColumnIndexOrThrow((TMDBProviderContract.TMDBFavorite.ID))));
                    tmpItem.setTitle(mCursor.getString(mCursor.getColumnIndexOrThrow((TMDBProviderContract.TMDBFavorite.TITLE))));
                    tmpItem.setOverview(mCursor.getString(mCursor.getColumnIndexOrThrow((TMDBProviderContract.TMDBFavorite.OVERVIEW))));
                    //tmpItem.setReleaseDate(mCursor.getString(mCursor.getColumnIndex((TMDBProviderContract.TMDBFavorite.RELEASE_DATE))));
                    //tmpItem.setUrl(mCursor.getString(mCursor.getColumnIndex((TMDBProviderContract.TMDBFavorite.URL))));
                    tmpItem.setVoteAverage(mCursor.getFloat(mCursor.getColumnIndexOrThrow((TMDBProviderContract.TMDBFavorite.VOTE_AVERAGE))));
                    tmpItem.setVoteCount(mCursor.getLong(mCursor.getColumnIndexOrThrow((TMDBProviderContract.TMDBFavorite.VOTE_COUNT))));
                    tmpItem.setBackdropPath(mCursor.getString(mCursor.getColumnIndexOrThrow((TMDBProviderContract.TMDBFavorite.BACKDROP_PATH))));
                    tmpItem.setPosterPath(mCursor.getString(mCursor.getColumnIndexOrThrow((TMDBProviderContract.TMDBFavorite.POSTER_PATH))));
                    resultList.add(tmpItem);
                }
                mCursor.close();
                return resultList;
            }
        }
    }

//    public boolean isFavorite(String contentId) {
//        String[] mProjection = {
//                TMDBProviderContract.TMDBContent.ID
//        };
//
//        Cursor mCursor = mContext.getContentResolver().query(
//                Uri.withAppendedPath(TMDBProviderContract.AUTHORITY_URI,"favorite"),
//                mProjection,
//                null,
//                null,
//                null);
//
//        if (mCursor == null) {
//            return false;
//        } else if (mCursor.getCount() < 1) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    /**
     * Adds an item as a favorite.
     * @param item The item to add.
     */
    public void addFavorite(TMDBContentItem item) {
        Log.d(LOG_TAG, "addFavorite: Adding item:" + item);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TMDBProviderContract.TMDBFavorite.ID, item.getId());
        contentValues.put(TMDBProviderContract.TMDBFavorite.TITLE, item.getTitle());
        contentValues.put(TMDBProviderContract.TMDBFavorite.OVERVIEW, item.getOverview());
        contentValues.put(TMDBProviderContract.TMDBFavorite.RELEASE_DATE, item.getReleaseDate().toString());
        contentValues.put(TMDBProviderContract.TMDBFavorite.URL,item.getItemURL());
        Uri favoriteInsert = mContext.getContentResolver().insert(
                Uri.withAppendedPath(TMDBProviderContract.AUTHORITY_URI,"favorite"),contentValues);
    }


}
