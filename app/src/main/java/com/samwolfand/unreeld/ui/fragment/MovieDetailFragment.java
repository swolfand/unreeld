package com.samwolfand.unreeld.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.samwolfand.unreeld.R;
import com.samwolfand.unreeld.network.entities.Movie;
import com.samwolfand.unreeld.ui.widget.AspectLockedImageView;
import com.samwolfand.unreeld.util.DateUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.Module;

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

    private Movie mMovie;

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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
        mMovieFavoriteButton.setSelected(movie.isFavorited());

        setBackdrop(movie);

        Glide.with(this).load("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath())
                .placeholder(R.color.movie_poster_placeholder)
                .centerCrop()
                .crossFade()
                .into(mMoviePoster);
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
        if (mToolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        }
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
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
}
