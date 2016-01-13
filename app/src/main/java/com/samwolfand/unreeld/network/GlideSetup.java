package com.samwolfand.unreeld.network;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.module.GlideModule;
import com.samwolfand.unreeld.UnreeldApplication;
import com.squareup.okhttp.OkHttpClient;

import java.io.InputStream;

import javax.inject.Inject;

import dagger.Module;

/**
 * Created by wkh176 on 1/4/16.
 */

@Module
public final class GlideSetup implements GlideModule {

    public static final String BASE_URL = "http://image.tmdb.org/t/p";

    @Inject
    OkHttpClient mOkHttpClient;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        UnreeldApplication.get(context).getAppComponent().inject(this);

        glide.register(String.class, InputStream.class, new ImageLoader.Factory());
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(mOkHttpClient));

    }

    private static class ImageLoader extends BaseGlideUrlLoader<String> {


        public ImageLoader(Context context) {
            super(context);
        }

        @Override
        protected String getUrl(String model, int width, int height) {
            return buildPosterUrl(model, width);
        }

        public static class Factory implements ModelLoaderFactory<String, InputStream> {
            @Override
            public ModelLoader<String, InputStream> build(Context context, GenericLoaderFactory factories) {
                return new ImageLoader(context);
            }

            @Override
            public void teardown() {
            }
        }

    }

    public static String buildPosterUrl(String imagePath, int width) {
        String widthPath;

        if (width <= 92)
            widthPath = "/w92";
        else if (width <= 154)
            widthPath = "/w154";
        else if (width <= 185)
            widthPath = "/w185";
        else if (width <= 342)
            widthPath = "/w342";
        else if (width <= 500)
            widthPath = "/w500";
        else
            widthPath = "/w780";

        return BASE_URL + widthPath + imagePath;
    }

}
