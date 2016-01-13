package com.samwolfand.unreeld.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.samwolfand.unreeld.R;
import com.samwolfand.unreeld.UnreeldApplication;
import com.samwolfand.unreeld.network.entities.Movie;
import com.samwolfand.unreeld.network.repository.MoviesRepository;
import com.samwolfand.unreeld.ui.fragment.MovieDetailFragment;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.Module;

@Module
public class MovieDetailActivity extends BaseActivity {
    public static final String EXTRA_MOVIE = "extra_movie";
    public static final String TRANSITION_SHARED_ELEMENT = "title";

//    public static final String KEY_MOVIE = "com.samwolfand.unreeld.parcels.KEY_MOVIE";

    private static final String FRAGMENT_TAG = "fragment_movie";

    @Bind(R.id.backdrop) ImageView mBackdrop;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.appbar) AppBarLayout mAppbar;
    @Bind(R.id.movie_details_container) FrameLayout mMovieDetailsContainer;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.movie_poster_play) ImageView mMoviePosterPlay;
    @Bind(R.id.container_inner_item) CoordinatorLayout mContainerInnerItem;

    @Inject MoviesRepository mMoviesRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UnreeldApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);


        ViewCompat.setTransitionName(mContainerInnerItem, TRANSITION_SHARED_ELEMENT);
        if (savedInstanceState == null) {
            MovieDetailFragment detailFragment = MovieDetailFragment.newInstance(movie);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, detailFragment, FRAGMENT_TAG)
                    .commit();
        }

        setBackdrop(movie);
        initCollapsingTitleBehavior(movie.getTitle());

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
            setSupportActionBar(mToolbar);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
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

    public ImageView getBackdrop() {
        return mBackdrop;
    }
}
