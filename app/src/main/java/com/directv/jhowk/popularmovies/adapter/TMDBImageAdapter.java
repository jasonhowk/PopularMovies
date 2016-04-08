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

import java.util.ArrayList;

/**
 * Created by 00r5478 on 4/8/16
 * Copyright (c) 2016 DIRECTV. All rights reserved.
 */
public class TMDBImageAdapter extends BaseAdapter {
    private static final String LOG_TAG = TMDBImageAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<PopularMovie> mMovies;

    public TMDBImageAdapter(Context c, ArrayList<PopularMovie> movies) {
        mContext = c;
        mMovies = movies;
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
            Log.d(LOG_TAG, "getView: Creating view.");
            posterGridItem = inflater.inflate(R.layout.poster_grid_item,null);

            // Image view.
            imageView = (ImageView)posterGridItem.findViewById(R.id.posterImageView);
            imageView.setBackgroundColor(Color.GRAY);
            //imageView.setImageResource(R.drawable.test_image);

        } else {
            posterGridItem = convertView;
        }

        return posterGridItem;
    }
}
