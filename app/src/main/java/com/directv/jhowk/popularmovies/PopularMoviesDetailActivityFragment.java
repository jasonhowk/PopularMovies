package com.directv.jhowk.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.directv.jhowk.popularmovies.model.TMDBContentItem;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesDetailActivityFragment extends Fragment {

    public PopularMoviesDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_popular_movies_detail, container, false);

        Intent intent = getActivity().getIntent();
        TMDBContentItem contentItem = intent.getParcelableExtra(PopularMoviesActivityFragment.EXTRA_CONTENT_ITEM);

        return fragment;
    }
}
