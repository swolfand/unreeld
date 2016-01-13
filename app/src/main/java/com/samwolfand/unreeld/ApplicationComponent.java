package com.samwolfand.unreeld;

import com.samwolfand.unreeld.network.GlideSetup;
import com.samwolfand.unreeld.ui.activity.MovieDetailActivity;
import com.samwolfand.unreeld.ui.activity.MoviesActivity;
import com.samwolfand.unreeld.ui.fragment.BaseFragment;
import com.samwolfand.unreeld.ui.fragment.MovieDetailFragment;
import com.samwolfand.unreeld.ui.fragment.MoviesFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by wkh176 on 1/4/16.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(MoviesActivity activity);

    void inject(GlideSetup glideSetup);

    void inject(MoviesFragment moviesFragment);

    void inject(BaseFragment baseFragment);

    void inject(MovieDetailActivity detailActivity);

    void injectDetailFragment(MovieDetailFragment detailFragment);
}
