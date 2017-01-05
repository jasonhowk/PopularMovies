package com.directv.jhowk.popularmovies.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by 00r5478 on 6/22/16
 * Copyright (c) 2016 DIRECTV. All rights reserved.
 */
public class TMDBProvider extends ContentProvider {
    private static final String LOG_TAG = TMDBProvider.class.getSimpleName();

    private static final int CONTENT = 1;
    private static final int CONTENT_ID = 2;
    private static final int FAVORITE = 10;
    private static final int FAVORITE_ID = 11;
    private static final int POPULAR = 20;
    private static final int TOP_RATED = 21;
    private static final int NOW_PLAYING = 22;
    private static final int UPCOMING = 23;
    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private DbHelper mDbHelper;

    static {
        sMatcher.addURI(TMDBProviderContract.AUTHORITY,"content",CONTENT);
        sMatcher.addURI(TMDBProviderContract.AUTHORITY,"content/#", CONTENT_ID);
        sMatcher.addURI(TMDBProviderContract.AUTHORITY,"favorite", FAVORITE);
        sMatcher.addURI(TMDBProviderContract.AUTHORITY,"favorite/#",FAVORITE_ID);
        sMatcher.addURI(TMDBProviderContract.AUTHORITY,"popular",POPULAR);
        sMatcher.addURI(TMDBProviderContract.AUTHORITY,"topRated",TOP_RATED);
        sMatcher.addURI(TMDBProviderContract.AUTHORITY,"nowPlaying",NOW_PLAYING);
        sMatcher.addURI(TMDBProviderContract.AUTHORITY,"upcoming",UPCOMING);
        Log.d(LOG_TAG, "static initializer: MATCHER:" + sMatcher);
    }

    @Override
    public boolean onCreate() {
        this.mDbHelper = new DbHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "query: " + uri.toString());
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (sMatcher.match(uri)) {
            case CONTENT:
                Log.d(LOG_TAG, "query: Getting Content item");
                // Content Item.
                break;
            case FAVORITE:
                // All favorites.
                Log.d(LOG_TAG, "query: Getting all favorites.");
                queryBuilder.setTables("tmdb_favorite");
                Cursor fCursor = queryBuilder.query(mDbHelper.getReadableDatabase(),projection,selection,selectionArgs,null,null,sortOrder);
                try {
                    fCursor.setNotificationUri(getContext().getContentResolver(), uri);
                    return fCursor;
                } catch (NullPointerException npe) {
                    return null;
                }
            case FAVORITE_ID:
                Log.d(LOG_TAG, "query: Getting specific favorite");
                // specific favorite
                break;
            case POPULAR:
                break;
            case TOP_RATED:
                break;
            case NOW_PLAYING:
                break;
            case UPCOMING:
                break;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sMatcher.match(uri)) {
            case CONTENT:
                // Content Item.
                return TMDBProviderContract.TMDBContent.CONTENT_TYPE;
            case CONTENT_ID:
                return TMDBProviderContract.TMDBContent.CONTENT_ITEM_TYPE;
            case FAVORITE:
                // All favorites.
                return TMDBProviderContract.TMDBFavorite.CONTENT_TYPE;
            case FAVORITE_ID:
                // specific favorite
                return TMDBProviderContract.TMDBFavorite.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "insert: Inserting: " + uri + ":"  + values);
        Long id;
        String table;
        switch (sMatcher.match(uri)) {
            case 1:
                Log.d(LOG_TAG, "insert: Inserting content.");
                table = TMDBProviderContract.TMDBContent.TABLE_NAME;
                break;
            case 10:
                Log.d(LOG_TAG, "insert: Inserting favorite");
                table = TMDBProviderContract.TMDBFavorite.TABLE_NAME;
                break;
            default:
                return null;
        }
        id = mDbHelper.getWritableDatabase().insert(table, null, values);
        if (id >= 0) {
            Log.d(LOG_TAG, "inserted: ID: " + id);
            return Uri.withAppendedPath(uri, id.toString());
        } else {
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "delete: Deleting...");
        Log.d(LOG_TAG, "insert: deleting: " + uri );
        String table;
        switch (sMatcher.match(uri)) {
            case 1:
                Log.d(LOG_TAG, "delete: Deleting content.");
                table = TMDBProviderContract.TMDBContent.TABLE_NAME;
                break;
            case 10:
                Log.d(LOG_TAG, "delete: Deleting favorite");
                table = TMDBProviderContract.TMDBFavorite.TABLE_NAME;
                break;
            default:
                return 0;
        }
        int count = mDbHelper.getWritableDatabase().delete(table,selection,selectionArgs);
        if (count >= 0) {
            Log.d(LOG_TAG, "delete: removed " + count + " rows.");
            return count;
        } else {
            return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "update: Updating...");
        return 0;
    }

    protected static final class DbHelper extends SQLiteOpenHelper {
        DbHelper(Context context) {
            super(context, "tmdbContentDb", null,1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "onCreate: Creating DB");
            Log.d(LOG_TAG, "onCreate: creating content table.");
            String createContentDDL = "create table tmdb_content(" +
                    TMDBProviderContract.TMDBContent.ID + " integer primary key, " +
                    TMDBProviderContract.TMDBContent.TITLE +" text, " +
                    TMDBProviderContract.TMDBContent.OVERVIEW + " text, " +
                    TMDBProviderContract.TMDBContent.RELEASE_DATE + " integer, " +
                    TMDBProviderContract.TMDBContent.BACKDROP_PATH + " text, " +
                    TMDBProviderContract.TMDBContent.POSTER_PATH + " text, " +
                    TMDBProviderContract.TMDBContent.VOTE_AVERAGE + " real, " +
                    TMDBProviderContract.TMDBContent.VOTE_COUNT + " integer, " +
                    TMDBProviderContract.TMDBContent.URL + " text);";
            db.execSQL(createContentDDL);
            Log.d(LOG_TAG, "onCreate: creating favorites table.");
            String createFavoritesDDL = "create table tmdb_favorite(" +
                    TMDBProviderContract.TMDBFavorite.ID + " integer primary key, " +
                    TMDBProviderContract.TMDBFavorite.JSON + " text);";
            db.execSQL(createFavoritesDDL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(LOG_TAG, "onUpgrade: Upgrading DB");
            db.execSQL("DROP TABLE IF EXISTS tmdb_favorite");
            db.execSQL("DROP TABLE IF EXISTS tmdb_content");
            onCreate(db);
        }
    }

}


