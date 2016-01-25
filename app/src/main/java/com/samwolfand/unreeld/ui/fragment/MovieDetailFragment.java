package com.samwolfand.unreeld.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.samwolfand.unreeld.R;
import com.samwolfand.unreeld.UnreeldApplication;
import com.samwolfand.unreeld.network.entities.Movie;
import com.samwolfand.unreeld.network.entities.Review;
import com.samwolfand.unreeld.network.entities.Video;
import com.samwolfand.unreeld.network.repository.MoviesRepository;
import com.samwolfand.unreeld.ui.orchestration.MovieOrchestrator;
import com.samwolfand.unreeld.ui.widget.AspectLockedImageView;
import com.samwolfand.unreeld.util.DateUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Module;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static butterknife.ButterKnife.findById;

@Module
public class MovieDetailFragment extends Fragment {

    private static final String KEY_MOVIE = "key_movie";

    @Bind(R.id.backdrop) ImageView mBackdrop;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.appbar) AppBarLayout mAppbar;
    @Bind(R.id.movie_poster) AspectLockedImageView mMoviePoster;
    @Bind(R.id.movie_favorite_button) ImageButton mMovieFavoriteButton;
    @Bind(R.id.movie_title) TextView mMovieTitle;
    @Bind(R.id.movie_release_date) TextView mMovieReleaseDate;
    @Bind(R.id.movie_average_rating) TextView mMovieAverageRating;
    @Bind(R.id.movie_overview) TextView mMovieOverview;
    @Bind(R.id.movie_reviews_header) TextView mMovieReviewsHeader;
    @Bind(R.id.movie_reviews_container) LinearLayout mReviewsGroup;
    @Bind(R.id.movie_videos_header) TextView mMovieVideosHeader;
    @Bind(R.id.movie_videos_container) LinearLayout mVideosGroup;

    @Inject MoviesRepository mMoviesRepository;

    private Movie mMovie;
    private MovieOrchestrator mOrchestrator;
    private boolean mReviewsExist;
    private Video mTrailer;
    private boolean mTwoPane;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    public static MovieDetailFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_MOVIE, movie);

        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((UnreeldApplication) getActivity().getApplication()).getAppComponent().injectDetailFragment(this);
        mOrchestrator = new MovieOrchestrator(getActivity(), mMoviesRepository);
        mMovie = getArguments().getParcelable(KEY_MOVIE);
        if (mMovie != null) {
            initCollapsingTitleBehavior(mMovie.getTitle());
            bindMovie(mMovie);

        }


    }

    private void bindMovie(Movie movie) {
        mMovieTitle.setText(movie.getTitle());
        mMovieAverageRating.setText(getString(R.string.movie_details_rating, movie.getVoteAverage()));
        mMovieReleaseDate.setText(DateUtils.getFormattedDate(movie.getReleaseDate()));
        mMovieOverview.setText(movie.getOverview());
        mMovieFavoriteButton.setSelected(movie.isFavorited());

        setBackdrop(movie);

        Glide.with(this).load("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath())
                .placeholder(R.color.movie_poster_placeholder)
                .centerCrop()
                .crossFade()
                .into(mMoviePoster);

        addReviewsToView(movie);
        addTrailersToView(movie);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.menu_share:
                mOrchestrator.shareTrailer(R.string.share_template, mTrailer);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setBackdrop(Movie movie) {
        Glide.with(this).load("http://image.tmdb.org/t/p/w342/" + movie.getBackdropPath())
                .placeholder(R.color.movie_cover_placeholder)
                .centerCrop()
                .crossFade()
                .into(mBackdrop);
    }

    private void initCollapsingTitleBehavior(String title) {
        mTwoPane = getResources().getBoolean(R.bool.two_pane);
        if (mToolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        }
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (mTwoPane) {
                mToolbar.setVisibility(View.GONE);

            } else {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            assert actionBar != null;
            actionBar.setTitle("");
        }
        mAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;
            boolean isShowing = false;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {

                    mCollapsingToolbar.setTitle(title);
                    isShowing = true;
                } else if (isShowing) {
                    mCollapsingToolbar.setTitle("");
                    isShowing = false;
                }
            }
        });
    }


    private void addTrailersToView(Movie movie) {
        mMoviesRepository.videos(movie.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindTrailers, throwable -> Timber.e(throwable, "failed to load reviews"));
    }


    private void bindTrailers(List<Video> videos) {

        final LayoutInflater inflater = LayoutInflater.from(getActivity());

        Stream.of(videos)
                .forEach(trailer -> {
                    if (trailer.getType().equals(Video.TYPE_TRAILER)) {
                        Timber.d("Trailer found");
                        mTrailer = trailer;
                        mBackdrop.setTag(trailer);
                        mBackdrop.setOnClickListener(view -> mOrchestrator.playVideo((Video) view.getTag()));
                        getActivity().findViewById(R.id.movie_poster_play).setVisibility(View.VISIBLE);
                    }
                });

        Stream.of(videos)
                .forEach(video -> {
                    final View videoView = inflater.inflate(R.layout.item_trailer, mVideosGroup, false);
                    final TextView videoNameView = findById(videoView, R.id.video_name);

                    videoNameView.setText(video.getSite() + ": " + video.getName());
                    videoView.setTag(video);
                    videoView.setOnClickListener(view -> mOrchestrator.playVideo((Video) view.getTag()));

                    mVideosGroup.addView(videoView);
                });

        mVideosGroup.setVisibility(View.VISIBLE);
    }


    //Do review bound operations

    private void addReviewsToView(Movie movie) {
        mMoviesRepository.reviews(movie.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindReviews);
    }

    public void bindReviews(@NonNull List<Review> reviews) {

        final LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());

        mReviewsExist = false;
        Stream.of(reviews)
                .filter(review -> !review.getAuthor().isEmpty())
                .forEach(review -> {
                    final View reviewView = inflater.inflate(R.layout.item_review_detail, mReviewsGroup, false);
                    final TextView reviewAuthor = findById(reviewView, R.id.review_author);
                    final TextView reviewContent = findById(reviewView, R.id.review_content);

                    reviewAuthor.setText(review.getAuthor());
                    reviewAuthor.setTypeface(Typeface.DEFAULT_BOLD);
                    reviewContent.setText(review.getContent());
                    mReviewsGroup.addView(reviewView);
                    mReviewsExist = true;
                });

        mReviewsGroup.setVisibility(mReviewsExist ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.movie_favorite_button)
    @SuppressWarnings("unused")
    public void setMovieFavorited() {
        if (mMovie == null) return;

        boolean favorited = !mMovie.isFavorited();
        mMovieFavoriteButton.setSelected(favorited);
        mOrchestrator.setFavorite(mMovie, favorited);
        if (favorited) {
            Toast.makeText(getContext(), "Saved to favorites.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Removed from favorites.", Toast.LENGTH_SHORT).show();
        }
    }
}
