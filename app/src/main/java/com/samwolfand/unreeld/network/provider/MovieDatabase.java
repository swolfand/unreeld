package com.samwolfand.unreeld.network.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.samwolfand.unreeld.network.provider.MoviesContract.MoviesColumns;


/**
 * Created by wkh176 on 12/8/15.
 */
final class MovieDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "movies.db";
    private static final int DB_VERSION = 1;

    private final Context context;

    interface Tables {
        String MOVIES = "movies";


        String MOVIE_JOIN_GENRES = "movies " + "LEFT OUTER JOIN movies_genres ON movies.movie_id=movies_genres.genre_id";
        String MOVIES_GENRES_JOIN_GENRES = "movies_genres "
                + "LEFT OUTER JOIN genres ON movies_genres.genre_id=genres.genre_id";
    }

    public interface MoviesGenres {
        String MOVIE_ID = "movie_id";
        String GENRE_ID = "genre_id";
    }

    private interface Qualified {

    }

    private interface References {

        String MOVIE_ID = "REFERENCES " + Tables.MOVIES + "(" + MoviesContract.Movies.MOVIE_ID + ")";
    }

    public MovieDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.MOVIES + "("
                + BaseColumns._ID + " INTEGER NOT NULL PRIMARY KEY,"
                + MoviesColumns.MOVIE_ID + " TEXT NOT NULL,"
                + MoviesColumns.MOVIE_TITLE + " TEXT NOT NULL,"
                + MoviesColumns.MOVIE_OVERVIEW + " TEXT,"
                + MoviesColumns.MOVIE_GENRE_IDS + " TEXT,"
                + MoviesColumns.MOVIE_POPULARITY + " REAL,"
                + MoviesColumns.MOVIE_VOTE_AVERAGE + " REAL,"
                + MoviesColumns.MOVIE_VOTE_COUNT + " INTEGER,"
                + MoviesColumns.MOVIE_BACKDROP_PATH + " TEXT,"
                + MoviesColumns.MOVIE_POSTER_PATH + " TEXT,"
                + MoviesColumns.MOVIE_RELEASE_DATE + " TEXT,"
                + MoviesColumns.MOVIE_FAVORED + " INTEGER NOT NULL DEFAULT 0,"
                + "UNIQUE (" + MoviesColumns.MOVIE_ID + ") ON CONFLICT REPLACE)");

//        insertGenres(db);
    }

//    private void insertGenres(SQLiteDatabase db) {
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(28).name("Action").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(12).name("Adventure").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(16).name("Animation").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(35).name("Comedy").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(80).name("Crime").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(99).name("Documentary").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(18).name("Drama").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(10751).name("Family").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(14).name("Fantasy").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(10765).name("Foreign").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(36).name("History").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(27).name("Horror").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(10402).name("Music").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(9648).name("Mystery").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(10749).name("Romance").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(878).name("Science Fiction").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(10770).name("TV Movie").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(53).name("Thriller").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(10752).name("War").build());
//        db.insert(Tables.GENRES, null, new Genre.Builder().id(37).name("Western").build());
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DB_NAME);
    }
}
