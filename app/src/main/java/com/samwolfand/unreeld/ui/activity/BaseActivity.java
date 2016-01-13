package com.samwolfand.unreeld.ui.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.samwolfand.unreeld.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.Module;
import timber.log.Timber;

/**
 * Created by Sam Wolfand on 1/5/16.
 */

public class BaseActivity extends AppCompatActivity {
    @Nullable @Bind(R.id.toolbar) Toolbar mToolbar;

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @CallSuper
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        initToolbar();
    }

    @Nullable
    public Toolbar getToolbar() {
        return mToolbar;
    }

    private void initToolbar() {
        if (mToolbar == null) {
            Timber.w("No toolbar found");
            return;
        }

        ViewCompat.setElevation(mToolbar, 8);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
    }

}
