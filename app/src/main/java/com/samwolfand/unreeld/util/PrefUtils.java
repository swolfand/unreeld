package com.samwolfand.unreeld.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.samwolfand.unreeld.network.api.Sort;

import java.util.HashSet;
import java.util.Set;


public final class PrefUtils {


    /**
     * Boolean indicating whether we performed the (one-time) welcome flow.
     */
    public static final String PREF_WELCOME_DONE = "pref_welcome_done";

    public static final String PREF_FAVORED_MOVIES = "pref_favored_movies";

    public static final String PREF_BROWSE_MOVIES_MODE = "pref_browse_movies_mode";

    public static final String PREF_INCLUDE_ADULT = "pref_include_adult";

    public static boolean isWelcomeDone(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_WELCOME_DONE, false);
    }

    public static void markWelcomeDone(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_WELCOME_DONE, true).apply();
    }

    public static void addToFavorites(long movieId) {

        Set<String> set = Prefs.getStringSet(PREF_FAVORED_MOVIES);
        if (set == null) set = new HashSet<>();
        set.add(String.valueOf(movieId));
        Prefs.putStringSet(PREF_FAVORED_MOVIES, set);
    }

    public static void removeFromFavorites(long movieId) {

        Set<String> set = Prefs.getStringSet(PREF_FAVORED_MOVIES);
        if (set == null) set = new HashSet<>();
        set.remove(String.valueOf(movieId));
        Prefs.putStringSet(PREF_FAVORED_MOVIES, set);
    }

    public static String getBrowseMoviesMode() {
        return Prefs.getString(PREF_BROWSE_MOVIES_MODE, Sort.POPULARITY.toString());
    }

    public static void setBrowseMoviesMode(final Context context, String mode) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_BROWSE_MOVIES_MODE, mode).apply();
    }

}
