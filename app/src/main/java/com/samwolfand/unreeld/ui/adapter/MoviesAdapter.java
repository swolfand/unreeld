package com.samwolfand.unreeld.ui.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.florent37.glidepalette.GlidePalette;
import com.samwolfand.unreeld.R;
import com.samwolfand.unreeld.network.entities.Movie;
import com.samwolfand.unreeld.ui.otto.BusProvider;
import com.samwolfand.unreeld.ui.otto.FavoriteClickedEvent;
import com.samwolfand.unreeld.ui.otto.OnMovieClickedEvent;

import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

/**
 * Created by wkh176 on 1/5/16.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> mMovies;
    private Context mContext;

    public MoviesAdapter(final Context context, List<Movie> movies) {
        this.mMovies = movies;
        mContext = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_movie, null));
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(mMovies.get(position));
    }

    public void add(@NonNull List<Movie> moviesToAdd) {
        if (!moviesToAdd.isEmpty()) {
            int currentSize = mMovies.size();
            int amountInserted = moviesToAdd.size();

            mMovies.addAll(moviesToAdd);
            notifyItemRangeInserted(currentSize, amountInserted);
        }
    }

    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void clear() {
        mMovies.clear();
    }

    public List<Movie> getItems() {
        return mMovies;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.movie_item_image) com.samwolfand.unreeld.ui.widget.AspectLockedImageView mMovieItemImage;
        @Bind(R.id.movie_item_title) TextView mMovieItemTitle;
        @Bind(R.id.movie_item_btn_favorite) ImageButton mFavoriteButton;
        @Bind(R.id.movie_item_footer) LinearLayout mMovieItemFooter;
        @Bind(R.id.container_inner_item) LinearLayout mContainerInnerItem;
        @Bind(R.id.movie_item_container) CardView mMovieItemContainer;

        @BindColor(R.color.colorPrimary) int mColorPrimary;
        @BindColor(R.color.white) int mColorWhite;
        @BindColor(R.color.body_masked) int mColorWhiteMask;

        private long mMovieId;
        private View mItemView;


        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mItemView = itemView;
        }


        public void bind(@NonNull final Movie movie) {
            mFavoriteButton.setSelected(movie.isFavorited());
            mMovieItemTitle.setText(movie.getTitle());
            mMovieItemContainer.setOnClickListener(v -> BusProvider.getInstance().post(new OnMovieClickedEvent(movie, itemView)));
            mFavoriteButton.setOnClickListener(v -> {
                BusProvider.getInstance()
                        .post(new FavoriteClickedEvent(movie, MovieViewHolder.this.getAdapterPosition()));
                mFavoriteButton.setSelected(movie.isFavorited());
            });
            //stop that blinking ish
            if (mMovieId != movie.getId()) {
                resetColors();
                mMovieId = movie.getId();
            }

            Glide.with(itemView.getContext())
                    .load("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath())
                    .crossFade()
                    .placeholder(R.color.movie_poster_placeholder)
                    .listener(GlidePalette.with(movie.getPosterPath())
                            .intoCallBack(palette -> applyColors(palette.getVibrantSwatch())))
                    .into(mMovieItemImage);
        }

        private void resetColors() {
            mMovieItemFooter.setBackgroundColor(mColorPrimary);
            mMovieItemTitle.setTextColor(mColorWhite);
            mFavoriteButton.setColorFilter(mColorWhite, PorterDuff.Mode.MULTIPLY);
        }

        private void applyColors(Palette.Swatch swatch) {
            if (swatch != null) {
                mMovieItemFooter.setBackgroundColor(swatch.getRgb());
                mMovieItemTitle.setTextColor(swatch.getBodyTextColor());

                mFavoriteButton.setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
            }
        }

    }
}
