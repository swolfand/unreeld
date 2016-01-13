package com.samwolfand.unreeld;

import android.app.Application;


import com.samwolfand.unreeld.network.NetworkModule;
import com.samwolfand.unreeld.ui.module.MovieModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Sam Wolfand on 1/4/16.
 */

@Module(includes = {NetworkModule.class, MovieModule.class})
public class ApplicationModule {
    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides @Singleton
    Application providesApplication() {
        return mApplication;
    }




}
