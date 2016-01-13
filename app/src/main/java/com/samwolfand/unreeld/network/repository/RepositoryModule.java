package com.samwolfand.unreeld.network.repository;

import android.content.ContentResolver;

import com.samwolfand.unreeld.network.api.MoviesApi;
import com.squareup.sqlbrite.BriteContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by wkh176 on 1/4/16.
 */

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    public MoviesRepository provideMoviesRepository(MoviesApi moviesApi, ContentResolver contentResolver,
                                                    BriteContentResolver briteContentResolver) {
        return new MoviesRepositoryImpl(moviesApi, contentResolver, briteContentResolver);
    }
}
