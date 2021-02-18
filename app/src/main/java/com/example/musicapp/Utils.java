package com.example.musicapp;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Utils {
    public static String makeId(String str) {
        return str.trim().replaceAll("[-+.^:,!@#$%&*?]", "").replace(' ', '_').toLowerCase();
    }

    public static void setFullScreen(AppCompatActivity activity) {

        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        activity. setContentView(R.layout.activity_show_song);
        View decorView = activity.getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.hide();
    }
}
