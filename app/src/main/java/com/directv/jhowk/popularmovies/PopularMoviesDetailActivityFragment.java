package com.directv.jhowk.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.directv.jhowk.popularmovies.loader.TMDBMovieLoader;
import com.directv.jhowk.popularmovies.model.TMDBContentItem;
import com.directv.jhowk.popularmovies.service.TMDBService;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<TMDBContentItem>, ViewTreeObserver.OnScrollChangedListener {
    private static final String LOG_TAG = PopularMoviesDetailActivityFragment.class.getSimpleName();
    private static final int TMDB_MOVIE_LOADER_ID = 2;
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    private static final String BUNDLE_CONTENT_ITEM = "bundle_content_item";

    private TMDBContentItem mContentItem;
    private ScrollView mScrollView;
    private Toolbar mToolbar;
    private CardView mCardView;
    private int mCardTop;

    public PopularMoviesDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_popular_movies_detail, container, false);

        Intent intent = getActivity().getIntent();
        mContentItem = intent.getParcelableExtra(PopularMoviesActivityFragment.EXTRA_CONTENT_ITEM);

        mScrollView = (ScrollView) fragment.findViewById(R.id.detail_scrollview);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(this);
        mCardView = (CardView) fragment.findViewById(R.id.card_view);

        // Setup Loader
        getLoaderManager().initLoader(TMDB_MOVIE_LOADER_ID, null, this);

        // Backdrop
        String backdropURL = String.format("%s%s", TMDBService.getBackdropBaseURL(), mContentItem.getBackdropPath());
        final ImageView backdropImageView = (ImageView) fragment.findViewById(R.id.backdropImageView);
        // This little routine is to dynamically resize the backdrop to an appropriate size depending on orientation.
        // PORTRAIT: Resize to the standard 1.777:1 (i.e. 16:9) and set card offset to 75%.
        // LANDSCAPE: Resize to 65% of the current views height, and set card offset to 50%.
        // The onGlobalLayout() was a tip from SO.
        fragment.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // Remove listener as we only need this to happen once.
                fragment.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int width = getView().getMeasuredWidth();
                Log.d(LOG_TAG, "onGlobalLayout: WIDTH:" + width);
                int backdropImageHeight;
                LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams) mCardView.getLayoutParams();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    backdropImageHeight = (int) (width / 1.777);
                    mCardTop = (int) (backdropImageHeight * .75);

                } else {
                    backdropImageHeight = (int) (getView().getMeasuredHeight() * .65);
                    mCardTop = (int) (backdropImageHeight * .50);
                }
                llParams.setMargins(30, mCardTop, 30, 0);
                (fragment.findViewById(R.id.backdropImageView)).setLayoutParams(new RelativeLayout.LayoutParams(width, backdropImageHeight));

                configureToolbar();
            }
        });
        Picasso.with(getActivity().getApplicationContext()).load(backdropURL).into(backdropImageView);

        // Poster
        String imageURL = String.format("%s%s", TMDBService.getImagesBaseURL(), mContentItem.getPosterPath());
        ImageView posterImageView = (ImageView) fragment.findViewById(R.id.posterImageView);
        Picasso.with(getActivity().getApplicationContext()).load(imageURL).into(posterImageView);

        // TITLE
        TextView titleTextView = (TextView) fragment.findViewById(R.id.titleTextView);
        titleTextView.setText(mContentItem.getTitle());

        // Year
        TextView yearTextView = (TextView) fragment.findViewById(R.id.yearTextView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.US);
        yearTextView.setText(simpleDateFormat.format(mContentItem.getReleaseDate()));

        // Runtime
        //TextView runtimeTextView = (TextView)fragment.findViewById(R.id.runtimeTextView);

        // Rating
        TextView ratingTextView = (TextView) fragment.findViewById(R.id.popularityTextView);
        String rating = String.format("%s/10", mContentItem.getVoteAverage());
        ratingTextView.setText(rating);

        // Overview
        TextView overviewTextView = (TextView) fragment.findViewById(R.id.overviewTextView);
        overviewTextView.setText(mContentItem.getOverview());

        // Favorite.
        ImageView favoriteImageView = (ImageView) fragment.findViewById(R.id.favoriteImageView);
        favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Favorite clicked.", Toast.LENGTH_SHORT).show();
            }
        });

        // Trailer
        ImageView backdropPlayImageView = (ImageView) fragment.findViewById(R.id.backdropPlayButton);
        backdropPlayImageView.setVisibility(View.GONE);
        backdropPlayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Backdrop play button clicked.", Toast.LENGTH_SHORT).show();
            }
        });

        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "onActivityCreated: ");
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Instance Methods
    ///////////////////////////////////////////////////////////////////////////

    private void configureToolbar() {
        int backgroundColor = getResources().getColor(R.color.colorPrimary);
        int textColor = getResources().getColor(R.color.toolbarText);
        // NOTE: we use the cardTop instead of getTop() as we actually set it
        // in the global layout.
        int threshold = mCardTop - (mToolbar.getBottom() - mToolbar.getTop());
        int pos = mScrollView.getScrollY();
        // This actually steps the alpha value of the bar color depending on the
        // position of the parent scrollview.
        if (pos >= threshold && threshold > 0) {
            mToolbar.setTitle(mContentItem.getTitle());
            mToolbar.setTitleTextColor(textColor);
            mToolbar.setBackgroundColor(backgroundColor);
        } else if (pos >= (threshold - 128) && threshold > 0) {
            // Get the color only (remove any existing alpha.)
            int bpc = backgroundColor & 0x00FFFFFF;
            int tpc = textColor & 0x00FFFFFF;
            // New alpha.
            float ap = (pos - (threshold - 128)) * 2;
            int na =  (((int) ap < 0 ? 0 : (int) ap) << 24);
            // Combine color with new alpha.
            int nbc = na + bpc;
            int ntc = na + tpc;
            mToolbar.setBackgroundColor(nbc);
            mToolbar.setTitle(mContentItem.getTitle());
            mToolbar.setTitleTextColor(ntc);
        } else {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
            mToolbar.setTitle(null);
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // LoaderManager.LoaderCallbacks
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public Loader<TMDBContentItem> onCreateLoader(int id, Bundle args) {
        TMDBMovieLoader tmdbMovieLoader = null;
        try {
            tmdbMovieLoader = new TMDBMovieLoader(getActivity().getApplicationContext(), mContentItem.getId());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity().getApplicationContext(), "Error locating movie.  Please try again.", Toast.LENGTH_SHORT).show();
        }
        return tmdbMovieLoader;
    }

    @Override
    public void onLoadFinished(Loader<TMDBContentItem> loader, TMDBContentItem data) {
        Log.d(LOG_TAG, "onLoadFinished: DATA:" + data.toString());
    }

    @Override
    public void onLoaderReset(Loader<TMDBContentItem> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
    }


    ///////////////////////////////////////////////////////////////////////////
    // ViewTreeObserver.OnScrollChangedListener
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onScrollChanged() {
        configureToolbar();
    }

}
