package com.directv.jhowk.popularmovies.service;

import android.content.ContentProviderOperation;
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
        return 0;
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
            // TODO: Finish.
            return null;
        }

    }

    /**
     * Adds an item as a favorite.
     * @param item The item to add.
     */
    public void addFavorite(TMDBContentItem item) {
        Log.d(LOG_TAG, "addFavorite: Adding item:" + item);

        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();

        // Insert Values.
        int insertIndex = ops.size();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TMDBProviderContract.TMDBContent.ID, item.getId());
        contentValues.put(TMDBProviderContract.TMDBContent.TITLE, item.getTitle());
        contentValues.put(TMDBProviderContract.TMDBContent.OVERVIEW, item.getOverview());
        contentValues.put(TMDBProviderContract.TMDBContent.RELEASE_DATE, item.getReleaseDate().toString());
        contentValues.put(TMDBProviderContract.TMDBContent.URL,item.getItemURL());
        Uri contentInsert = mContext.getContentResolver().insert(
                Uri.withAppendedPath(TMDBProviderContract.AUTHORITY_URI,"content"),contentValues);
//        Uri uri = Uri.withAppendedPath(TMDBProviderContract.AUTHORITY_URI,"content");
//        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(uri).withValues(contentValues);
//        ops.add(builder.build());


//        ContentValues favoriteValues = new ContentValues();
//        favoriteValues.put(TMDBProviderContract.TMDBContent.ID,item.getId());
////        Uri favoriteInsert = mContext.getContentResolver().insert(
////                Uri.withAppendedPath(TMDBProviderContract.AUTHORITY_URI,"favorite"),favoriteValues);
//        ops.add(ContentProviderOperation.newInsert(
//                Uri.withAppendedPath(TMDBProviderContract.AUTHORITY_URI,"favorite"))
//                .withValues(favoriteValues)
//                .build());

//        try {
//            mContext.getContentResolver().applyBatch(TMDBProviderContract.AUTHORITY_URI.getAuthority(), ops);
//        } catch (Exception e) {
//           e.printStackTrace();
//        }

    }


}
