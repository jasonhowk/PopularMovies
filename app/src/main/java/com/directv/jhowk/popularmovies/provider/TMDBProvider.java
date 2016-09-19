package com.directv.jhowk.popularmovies.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by 00r5478 on 6/22/16
 * Copyright (c) 2016 DIRECTV. All rights reserved.
 */
public class TMDBProvider extends ContentProvider {
    private static final String LOG_TAG = TMDBProvider.class.getSimpleName();
    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private DbHelper mDbHelper;

    static {
        sMatcher.addURI(TMDBProviderContract.AUTHORITY,"content",1);
        sMatcher.addURI(TMDBProviderContract.AUTHORITY,"content/#", 2);
        sMatcher.addURI(TMDBProviderContract.AUTHORITY,"favorite", 10);
        sMatcher.addURI(TMDBProviderContract.AUTHORITY,"favorite/#",11);
//        sMatcher.addURI(TMDBProviderContract.TMDBContent.CONTENT_URI.toString(),"/#",1);
//        sMatcher.addURI(TMDBProviderContract.TMDBFavorite.CONTENT_URI.toString(),"",10);
//        sMatcher.addURI(TMDBProviderContract.TMDBFavorite.CONTENT_URI.toString(),"/#",11);
        Log.d(LOG_TAG, "static initializer: MATCHER:" + sMatcher);
    }

    @Override
    public boolean onCreate() {
        this.mDbHelper = new DbHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "query: Query...");
        switch (sMatcher.match(uri)) {
            case 1:
                Log.d(LOG_TAG, "query: Getting Content item");
                // Content Item.
                break;
            case 10:
                // All favorites.
                Log.d(LOG_TAG, "query: Getting all favorites.");
                mDbHelper.getReadableDatabase().execSQL("select * from tmdb_content;");
                break;
            case 11:
                Log.d(LOG_TAG, "query: Getting specific favorite");
                // specific favorite
                break;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sMatcher.match(uri)) {
            case 1:
                // Content Item.
                return TMDBProviderContract.TMDBContent.CONTENT_TYPE;
            case 2:
                return TMDBProviderContract.TMDBContent.CONTENT_ITEM_TYPE;
            case 10:
                // All favorites.
                return TMDBProviderContract.TMDBFavorite.CONTENT_TYPE;
            case 11:
                // specific favorite
                return TMDBProviderContract.TMDBFavorite.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "insert: Inserting: " + uri + ":"  + values);
        Long id = null;
        switch (sMatcher.match(uri)) {
            case 1:
                Log.d(LOG_TAG, "insert: Inserting content.");
                id = mDbHelper.getWritableDatabase().insert(TMDBProviderContract.TMDBContent.TABLE_NAME,null,values);
            case 10:
                Log.d(LOG_TAG, "insert: Inserting favorite");
                id = mDbHelper.getWritableDatabase().insert(TMDBProviderContract.TMDBFavorite.TABLE_NAME,null,values);
        }
        if (id != null) {
            Log.d(LOG_TAG, "inserted: ID: " + id);
            return Uri.withAppendedPath(uri, id.toString());
        } else {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "delete: Deleting...");
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "update: Updating...");
        return 0;
    }

    protected static final class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context) {
            super(context, "tmdbContentDb", null,1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "onCreate: Creating DB");
            String createContentDDL = "create table tmdb_content(_ID integer primary key, " +
                    "title text, overview text, release_date integer, backdrop_path text, poster_path text, " +
                    "vote_average real, vote_count integer, url text);";
            db.execSQL(createContentDDL);
            String createFavoritesDDL = "create table tmdb_favorite(_ID integer primary key);";
            db.execSQL(createFavoritesDDL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}


