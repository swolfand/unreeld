package com.samwolfand.unreeld;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.samwolfand.unreeld.util.Prefs;

import timber.log.Timber;


/**
 * Created by wkh176 on 1/4/16.
 */


public class UnreeldApplication extends Application {
    private ApplicationComponent mAppComponent;

    public static UnreeldApplication get(Context context) {
        return (UnreeldApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        mAppComponent = com.samwolfand.unreeld.DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setDefaultBooleanValue(false)
                .setDefaultIntValue(-1)
                .setDefaultStringValue("KO")
                .setUseDefaultSharedPreference(true)
                .build();
    }

    ApplicationModule getApplicationModule() {
        return new ApplicationModule(this);
    }

    public ApplicationComponent getAppComponent() {
        return mAppComponent;
    }

    void initComponent() {
        mAppComponent = DaggerApplicationComponent.builder()
                .applicationModule(getApplicationModule())
                .build();
    }
}
