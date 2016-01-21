package com.samwolfand.unreeld.ui.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samwolfand.unreeld.R;
import com.samwolfand.unreeld.UnreeldApplication;
import com.samwolfand.unreeld.network.api.Sort;
import com.samwolfand.unreeld.network.entities.Movie;
import com.samwolfand.unreeld.network.repository.MoviesRepository;
import com.samwolfand.unreeld.ui.adapter.MoviesAdapter;
import com.samwolfand.unreeld.ui.widget.AspectLockedImageView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.Module;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;


@Module
public class MoviesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private static final String KEY_MOVIES = "movies";
    private static final String ARG_SORT = "sort";

    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.error_layout) LinearLayout mErrorLayout;

    @Inject MoviesRepository mMoviesRepository;



    private GridLayoutManager mGridLayoutManager;
    private MoviesAdapter mMoviesAdapter;
    private Sort mSort;

    public MoviesFragment() {
        // Required empty public constructor
    }

    public static MoviesFragment newInstance(@NonNull Sort sort) {
        Bundle args = new Bundle();

        args.putSerializable(ARG_SORT, sort);

        MoviesFragment fragment = new MoviesFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSort = (Sort) getArguments().getSerializable(ARG_SORT);

        List<Movie> moviesFromInstance = savedInstanceState != null
                ? savedInstanceState.getParcelableArrayList(KEY_MOVIES)
                : new ArrayList<>();

        mMoviesAdapter = new MoviesAdapter(getContext(), moviesFromInstance);
        initRecyclerView();

    }

    private void initRecyclerView() {
        mGridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.num_columns));
        mRecyclerView.setLayoutManager(mGridLayoutManager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        ((UnreeldApplication) getActivity().getApplication()).getAppComponent().inject(this);
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        loadMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_MOVIES, new ArrayList<>(mMoviesAdapter.getItems()));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void loadMovies() {
        if (mSort.equals(Sort.FAVORITE)) {
            mMoviesRepository.savedMovies()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(savedMovies -> {
                        Timber.d(String.format("%d Favorite movies found", savedMovies.size()));
                        mMoviesAdapter = new MoviesAdapter(getContext(), savedMovies);
                        mRecyclerView.setAdapter(mMoviesAdapter);
                    }, throwable -> Timber.e(throwable, "Failed to load saved movies"));
        } else {

            mMoviesRepository.discoverMovies(mSort, 1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(movies -> {

                        mMoviesAdapter.add(movies);
                        mRecyclerView.setAdapter(mMoviesAdapter);
                    }, throwable -> {
                        Timber.e(throwable, "No movies found");
                        Toast.makeText(getContext(), "This content is not available offline", Toast.LENGTH_SHORT).show();
                        mErrorLayout.setVisibility(View.VISIBLE);
                    });
        }
    }

    @Override
    public void onRefresh() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
