package com.example.musicapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.Utils;

public class Splash extends Activity {
    private static final int SPLASH_TIME_OUT = 3000;

    private TextView splash_TXT_title;
    private ImageView splash_IMG_guitar;

    @Override
    protected void onCreate(Bundle icicle) {

        Utils.setFullScreen(this);
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);
        findViews();
        initViews();


        new Handler().postDelayed(() -> {
            Intent i = new Intent(Splash.this, LoginActivity.class);
            startActivity(i);
            finish();
        }, SPLASH_TIME_OUT);
    }/**/

    private void initViews() {
        Glide.with(this).load(R.drawable.splash_guitar).centerCrop().into(this.splash_IMG_guitar);
        splash_TXT_title.animate().setDuration(2000).scaleX(1).scaleY(1).setInterpolator(new AccelerateInterpolator()).alpha(1f);
    }

    private void findViews() {
        this.splash_TXT_title = findViewById(R.id.splash_TXT_title);
        this.splash_IMG_guitar = findViewById(R.id.splash_IMG_guitar);
    }
}