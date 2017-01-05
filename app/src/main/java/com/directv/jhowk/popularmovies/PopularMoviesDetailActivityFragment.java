package com.directv.jhowk.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.directv.jhowk.popularmovies.loader.TMDBMovieLoader;
import com.directv.jhowk.popularmovies.model.TMDBContentItem;
import com.directv.jhowk.popularmovies.service.FavoriteService;
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
    private FavoriteService mFavoriteService;

    public PopularMoviesDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View detailView = inflater.inflate(R.layout.fragment_popular_movies_detail, container, false);

        mFavoriteService = FavoriteService.getInstance(getActivity().getApplicationContext());

        Intent intent = getActivity().getIntent();
        mContentItem = intent.getParcelableExtra(PopularMoviesActivityFragment.EXTRA_CONTENT_ITEM);

        mScrollView = (ScrollView) detailView.findViewById(R.id.detail_scrollview);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(this);
        mCardView = (CardView) detailView.findViewById(R.id.card_view);

        // Setup Loader
        getLoaderManager().initLoader(TMDB_MOVIE_LOADER_ID, null, this);

        // Configure initial details.
        configureView(detailView);

        // Trailer Button.  Initially hidden, and exposed if trailers are available.
        ImageButton trailerImageButton = (ImageButton) detailView.findViewById(R.id.contentTrailerButton);
        trailerImageButton.setVisibility(View.GONE);
        trailerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Trailer clicked.", Toast.LENGTH_SHORT).show();
            }
        });

        // Share Button
        ImageButton shareImageButton = (ImageButton) detailView.findViewById(R.id.contentShareButton);
        shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "TMDB Movie link for " + mContentItem.getTitle() + ".");
                sendIntent.putExtra(Intent.EXTRA_TEXT, mContentItem.getItemURL());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.shareTitle, mContentItem.getTitle())));
            }
        });

        // Favorite Button.
        ImageButton favoriteImageButton = (ImageButton) detailView.findViewById(R.id.contentFavoriteButton);
        // Check to see if favorite.
        if (mFavoriteService.isFavorite(mContentItem.getId())) {
            favoriteImageButton.setImageResource(R.drawable.ic_favorite_black_24dp);
            favoriteImageButton.setColorFilter(Color.RED);
        }

        favoriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton favoriteImageButton = (ImageButton) detailView.findViewById(R.id.contentFavoriteButton);
                if (mFavoriteService.isFavorite(mContentItem.getId())) {
                    mFavoriteService.removeFavorite(mContentItem);
                    favoriteImageButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    favoriteImageButton.setColorFilter(Color.RED);
                } else {
                    mFavoriteService.addFavorite(mContentItem);
                    favoriteImageButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                    favoriteImageButton.setColorFilter(Color.RED);
                }
            }
        });

        return detailView;
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
            int na = (((int) ap < 0 ? 0 : (int) ap) << 24);
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

    private void configureView(final View detailView) {
        // Poster
        String imageURL = String.format("%s%s", TMDBService.getImagesBaseURL(), mContentItem.getPosterPath());
        ImageView posterImageView = (ImageView) detailView.findViewById(R.id.posterImageView);
        Picasso.with(getActivity().getApplicationContext()).load(imageURL).into(posterImageView);

        // Backdrop
        String backdropURL = String.format("%s%s", TMDBService.getBackdropBaseURL(), mContentItem.getBackdropPath());
        final ImageView backdropImageView = (ImageView) detailView.findViewById(R.id.backdropImageView);
        // This little routine is to dynamically resize the backdrop to an appropriate size depending on orientation.
        // PORTRAIT: Resize to the standard 1.777:1 (i.e. 16:9) and set card offset to 75%.
        // LANDSCAPE: Resize to 65% of the current views height, and set card offset to 50%.
        // The onGlobalLayout() was a tip from SO.
        detailView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // Remove listener as we only need this to happen once.
                detailView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

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
                (detailView.findViewById(R.id.backdropImageView)).setLayoutParams(new RelativeLayout.LayoutParams(width, backdropImageHeight));

                configureToolbar();
            }
        });

        if (mContentItem.getBackdropPath() != null) {
            Log.d(LOG_TAG, "configureView: BACKDROP:" + mContentItem.getBackdropPath());
            Picasso.with(getActivity().getApplicationContext()).load(backdropURL).into(backdropImageView);
        } else {
            Log.d(LOG_TAG, "configureView: NO BACKDROP.  Using Poster");
            Picasso.with(getActivity().getApplicationContext()).load(imageURL).into(backdropImageView);
        }


        // Title
        TextView titleTextView = (TextView) detailView.findViewById(R.id.titleTextView);
        titleTextView.setText(mContentItem.getTitle());

        // Subhead
        TextView yearTextView = (TextView) detailView.findViewById(R.id.subheadView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.US);
        String subHeadFormat;
        if (mContentItem.getRuntime() > 0) {
            subHeadFormat = String.format(Locale.getDefault(),"%s %d minutes", simpleDateFormat.format(mContentItem.getReleaseDate()), mContentItem.getRuntime());
        } else {
            subHeadFormat = String.format(Locale.getDefault(),"%s", simpleDateFormat.format(mContentItem.getReleaseDate()));
        }
        yearTextView.setText(subHeadFormat);

        // Rating
        RatingBar ratingBar = (RatingBar) detailView.findViewById(R.id.ratingBar);
        Float rating = mContentItem.getVoteAverage();
        Log.d(LOG_TAG, "onCreateView: RATING:" + rating);
        ratingBar.setRating(rating);

        // Votes
        TextView votesTextView = (TextView) detailView.findViewById(R.id.votesTextView);
        String votes = String.format("%s ", mContentItem.getVoteCount());
        Log.d(LOG_TAG, "onCreateView: VOTES:" + votes);
        Drawable person = getResources().getDrawable(R.drawable.ic_person_black_24px);
        person.setColorFilter(Color.argb(255, 128, 128, 128), PorterDuff.Mode.SRC_ATOP);
        // Bounds were needed on this drawable.  An idea that came from SO.  If the bounds
        // aren't specified the drawable won't render correctly.
        person.setBounds(0, 0, 35, 35);
        ImageSpan is = new ImageSpan(person, ImageSpan.ALIGN_BASELINE);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(votes);
        spannableStringBuilder.setSpan(is, votes.length() - 1, votes.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        votesTextView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);

        // Overview
        TextView overviewTextView = (TextView) detailView.findViewById(R.id.overviewTextView);
        overviewTextView.setText(mContentItem.getOverview());
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
        this.mContentItem = data;

        //reconfigure view with updated details.
        configureView(this.getView());

        if (mContentItem.getContentTrailers() != null && mContentItem.getContentTrailers().size() > 0) {
            Log.d(LOG_TAG, "onLoadFinished: TRAILERS:" + mContentItem.getContentTrailers());
            final ImageButton trailerImageButton = (ImageButton) this.getView().findViewById(R.id.contentTrailerButton);
            trailerImageButton.setVisibility(View.VISIBLE);
        }
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
