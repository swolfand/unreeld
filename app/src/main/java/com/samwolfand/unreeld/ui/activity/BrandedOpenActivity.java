package com.samwolfand.unreeld.ui.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.samwolfand.unreeld.R;
import com.samwolfand.unreeld.util.Prefs;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class BrandedOpenActivity extends AppCompatActivity {

    @Bind(R.id.progress_bar_custom) ProgressBar progressBarCustom;
    @Bind(R.id.unreel) ImageView unreel;

    public static final String SEEN_ANIMATION = "seen_animation";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branded_open);
        ButterKnife.bind(this);
        int time = 0;
        progressBarCustom.setVisibility(View.GONE);
        if (!Prefs.getBoolean(SEEN_ANIMATION)) {
            time = 3000;
            Prefs.putBoolean(SEEN_ANIMATION, true);
        }
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            finish();
            startActivity(new Intent(BrandedOpenActivity.this, MoviesActivity.class));
            overridePendingTransition(0, R.anim.fade_out);
        }, time);

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        unreel.setBackgroundResource(R.drawable.unreel_animation);
        AnimationDrawable animation = (AnimationDrawable) unreel.getDrawable();

        animation.start();

    }
}
