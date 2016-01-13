
package com.samwolfand.unreeld.network.provider.meta;

import android.content.ContentValues;
import android.database.Cursor;

import com.samwolfand.unreeld.network.entities.Movie;
import com.samwolfand.unreeld.network.provider.MoviesContract;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.functions.Func1;

public interface MovieMeta {


    int BOOLEAN_FALSE = 0;
    int BOOLEAN_TRUE = 1;

    String[] PROJECTION = {
            MoviesContract.Movies._ID,
            MoviesContract.Movies.MOVIE_ID,
            MoviesContract.Movies.MOVIE_TITLE,
            MoviesContract.Movies.MOVIE_OVERVIEW,
            MoviesContract.Movies.MOVIE_GENRE_IDS,
            MoviesContract.Movies.MOVIE_POSTER_PATH,
            MoviesContract.Movies.MOVIE_BACKDROP_PATH,
            MoviesContract.Movies.MOVIE_FAVORED,
            MoviesContract.Movies.MOVIE_POPULARITY,
            MoviesContract.Movies.MOVIE_VOTE_COUNT,
            MoviesContract.Movies.MOVIE_VOTE_AVERAGE,
            MoviesContract.Movies.MOVIE_RELEASE_DATE
    };


    Func1<SqlBrite.Query, List<Movie>> PROJECTION_MAP = query -> {
        Cursor cursor = query.run();
        try {
            List<Movie> values = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                values.add(new Movie()
                        .setId(getLong(cursor, MoviesContract.Movies.MOVIE_ID))
                        .setTitle(getString(cursor, MoviesContract.Movies.MOVIE_TITLE))
                        .setOverview(getString(cursor, MoviesContract.Movies.MOVIE_OVERVIEW))
                        .setPosterPath(getString(cursor, MoviesContract.Movies.MOVIE_POSTER_PATH))
                        .setBackdropPath(getString(cursor, MoviesContract.Movies.MOVIE_BACKDROP_PATH))
                        .setFavorited(getBoolean(cursor, MoviesContract.Movies.MOVIE_FAVORED))
                        .setPopularity(getDouble(cursor, MoviesContract.Movies.MOVIE_POPULARITY))
                        .setVoteCount(getInt(cursor, MoviesContract.Movies.MOVIE_VOTE_COUNT))
                        .setVoteAverage(getDouble(cursor, MoviesContract.Movies.MOVIE_VOTE_AVERAGE))
                        .setReleaseDate(getString(cursor, MoviesContract.Movies.MOVIE_RELEASE_DATE)));
            }
            return values;
        } finally {
            cursor.close();
        }
    };

    String[] ID_PROJECTION = {
            MoviesContract.Movies.MOVIE_ID
    };

    Func1<SqlBrite.Query, Set<Long>> ID_PROJECTION_MAP = query -> {
        Cursor cursor = query.run();
        try {
            Set<Long> idSet = new HashSet<>(cursor.getCount());
            while (cursor.moveToNext()) {
                idSet.add(getLong(cursor, MoviesContract.Movies.MOVIE_ID));
            }
            return idSet;
        } finally {
            cursor.close();
        }
    };

    final class Builder {
        private final ContentValues values = new ContentValues();

        public Builder id(long id) {
            values.put(MoviesContract.Movies.MOVIE_ID, id);
            return this;
        }

        public Builder title(String title) {
            values.put(MoviesContract.Movies.MOVIE_TITLE, title);
            return this;
        }

        public Builder overview(String overview) {
            values.put(MoviesContract.Movies.MOVIE_OVERVIEW, overview);
            return this;
        }

        public Builder genreIds(String genreIds) {
            values.put(MoviesContract.Movies.MOVIE_GENRE_IDS, genreIds);
            return this;
        }

        public Builder backdropPath(String backdropPath) {
            values.put(MoviesContract.Movies.MOVIE_BACKDROP_PATH, backdropPath);
            return this;
        }

        public Builder posterPath(String posterPath) {
            values.put(MoviesContract.Movies.MOVIE_POSTER_PATH, posterPath);
            return this;
        }

        public Builder voteCount(long voteCount) {
            values.put(MoviesContract.Movies.MOVIE_VOTE_COUNT, voteCount);
            return this;
        }

        public Builder voteAverage(double voteCount) {
            values.put(MoviesContract.Movies.MOVIE_VOTE_AVERAGE, voteCount);
            return this;
        }

        public Builder popularity(double popularity) {
            values.put(MoviesContract.Movies.MOVIE_POPULARITY, popularity);
            return this;
        }

        public Builder favored(boolean favored) {
            values.put(MoviesContract.Movies.MOVIE_FAVORED, favored);
            return this;
        }

        public Builder releaseDate(String date) {
            values.put(MoviesContract.Movies.MOVIE_RELEASE_DATE, date);
            return this;
        }

        public Builder movie(Movie movie) {
            return id(movie.getId())
                    .title(movie.getTitle())
                    .overview(movie.getOverview())
                    .backdropPath(movie.getBackdropPath())
                    .posterPath(movie.getPosterPath())
                    .popularity(movie.getPopularity())
                    .voteCount(movie.getVoteCount())
                    .voteAverage(movie.getVoteAverage())
                    .voteAverage(movie.getVoteAverage())
                    .releaseDate(movie.getReleaseDate())
                    .favored(movie.isFavorited());
        }

        public ContentValues build() {
            return values;
        }
    }


    public static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    public static boolean getBoolean(Cursor cursor, String columnName) {
        return getInt(cursor, columnName) == BOOLEAN_TRUE;
    }

    public static long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
    }

    public static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
    }

    public static double getDouble(Cursor cursor, String columnName) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(columnName));
    }
}
