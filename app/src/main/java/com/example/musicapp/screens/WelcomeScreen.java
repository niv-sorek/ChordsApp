package com.example.musicapp.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.musicapp.R;
import com.example.musicapp.Viewable;

public class WelcomeScreen extends AppCompatActivity implements Viewable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
    }

    @Override
    public void findViews() {

    }

    @Override
    public void initViews() {

    }
}