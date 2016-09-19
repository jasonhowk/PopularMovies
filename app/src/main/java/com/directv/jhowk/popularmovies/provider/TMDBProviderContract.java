package com.directv.jhowk.popularmovies.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by 00r5478 on 6/22/16
 * Copyright (c) 2016 DIRECTV. All rights reserved.
 */
public final class TMDBProviderContract {
    /** The authority for the contacts provider */
    public static final String AUTHORITY = "com.directv.jhowk.TMDBProvider";
    /** A content:// style uri to the authority for the contacts provider */
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static final class TMDBContent implements BaseColumns{
        public static final String TABLE_NAME = "tmdb_content";
        //public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + TMDBProviderContract.AUTHORITY + "." + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + TMDBProviderContract.AUTHORITY + "." + TABLE_NAME;

        public static final String ID = "_ID";
        public static final String TITLE = "TITLE";
        public static final String OVERVIEW = "OVERVIEW";
        public static final String RELEASE_DATE = "RELEASE_DATE";
        public static final String BACKDROP_PATH = "BACKDROP_PATH";
        public static final String POSTER_PATH = "POSTER_PATH";
        public static final String VOTE_AVERAGE = "VOTE_AVERAGE";
        public static final String VOTE_COUNT = "VOTE_COUNT";
        public static final String URL = "URL";
    }

    public static final class TMDBFavorite implements BaseColumns{
        public static final String TABLE_NAME = "tmdb_favorite";
        //public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + TMDBProviderContract.AUTHORITY + "." + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + TMDBProviderContract.AUTHORITY + "." + TABLE_NAME;

        public static final String ID = "_ID";
    }

}
