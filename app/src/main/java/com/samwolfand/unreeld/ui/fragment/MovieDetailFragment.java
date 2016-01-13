package com.samwolfand.unreeld.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import com.samwolfand.unreeld.ui.activity.MovieDetailActivity;
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
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;
import static butterknife.ButterKnife.findById;
import static butterknife.ButterKnife.unbind;

/**
 * Created by Sam Wolfand on 1/6/16.
 */

@Module
public class MovieDetailFragment extends BaseFragment {

    public static final String KEY_MOVIE = "arg_movie";


    @Bind(R.id.movie_favorite_button) ImageButton mFavoriteButton;
    @Bind(R.id.movie_title) TextView mTitle;
    @Bind(R.id.movie_release_date) TextView mReleaseDate;
    @Bind(R.id.movie_average_rating) TextView mRating;
    @Bind(R.id.movie_overview) TextView mOverview;
    @Bind(R.id.movie_reviews_container) ViewGroup mReviewsGroup;
    @Bind(R.id.movie_videos_container) ViewGroup mVideosGroup;
    @Bind(R.id.movie_poster) AspectLockedImageView mMoviePoster;
    @Bind(R.id.movie_reviews_header) TextView mMovieReviewsHeader;
    @Bind(R.id.movie_videos_header) TextView mMovieVideosHeader;

    @Inject
    MoviesRepository mMoviesRepository;



    private Movie mMovie;
    private boolean mReviewsExist;
    private CompositeSubscription mSubscriptions;
    private MovieOrchestrator mOrchestrator;
    private Video mTrailer;

    public static MovieDetailFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_MOVIE, movie);

        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((UnreeldApplication) getActivity().getApplication()).getAppComponent().injectDetailFragment(this);
        setHasOptionsMenu(true);
        mOrchestrator = new MovieOrchestrator(getActivity(), mMoviesRepository);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        bind(this, view);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSubscriptions = new CompositeSubscription();
        bindMovie(getArguments().getParcelable(KEY_MOVIE));
        setupFavoriteSubscription();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                if (mTrailer != null) {
                    mOrchestrator.shareTrailer(R.string.share_template, mTrailer);
                }
                break;
            case android.R.id.home:
                getActivity().onBackPressed();
            default:
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_MOVIE, mMovie);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbind(this);
        ButterKnife.unbind(this);
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.movie_favorite_button)
    public void favoriteClicked() {
        if (mMovie == null) return;

        boolean favorite = !mMovie.isFavorited();
        mFavoriteButton.setSelected(favorite);
        mOrchestrator.setFavorite(mMovie, favorite);
        if (favorite)
            Toast.makeText(getContext(), "Saved to favorites.", Toast.LENGTH_SHORT).show();
    }


    // Do movie bound operations

    private void bindMovie(Movie movie) {
        mMovie = movie;

        mTitle.setText(movie.getTitle());
        mRating.setText(getString(R.string.movie_details_rating, movie.getVoteAverage()));
        mReleaseDate.setText(DateUtils.getFormattedDate(movie.getReleaseDate()));
        mOverview.setText(movie.getOverview());
        mFavoriteButton.setSelected(movie.isFavorited());


        // Poster image

        Glide.with(this).load("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath())
                .centerCrop()
                .crossFade()
                .into(mMoviePoster);
        addReviewsToView(movie);
        addTrailersToView(movie);

    }

    private void addTrailersToView(Movie movie) {
        mMoviesRepository.videos(movie.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindTrailers, throwable -> Timber.e(throwable, "failed to load reviews"));
    }

    private void bindTrailers(List<Video> videos) {
        List<Video> trailerList = videos;

        final LayoutInflater inflater = LayoutInflater.from(getActivity());

        Stream.of(trailerList)
                .forEach(trailer -> {
                    if (trailer.getType().equals(Video.TYPE_TRAILER)) {
                        Timber.d("Trailer found");
                        mTrailer = trailer;
                        ((MovieDetailActivity) getActivity()).getBackdrop().setTag(trailer);
                        ((MovieDetailActivity) getActivity()).getBackdrop().setOnClickListener(view -> mOrchestrator.playVideo((Video) view.getTag()));
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
        List<Review> reviews1 = reviews;

        final LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());

        mReviewsExist = false;
        Stream.of(reviews)
                .filter(review -> !review.getAuthor().isEmpty())
                .forEach(review -> {
                    final View reviewView = inflater.inflate(R.layout.item_review_detail, mReviewsGroup, false);
                    final TextView reviewAuthor = findById(reviewView, R.id.review_author);
                    final TextView reviewContent = findById(reviewView, R.id.review_content);

                    reviewAuthor.setText(review.getAuthor());
                    reviewContent.setText(review.getContent());
                    mReviewsGroup.addView(reviewView);
                    mReviewsExist = true;
                });

        mReviewsGroup.setVisibility(mReviewsExist ? View.VISIBLE : View.GONE);
    }

    // Do video bound operations


    private void setupFavoriteSubscription() {
        mSubscriptions.add(mOrchestrator.getFavoriteObservable()
                .filter(event -> ((mMovie != null)
                        && (mMovie.getId() == event.getMovieId())))
                .subscribe(movie -> {
                    mMovie.setFavorited(movie.isFavorited());
                    mFavoriteButton.setSelected(movie.isFavorited());
                }));
    }
}
