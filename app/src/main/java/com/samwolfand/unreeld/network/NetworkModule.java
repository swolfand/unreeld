package com.samwolfand.unreeld.network;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.samwolfand.unreeld.network.api.ApiModule;
import com.samwolfand.unreeld.network.provider.ProviderModule;
import com.samwolfand.unreeld.network.repository.RepositoryModule;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by wkh176 on 1/4/16.
 */
@Module(includes = {ApiModule.class, ProviderModule.class, RepositoryModule.class})
public class NetworkModule {

    public NetworkModule() {
    }

    public static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; //50MB

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    Gson provideGson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    OkHttpClient provideClient(Application application) {
        return initClient(application);
    }

    static OkHttpClient initClient(Application application) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(10, TimeUnit.SECONDS);
        client.setWriteTimeout(10, TimeUnit.SECONDS);

        File cacheDir = new File(application.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        client.setCache(cache);

        return client;
    }



}
