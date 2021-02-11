package com.example.musicapp;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class Utils {
    public static String makeId(String str) {
        return str.trim().replaceAll("[-+.^:,!@#$%&*?]", "").replace(' ', '_').toLowerCase();
    }

    public static void setFullScreen(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
