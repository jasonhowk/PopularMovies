package com.directv.jhowk.popularmovies.service;

import android.util.Log;

import com.directv.jhowk.popularmovies.model.TMDBContentItem;

import java.util.ArrayList;

/**
 * Created by 00r5478 on 5/9/16
 * Copyright (c) 2016 DIRECTV. All rights reserved.
 */
public class FavoriteService {
    private static final String LOG_TAG = FavoriteService.class.getSimpleName();
    private static FavoriteService sFavoriteService;

    public static FavoriteService getInstance() {
        if (sFavoriteService == null) {
            sFavoriteService = new FavoriteService();
        }
        return sFavoriteService;
    }

    public FavoriteService() {
        Log.d(LOG_TAG, "FavoriteService: Creating instance.");
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


}
