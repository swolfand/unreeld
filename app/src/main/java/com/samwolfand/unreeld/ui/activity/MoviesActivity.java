package com.samwolfand.unreeld.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.samwolfand.unreeld.R;
import com.samwolfand.unreeld.UnreeldApplication;
import com.samwolfand.unreeld.network.api.Sort;
import com.samwolfand.unreeld.network.repository.MoviesRepository;
import com.samwolfand.unreeld.ui.adapter.spinner.SortBySpinnerAdapter;
import com.samwolfand.unreeld.ui.fragment.MovieDetailFragment;
import com.samwolfand.unreeld.ui.fragment.MoviesFragment;
import com.samwolfand.unreeld.ui.orchestration.MovieOrchestrator;
import com.samwolfand.unreeld.ui.otto.BusProvider;
import com.samwolfand.unreeld.ui.otto.FavoriteClickedEvent;
import com.samwolfand.unreeld.ui.otto.OnMovieClickedEvent;
import com.samwolfand.unreeld.util.PrefUtils;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.Module;

@Module
public class MoviesActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    public static final String MOVIES_FRAGMENT_TAG = "fragment_movies_tag";
    public static final String KEY_MODE = "key_mode";
    private static final String MODE_FAVORITES = "favorites";

    private MoviesFragment mMoviesFragment;
    SortBySpinnerAdapter mSpinnerAdapter = new SortBySpinnerAdapter();
    private String mMode;
    private boolean mTwoPane;
    private MovieOrchestrator mOrchestrator;

    @Inject MoviesRepository mMoviesRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((UnreeldApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        ButterKnife.bind(this);

        mOrchestrator = new MovieOrchestrator(this, mMoviesRepository);

        mMode = (savedInstanceState != null) ?
                savedInstanceState.getString(KEY_MODE, Sort.POPULARITY.toString())
                : PrefUtils.getBrowseMoviesMode();

        initModeSpinner();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mMoviesFragment = (MoviesFragment) getSupportFragmentManager().findFragmentByTag(MOVIES_FRAGMENT_TAG);
        if (mMoviesFragment == null) {
            loadFragment(Sort.fromString(mMode));
        }
    }



    private void loadFragment(Sort sort) {
        MoviesFragment fragment = MoviesFragment.newInstance(sort);
        mMoviesFragment = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movies_container, fragment, MOVIES_FRAGMENT_TAG)
                .commit();

    }

    private void initModeSpinner() {
        Toolbar toolbar = getToolbar();
        if (toolbar == null)
            return;

        mSpinnerAdapter.clear();
        mSpinnerAdapter.addItem(MODE_FAVORITES, getString(R.string.mode_favorites), false);
        mSpinnerAdapter.addHeader(getString(R.string.mode_sort));
        mSpinnerAdapter.addItem(Sort.POPULARITY.toString(), getString(R.string.mode_sort_popularity), false);
        mSpinnerAdapter.addItem(Sort.VOTE_COUNT.toString(), getString(R.string.mode_sort_vote_count), false);
        mSpinnerAdapter.addItem(Sort.VOTE_AVERAGE.toString(), getString(R.string.mode_sort_vote_average), false);


        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.spinner, toolbar, false);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, layoutParams);

        Spinner spinner = (Spinner) spinnerContainer.findViewById(R.id.sort_spinner);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        int position = 2;


        if (mMode.equals(Sort.FAVORITE.toString()))
            position = 0;
        else if (mMode.equals(Sort.POPULARITY.toString()))
            position = 2;
        else if (mMode.equals(Sort.VOTE_COUNT.toString()))
            position = 3;
        else if (mMode.equals(Sort.VOTE_AVERAGE.toString()))
            position = 4;


        spinner.setSelection(position);



    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        PrefUtils.setBrowseMoviesMode(this, mMode);
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mMode.equals(mSpinnerAdapter.getMode(position))) return;
        mMode = mSpinnerAdapter.getMode(position);
        mMoviesFragment = MoviesFragment.newInstance(Sort.fromString(mSpinnerAdapter.getMode(position)));
        loadFragment(Sort.fromString(mMode));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onMovieSelected(OnMovieClickedEvent event) {

        final View itemView = event.getItemView();

        if (mTwoPane) {
            MovieDetailFragment fragment = MovieDetailFragment.newInstance(event.getMovie());
            setTwoPane(fragment);

        } else {
            View innerContainer = itemView.findViewById(R.id.container_inner_item);
            Intent startIntent = new Intent(itemView.getContext(), MovieDetailActivity.class);
            startIntent.putExtra(MovieDetailActivity.EXTRA_MOVIE, event.getMovie());
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this, innerContainer, MovieDetailActivity
                            .TRANSITION_SHARED_ELEMENT);
            ActivityCompat.startActivity(this, startIntent, options.toBundle());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_MODE, mMode);

    }

    //TODO
    @Subscribe
    @SuppressWarnings("unused")
    public void onFavoriteClicked(FavoriteClickedEvent event) {
        boolean favorited = !event.getMovie().isFavorited();
        mOrchestrator.setFavorite(event.getMovie(), favorited);
        String message = favorited ? "Added " + event.getMovie().getTitle() + " to favorites." :
                "Removed " + event.getMovie().getTitle() +  " from favorites.";
        Toast.makeText(MoviesActivity.this, message, Toast.LENGTH_SHORT).show();

    }

    //TODO
    private void setTwoPane(MovieDetailFragment fragment) {

    }

}





