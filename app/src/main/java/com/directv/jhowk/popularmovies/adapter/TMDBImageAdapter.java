package com.directv.jhowk.popularmovies.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.directv.jhowk.popularmovies.R;
import com.directv.jhowk.popularmovies.model.PopularMovie;
import com.directv.jhowk.popularmovies.service.TMDBService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by 00r5478 on 4/8/16
 * Copyright (c) 2016 DIRECTV. All rights reserved.
 */
public class TMDBImageAdapter extends BaseAdapter {
    private static final String LOG_TAG = TMDBImageAdapter.class.getSimpleName();

    private final Context mContext;
    private final ArrayList<PopularMovie> mMovies;
    private final Picasso mPicasso;

    public TMDBImageAdapter(Context c, ArrayList<PopularMovie> movies) {
        mContext = c;
        mMovies = movies;
        mPicasso = Picasso.with(mContext);
        //mPicasso.setIndicatorsEnabled(true);
        //mPicasso.setLoggingEnabled(true);
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public PopularMovie getItem(int position) {
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        View posterGridItem;

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            //Log.d(LOG_TAG, "getView: Creating view.");
            posterGridItem = inflater.inflate(R.layout.poster_grid_item,parent,false);
        } else {
            posterGridItem = convertView;
        }

        // Image view.
        imageView = (ImageView)posterGridItem.findViewById(R.id.posterImageView);
        imageView.setBackgroundColor(Color.GRAY);
        try {
            String imageURL = String.format("%s%s", TMDBService.getImagesBaseURL(),getItem(position).getPosterPath());
            //Log.d(LOG_TAG, "getView: Image URL:" + imageURL);
            mPicasso.load(imageURL).into(imageView);
        } catch (Exception e) {
            Log.e(LOG_TAG, "getView: Error loading image.",e);
        }

        return posterGridItem;
    }
}
