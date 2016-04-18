package com.directv.jhowk.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directv.jhowk.popularmovies.model.TMDBContentItem;
import com.directv.jhowk.popularmovies.service.TMDBService;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesDetailActivityFragment extends Fragment {
    private static final String LOG_TAG = PopularMoviesDetailActivityFragment.class.getSimpleName();

    public PopularMoviesDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_popular_movies_detail, container, false);

        Intent intent = getActivity().getIntent();
        TMDBContentItem contentItem = intent.getParcelableExtra(PopularMoviesActivityFragment.EXTRA_CONTENT_ITEM);

        // Backdrop
        String backdropURL = String.format("%s%s", TMDBService.getBackdropBaseURL(),contentItem.getBackdropPath());
        ImageView backdropImageView = (ImageView)fragment.findViewById(R.id.backdropImageView);
        // This little routine is to dynamically resize the backdrop to the standard 1.777:1 (i.e. 16:9) ratio.
        // Not sure if it's the best way, but was a partial solution found on SO.
        fragment.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = getView().getMeasuredWidth();
                Log.d(LOG_TAG, "onGlobalLayout: WIDTH:" + width);
                (fragment.findViewById(R.id.backdropLayout)).setLayoutParams(new LinearLayout.LayoutParams(width, (int)(width / 1.777)));
                fragment.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        Picasso.with(getContext()).load(backdropURL).into(backdropImageView);

        // Poster
        String imageURL = String.format("%s%s", TMDBService.getImagesBaseURL(),contentItem.getPosterPath());
        ImageView posterImageView = (ImageView)fragment.findViewById(R.id.posterImageView);
        Picasso.with(getContext()).load(imageURL).into(posterImageView);

        // TITLE
        TextView titleTextView = (TextView)fragment.findViewById(R.id.titleTextView);
        titleTextView.setText(contentItem.getTitle());

        // Year
        TextView yearTextView = (TextView)fragment.findViewById(R.id.yearTextView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.US);
        yearTextView.setText(simpleDateFormat.format(contentItem.getReleaseDate()));

        // Runtime
        //TextView runtimeTextView = (TextView)fragment.findViewById(R.id.runtimeTextView);

        // Rating
        TextView ratingTextView = (TextView)fragment.findViewById(R.id.popularityTextView);
        String rating = String.format("%s/10",contentItem.getVoteAverage());
        ratingTextView.setText(rating);

        // Overview
        TextView overviewTextView = (TextView)fragment.findViewById(R.id.overviewTextView);
        overviewTextView.setText(contentItem.getOverview());

        ImageView favoriteImageView = (ImageView)fragment.findViewById(R.id.favoriteImageView);
        favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Favorite clicked.",Toast.LENGTH_SHORT).show();
            }
        });

        return fragment;
    }
}
