package com.samwolfand.unreeld.network.repository;

import com.samwolfand.unreeld.network.api.Sort;
import com.samwolfand.unreeld.network.entities.Movie;
import com.samwolfand.unreeld.network.entities.Review;
import com.samwolfand.unreeld.network.entities.Video;

import java.util.List;
import java.util.Set;

import dagger.Module;
import rx.Observable;


public interface MoviesRepository {
    Observable<List<Movie>> discoverMovies(Sort sort, int page);

    Observable<List<Movie>> savedMovies();

    Observable<Set<Long>> savedMovieIds();

    void saveMovie(Movie movie);

    void deleteMovie(Movie movie);

    Observable<List<Video>> videos(long movieId);

    Observable<List<Review>> reviews(long movieId);

}
