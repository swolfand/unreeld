package com.samwolfand.unreeld.network.provider;

import android.net.Uri;
import android.provider.BaseColumns;


public final class MoviesContract {

    public static final String QUERY_PARAMETER_DISTINCT = "distinct";

    public interface MoviesColumns {
        String MOVIE_ID = "movie_id";
        String MOVIE_TITLE = "movie_title";
        String MOVIE_OVERVIEW = "movie_overview";
        String MOVIE_POPULARITY = "movie_popularity";
        String MOVIE_GENRE_IDS = "movie_genre_ids"; //This is a comma-separated list of genre ids.
        String MOVIE_VOTE_COUNT = "movie_vote_count";
        String MOVIE_VOTE_AVERAGE = "movie_vote_average";
        String MOVIE_RELEASE_DATE = "movie_release_date";
        String MOVIE_FAVORED = "movie_favored";
        String MOVIE_POSTER_PATH = "movie_poster_path";
        String MOVIE_BACKDROP_PATH = "movie_backdrop_path";
    }

    public static final String CONTENT_AUTHORITY = "com.samwolfand.unreeld.network.provider";

    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_MOVIES = "movies";


    public static class Movies implements MoviesColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.unreeld.movie";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.unreeld.movie";

        /**
         * Default "ORDER BY" clause.
         */
        public static final String DEFAULT_SORT = BaseColumns._ID + " DESC";

        /**
         * Build {@link Uri} for requested {@link #MOVIE_ID}
         */
        public static Uri buildMovieUri(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        /**
         * Read {@link #MOVIE_ID} from {@link Movies} {@link Uri}
         */
        public static String getMovieId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    private MoviesContract() {
        throw new AssertionError("No instances.");
    }
}
