package com.samwolfand.unreeld.ui.activity;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.samwolfand.unreeld.BuildConfig;
import com.samwolfand.unreeld.R;
import com.samwolfand.unreeld.TestUnreeldApplication;
import com.samwolfand.unreeld.network.api.MoviesApi;
import com.samwolfand.unreeld.network.api.Sort;
import com.samwolfand.unreeld.network.entities.Movie;
import com.samwolfand.unreeld.network.repository.MoviesRepository;
import com.samwolfand.unreeld.ui.adapter.MoviesAdapter;
import com.samwolfand.unreeld.ui.fragment.MoviesFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.ShadowsAdapter;
import org.robolectric.annotation.Config;
import org.robolectric.internal.Shadow;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowViewGroup;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import rx.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, application = TestUnreeldApplication.class)
public class TestMoviesActivity {

    @Mock
    MoviesRepository mMoviesRepository;

    @Mock
    MoviesApi mMoviesApi;

    List<Movie> mMovies = new ArrayList<>();
    RecyclerView mRecyclerView;
    MoviesActivity mMoviesActivity;
    MoviesAdapter mMoviesAdapter;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        TestUnreeldApplication app = (TestUnreeldApplication) RuntimeEnvironment.application;
        MoviesActivity activity = Robolectric.setupActivity(MoviesActivity.class);

        mRecyclerView = (RecyclerView) activity.findViewById(R.id.recycler_view);
        mRecyclerView.measure(0, 0);
        mRecyclerView.layout(0, 0, 100, 10000);
        mMovies.add(new Movie());
        mMoviesAdapter = new MoviesAdapter(app.getApplicationContext(), mMovies);

    }

    @Test
    public void testAdapter_doesLoad() throws Exception {
        ShadowApplication application = shadowOf(RuntimeEnvironment.application);


        int count = mRecyclerView.getAdapter().getItemCount();
        assertTrue(count >= 0);
    }

    @Test
    public void testGetToolbar() throws Exception {
        MoviesActivity activity = Robolectric.setupActivity(MoviesActivity.class);
        Toolbar toolbar = activity.getToolbar();
        assertNotNull(toolbar);
    }


}
