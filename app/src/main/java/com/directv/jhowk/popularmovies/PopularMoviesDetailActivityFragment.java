package com.directv.jhowk.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.directv.jhowk.popularmovies.model.TMDBContentReview;
import com.directv.jhowk.popularmovies.model.TMDBContentTrailer;
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
    private static final String YOUTUBE_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/";
    private static final String YOUTUBE_THUMBNAIL_PATH = "/sddefault.jpg";

    private TMDBContentItem mContentItem;
    private ScrollView mScrollView;
    private Toolbar mToolbar;
    private CardView mCardView;
    private View mDetailView;
    private int mCardTop;
    private View mTrailerView;
    private View mReviewView;
    private ImageView mBackdropImageView;
    private FavoriteService mFavoriteService;
    private Picasso mPicasso;

    public PopularMoviesDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDetailView = inflater.inflate(R.layout.fragment_popular_movies_detail, container, false);

        mFavoriteService = FavoriteService.getInstance(getActivity().getApplicationContext());

        Intent intent = getActivity().getIntent();
        mContentItem = intent.getParcelableExtra(PopularMoviesActivityFragment.EXTRA_CONTENT_ITEM);

        mScrollView = (ScrollView) mDetailView.findViewById(R.id.detail_scrollview);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(this);
        mCardView = (CardView) mDetailView.findViewById(R.id.card_view);
        mBackdropImageView = (ImageView) mDetailView.findViewById(R.id.backdropImageView);

        // Picasso
        mPicasso = Picasso.with(getActivity().getApplicationContext());
        //mPicasso.setIndicatorsEnabled(true);
        //mPicasso.setLoggingEnabled(true);

        // Setup Loader
        getLoaderManager().initLoader(TMDB_MOVIE_LOADER_ID, null, this);

        // Share Button
        ImageButton shareImageButton = (ImageButton) mDetailView.findViewById(R.id.contentShareButton);
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
        ImageButton favoriteImageButton = (ImageButton) mDetailView.findViewById(R.id.contentFavoriteButton);
        // Check to see if favorite.
        if (mFavoriteService.isFavorite(mContentItem.getId())) {
            favoriteImageButton.setImageResource(R.drawable.ic_favorite_black_24dp);
            favoriteImageButton.setColorFilter(Color.RED);
        }

        favoriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton favoriteImageButton = (ImageButton) mDetailView.findViewById(R.id.contentFavoriteButton);
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

        // Trailers
        mTrailerView = mDetailView.findViewById(R.id.trailerDetailLayout);

        // Reviews
        mReviewView = mDetailView.findViewById(R.id.reviewDetailLayout);

        return mDetailView;
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
        //Log.d(LOG_TAG, "configureToolbar");
        int backgroundColor = getResources().getColor(R.color.colorPrimary);
        int textColor = getResources().getColor(R.color.toolbarText);
        // NOTE: we use the cardTop instead of getTop() as we actually set it
        // in the global layout.
        int threshold = mCardTop - (mToolbar.getBottom() - mToolbar.getTop());
        int pos = mScrollView.getScrollY();
        // Slow the movement of the backdrop offscreen when scrolling.
        mBackdropImageView.setY(pos / 2);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(LOG_TAG, "onConfigurationChanged: Configuration changed");
        //configureDetailView(this.getView());
    }

    private void configureDetailView(final View detailView) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // This little routine is to dynamically resize the backdrop to an appropriate size depending on orientation.
        // We go get the posters after to avoid any race conditions that happen with the posters.
        // PORTRAIT: Resize to the standard 1.777:1 (i.e. 16:9) and set card offset to 75%.
        // LANDSCAPE: Resize to 65% of the current views height, and set card offset to 50%.
        // The onGlobalLayout() was a tip from SO.
        detailView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = detailView.getWidth();
                Log.d(LOG_TAG, "onGlobalLayout: WIDTH:" + width);
                int backdropImageHeight;
                LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams) mCardView.getLayoutParams();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    backdropImageHeight = (int) (width / 1.777);
                    mCardTop = (int) (backdropImageHeight * .75);
                } else {
                    backdropImageHeight = (int) (detailView.getMeasuredHeight() * .65);
                    mCardTop = (int) (backdropImageHeight * .50);
                }
                (detailView.findViewById(R.id.backdropImageView)).setLayoutParams(new RelativeLayout.LayoutParams(width, backdropImageHeight));
                llParams.setMargins(30, mCardTop, 30, 0);

                configureToolbar();

                // Poster
                String imageURL = String.format("%s%s", TMDBService.getImagesBaseURL(), mContentItem.getPosterPath());
                ImageView posterImageView = (ImageView) detailView.findViewById(R.id.posterImageView);
                mPicasso.load(imageURL).into(posterImageView);

                // Backdrop
                String backdropURL = String.format("%s%s", TMDBService.getBackdropBaseURL(), mContentItem.getBackdropPath());
                mPicasso.load(backdropURL).into(mBackdropImageView);

                // Remove listener as we only need this to happen once.
                detailView.getViewTreeObserver().removeGlobalOnLayoutListener(this);


            }
        });

        // Title
        TextView titleTextView = (TextView) detailView.findViewById(R.id.titleTextView);
        titleTextView.setText(mContentItem.getTitle());

        // Subhead
        TextView yearTextView = (TextView) detailView.findViewById(R.id.subheadView);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.US);
        String subHeadFormat;
        if (mContentItem.getRuntime() > 0) {
            subHeadFormat = String.format(Locale.getDefault(), "%s %d minutes", simpleDateFormat.format(mContentItem.getReleaseDate()), mContentItem.getRuntime());
        } else {
            subHeadFormat = String.format(Locale.getDefault(), "%s", simpleDateFormat.format(mContentItem.getReleaseDate()));
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

        // Trailers.
        Log.d(LOG_TAG, "configureDetailView: TRAILERS: " + mContentItem.getContentTrailers());
        if (mContentItem.getContentTrailers().size() > 0) {
            for (TMDBContentTrailer trailer : mContentItem.getContentTrailers()) {
                if (trailer.getType().equalsIgnoreCase("trailer")) {
                    View tmpView = inflater.inflate(R.layout.content_trailer_detail_layout, null);
                    // Title
                    TextView title = (TextView) tmpView.findViewById(R.id.trailerTitleTextView);
                    title.setText(trailer.getName());
                    // Play Button
                    ImageButton trailerImageButton = (ImageButton) tmpView.findViewById(R.id.contentTrailerButton);
                    trailerImageButton.setTag(trailer.getSource());
                    trailerImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + v.getTag()));
                            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + v.getTag()));
                            try {
                                startActivity(appIntent);
                            } catch (ActivityNotFoundException anfe) {
                                // No youtube app.  Use web instead.
                                startActivity(webIntent);
                            }
                        }
                    });

                    // Thumbnail
                    ImageView thumbnail = (ImageView) tmpView.findViewById(R.id.trailerThumbnailImageView);
                    String imageURL = String.format("%s%s%s", YOUTUBE_THUMBNAIL_BASE_URL, trailer.getSource(), YOUTUBE_THUMBNAIL_PATH);
                    mPicasso.load(imageURL).into(thumbnail);

                    ((ViewGroup) mTrailerView).addView(tmpView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            }
        }

        // Reviews.
        Log.d(LOG_TAG, "configureDetailView: REVIEWS: " + mContentItem.getContentReviews());
        if (mContentItem.getContentReviews().size() > 0) {
            for (TMDBContentReview review : mContentItem.getContentReviews()) {
                View tmpView = inflater.inflate(R.layout.content_review_detail_layout, null);
                // Author
                TextView authorTextView = (TextView) tmpView.findViewById(R.id.reviewDetailAuthor);
                authorTextView.setText(getResources().getString(R.string.reviewAuthor,review.getAuthor()));
                // Review Content
                TextView reviewTextView = (TextView) tmpView.findViewById(R.id.reviewDetailContent);
                reviewTextView.setText(review.getContent());

                ((ViewGroup) mReviewView).addView(tmpView,0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        } else {
            TextView placeholder = new TextView(getActivity().getApplicationContext());
            placeholder.setText(getResources().getString(R.string.emptyReview,mContentItem.getTitle()));
            ((ViewGroup)mReviewView).addView(placeholder,0,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
        this.mContentItem = data;

        //reconfigure view with updated details.
        configureDetailView(this.getView());
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
        //Log.d(LOG_TAG, "onScrollChanged");
        configureToolbar();
    }

}
