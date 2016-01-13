package com.samwolfand.unreeld.ui.otto;

import android.support.annotation.NonNull;

import com.samwolfand.unreeld.network.entities.Movie;


/**
 * Created by wkh176 on 12/14/15.
 */
public class FavoriteClickedEvent {

    @NonNull
    Movie movie;
    int position;
    long movieId;
    boolean favorited;

    public FavoriteClickedEvent(@NonNull Movie movie, int position) {
        this.movie = movie;
        this.position = position;
    }

    public FavoriteClickedEvent(long movieId, boolean favorited) {
        this.favorited = favorited;
        this.movieId = movieId;
    }

    @NonNull
    public Movie getMovie() {
        return movie;
    }

    public int getPosition() {
        return position;
    }

    public void setMovie(@NonNull Movie movie) {
        this.movie = movie;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }
}
