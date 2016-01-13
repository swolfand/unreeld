package com.samwolfand.unreeld.network.repository;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;

import com.samwolfand.unreeld.network.api.MoviesApi;
import com.samwolfand.unreeld.network.api.Sort;
import com.samwolfand.unreeld.network.entities.Movie;
import com.samwolfand.unreeld.network.entities.Review;
import com.samwolfand.unreeld.network.entities.Video;
import com.samwolfand.unreeld.network.provider.MoviesContract;
import com.squareup.sqlbrite.BriteContentResolver;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import static com.samwolfand.unreeld.network.provider.MoviesContract.*;


/**
 * Created by wkh176 on 1/4/16.
 */

public class MoviesRepositoryImpl implements MoviesRepository {
    private final MoviesApi mMoviesApi;
    private final ContentResolver mContentResolver;
    private final BriteContentResolver mBriteContentResolver;

    private BehaviorSubject<Set<Long>> mSavedMovieIdsSubject;

    public MoviesRepositoryImpl(MoviesApi mMoviesApi, ContentResolver mContentResolver, BriteContentResolver mBriteContentResolver) {
        this.mMoviesApi = mMoviesApi;
        this.mContentResolver = mContentResolver;
        this.mBriteContentResolver = mBriteContentResolver;
    }

    @Override
    public Observable<List<Movie>> discoverMovies(Sort sort, int page) {
        return mMoviesApi.discoverMovies(sort, page)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .map(response -> response.movies)
                .withLatestFrom(getSavedMovieIds(), (movies, favoredIds) -> {
                    for (Movie movie : movies)
                        movie.setFavorited(favoredIds.contains(movie.getId()));
                    return movies;
                })
                .subscribeOn(Schedulers.io());
    }


    @Override
    public Observable<List<Movie>> savedMovies() {
        return mBriteContentResolver.createQuery(Movies.CONTENT_URI, Movie.PROJECTION, null, null, Movies.DEFAULT_SORT, true)
                .map(Movie.PROJECTION_MAP)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Set<Long>> savedMovieIds() {
        return mBriteContentResolver.createQuery(Movies.CONTENT_URI, Movie.ID_PROJECTION, null, null, null, true)
                .map(Movie.ID_PROJECTION_MAP)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public void saveMovie(Movie movie) {
        AsyncQueryHandler handler = new AsyncQueryHandler(mContentResolver) {};
        handler.startInsert(-1, null, Movies.CONTENT_URI, new Movie.Builder()
                .movie(movie)
                .build());
    }

    @Override
    public void deleteMovie(Movie movie) {
        String query = Movies.MOVIE_ID + "=?";
        String[] args = new String[]{String.valueOf(movie.getId())};

        AsyncQueryHandler handler = new AsyncQueryHandler(mContentResolver) {};
        handler.startDelete(-1, null, Movies.CONTENT_URI, query, args);
    }

    @Override
    public Observable<List<Video>> videos(long movieId) {
        return mMoviesApi.videos(movieId)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .map(response -> response.videos);
    }

    @Override
    public Observable<List<Review>> reviews(long movieId) {
        return mMoviesApi.reviews(movieId, 1)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .map(response -> response.reviews);
    }

    private Observable<Set<Long>> getSavedMovieIds() {
        if (mSavedMovieIdsSubject == null) {
            mSavedMovieIdsSubject = BehaviorSubject.create();
            savedMovieIds().subscribe(mSavedMovieIdsSubject);
        }
        return mSavedMovieIdsSubject.asObservable();
    }
}
