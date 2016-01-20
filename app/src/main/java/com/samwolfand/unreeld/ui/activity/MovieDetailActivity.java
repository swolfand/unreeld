package com.samwolfand.unreeld.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;

import com.samwolfand.unreeld.R;
import com.samwolfand.unreeld.UnreeldApplication;
import com.samwolfand.unreeld.network.entities.Movie;
import com.samwolfand.unreeld.ui.fragment.MovieDetailFragment;

import dagger.Module;

@Module
public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "extra_movie";
    public static final String TRANSITION_SHARED_ELEMENT = "title";

//    public static final String KEY_MOVIE = "com.samwolfand.unreeld.parcels.KEY_MOVIE";

    private static final String FRAGMENT_TAG = "fragment_movie";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ((UnreeldApplication) getApplication()).getAppComponent().inject(this);

        Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);

        ViewCompat.setTransitionName();
        if (savedInstanceState == null) {
            MovieDetailFragment detailFragment = MovieDetailFragment.newInstance(movie);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, detailFragment, FRAGMENT_TAG)
                    .commit();
        }
    }
}
