package com.samwolfand.unreeld.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.samwolfand.unreeld.R;
import com.samwolfand.unreeld.UnreeldApplication;

import dagger.Module;

@Module
public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "movie_extra";
    public static final String TRANSITION_SHARED_ELEMENT = "transition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ((UnreeldApplication) getApplication()).getAppComponent().inject(this);
    }
}
