package com.samwolfand.unreeld.ui.otto;

import android.view.View;

import com.samwolfand.unreeld.network.entities.Movie;


/**
 * Created by wkh176 on 12/28/15.
 */
public class OnMovieClickedEvent {
    private View itemView;
    private Movie movie;

    public OnMovieClickedEvent(Movie movie, View itemView) {
        this.itemView = itemView;
        this.movie = movie;
    }

    public View getItemView() {
        return itemView;
    }

    public Movie getMovie() {
        return movie;
    }
}
