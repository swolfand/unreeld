package com.samwolfand.unreeld.ui.module;

import com.samwolfand.unreeld.network.repository.RepositoryModule;
import com.samwolfand.unreeld.ui.activity.MovieDetailActivity;
import com.samwolfand.unreeld.ui.activity.MoviesActivity;
import com.samwolfand.unreeld.ui.fragment.BaseFragment;
import com.samwolfand.unreeld.ui.fragment.MovieDetailFragment;

import dagger.Module;

/**
 * Created by wkh176 on 1/9/16.
 */

@Module(includes = {MoviesActivity.class, RepositoryModule.class, BaseFragment.class, MovieDetailFragment.class, MovieDetailActivity.class})
public class MovieModule {
}
