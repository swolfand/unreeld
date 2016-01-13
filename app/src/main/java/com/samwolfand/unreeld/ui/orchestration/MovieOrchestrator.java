package com.samwolfand.unreeld.ui.orchestration;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;

import com.samwolfand.unreeld.R;
import com.samwolfand.unreeld.network.entities.Movie;
import com.samwolfand.unreeld.network.entities.Video;
import com.samwolfand.unreeld.network.repository.MoviesRepository;
import com.samwolfand.unreeld.ui.otto.FavoriteClickedEvent;
import com.samwolfand.unreeld.util.PrefUtils;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

/**
 * Created by wkh176 on 1/11/16.
 */
public class MovieOrchestrator {

    private static final PublishSubject<FavoriteClickedEvent> FAVORITE_SUBJECT = PublishSubject.create();

    private final Activity mActivity;
    private final MoviesRepository mRepository;

    public MovieOrchestrator(Activity activity, MoviesRepository repository) {
        mActivity = activity;
        mRepository = repository;
    }


    public Observable<FavoriteClickedEvent> getFavoriteObservable() {
        return FAVORITE_SUBJECT.asObservable();
    }

    public void setFavorite(Movie movie, boolean favorited) {
        movie.setFavorited(favorited);
        if (favorited) {
            mRepository.saveMovie(movie);
            PrefUtils.addToFavorites(movie.getId());
        } else {
            mRepository.deleteMovie(movie);
            PrefUtils.removeFromFavorites(movie.getId());
        }
        FAVORITE_SUBJECT.onNext(new FavoriteClickedEvent(movie.getId(), favorited));
    }

    public void playVideo(Video video) {
        if (video.getSite().equals(Video.SITE_YOUTUBE))
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video.getKey())));
        else
            Timber.w("Unsupported video format");
    }

    public void shareTrailer(int template, Video trailer) {
        mActivity.startActivity(Intent.createChooser(createShareTrailerIntent(template, trailer.getName(), trailer.getKey()), mActivity.getString(R.string.share_trailer)));
    }

    private Intent createShareTrailerIntent(int template, String name, String key) {
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(mActivity)
                .setType("text/plain")
                .setText(mActivity.getString(template, name, " http://www.youtube.com/watch?v=" + key));
        return builder.getIntent();
    }
}
