package com.samwolfand.unreeld.network.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.samwolfand.unreeld.network.provider.MovieDatabase.Tables;
import com.samwolfand.unreeld.network.provider.MoviesContract.Movies;


import java.util.Arrays;

import dagger.Module;
import timber.log.Timber;

import static com.samwolfand.unreeld.network.provider.MoviesContract.*;


/**
 * Created by wkh176 on 12/8/15.
 */

@Module
public class MovieProvider extends ContentProvider {

    private static final int GENRES = 100;
    private static final int MOVIES = 200;
    private static final int MOVIES_ID = 201;
    private static final int MOVIES_ID_GENRES = 202;
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private SQLiteOpenHelper openHelper;

    /**
     * Build and return a {@link UriMatcher} that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, "genres", GENRES);

        matcher.addURI(authority, "movies", MOVIES);
        matcher.addURI(authority, "movies/*", MOVIES_ID);
        matcher.addURI(authority, "movies/*/genres", MOVIES_ID_GENRES);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        openHelper = new MovieDatabase(getContext());
        return true;
    }

    private void deleteDatabase() {
        openHelper.close();
        Context context = getContext();
        MovieDatabase.deleteDatabase(context);
        openHelper = new MovieDatabase(getContext());
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = openHelper.getReadableDatabase();
        final int match = uriMatcher.match(uri);

        Timber.tag(getClass().getCanonicalName()).v("uri=" + uri + " match=" + match + " proj=" + Arrays.toString(projection) +
                " selection=" + selection + " args=" + Arrays.toString(selectionArgs) + ")");

        final SelectionBuilder builder = buildExpandedSelection(uri, match);

        boolean distinct = !TextUtils.isEmpty(uri.getQueryParameter(QUERY_PARAMETER_DISTINCT));

        Cursor cursor = builder
                .where(selection, selectionArgs)
                .query(db, distinct, projection, sortOrder, null);

        Context context = getContext();
        if (null != context) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return cursor;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
//            case GENRES:
//                return Genres.CONTENT_TYPE;
            case MOVIES:
                return Movies.CONTENT_TYPE;
            case MOVIES_ID:
                return Movies.CONTENT_ITEM_TYPE;
//            case MOVIES_ID_GENRES:
//                return Genres.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case MOVIES: {
                db.insertOrThrow(Tables.MOVIES, null, values);
                notifyChange(uri);
                return Movies.buildMovieUri(values.getAsString(Movies.MOVIE_ID));
            }
            default:
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        notifyChange(uri);
        return retVal;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uri.equals(BASE_CONTENT_URI)) {
            deleteDatabase();
            notifyChange(uri);
            return 1;
        }
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        notifyChange(uri);
        return retVal;
    }

    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }


    /**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually enough to support {@link #insert}
     * {@link #update}, and {@link #delete} operations.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return builder.table(Tables.MOVIES);
            case MOVIES_ID: {
                final String movieId = Movies.getMovieId(uri);
                return builder.table(Tables.MOVIES)
                        .where(Movies.MOVIE_ID + "=?", movieId);
            }
            default:
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
        }
    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case MOVIES:
                return builder.table(Tables.MOVIES);
            case MOVIES_ID: {
                final String movieId = Movies.getMovieId(uri);
                return builder.table(Tables.MOVIE_JOIN_GENRES)
                        .mapToTable(Movies._ID, Tables.MOVIES)
                        .mapToTable(Movies.MOVIE_ID, Tables.MOVIES)
                        .where(Qualified.MOVIES_MOVIE_ID + "=?", movieId);
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private interface Qualified {
        String MOVIES_MOVIE_ID = Tables.MOVIES + "." + Movies.MOVIE_ID;
    }

}
