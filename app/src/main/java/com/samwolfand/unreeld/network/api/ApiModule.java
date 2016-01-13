package com.samwolfand.unreeld.network.api;

import com.google.gson.Gson;
import com.samwolfand.unreeld.BuildConfig;
import com.squareup.okhttp.OkHttpClient;


import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module()

public final class ApiModule {
    public static final String MOVIE_DB_API_URL = "http://api.themoviedb.org/3";

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint(MOVIE_DB_API_URL);
    }

    @Provides
    @Singleton
    @Named("Api")
    @SuppressWarnings("unused")
    OkHttpClient provideApiClient(OkHttpClient client) {
        return client.clone();
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    RestAdapter provideRestAdapter(Endpoint endpoint, @Named("Api") OkHttpClient client, Gson gson) {
        return new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setEndpoint(endpoint)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setRequestInterceptor(request -> request.addQueryParam("api_key", BuildConfig.MOVIE_DB_API_KEY))
                .setConverter(new GsonConverter(gson))
                .build();
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    MoviesApi provideMoviesApi(RestAdapter restAdapter) {
        return restAdapter.create(MoviesApi.class);
    }
}
