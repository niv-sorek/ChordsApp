package com.example.musicapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.Artist;
import com.example.musicapp.R;
import com.example.musicapp.Song;
import com.example.musicapp.Viewable;
import com.google.gson.Gson;

public class ShowSong extends AppCompatActivity implements Viewable {

    private TextView song_TXT_songName;
    private TextView song_TXT_artistName;
    private RatingBar song_RATE_rating;
    private Song song;
    private Artist artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_song);

        findViews();
        initViews();
    }

    @Override
    public void findViews() {
        this.song_TXT_songName = findViewById(R.id.song_TXT_songName);
        this.song_TXT_artistName = findViewById(R.id.song_TXT_artistName);
        this.song_RATE_rating = findViewById(R.id.song_RATE_rating);
    }

    @Override
    public void initViews() {
        String json = getIntent().getStringExtra("song");

        Gson gson = new Gson();
        this.song = gson.fromJson(json, Song.class);
        this.artist = gson.fromJson(getIntent().getStringExtra("artist"), Artist.class);
        this.song_TXT_songName.setText(this.song.getName());
        this.song_TXT_artistName.setText(this.artist.getName());
        this.song_RATE_rating.setProgress((int) this.song.getRank());
        this.song_TXT_artistName.setOnClickListener(v -> {
                    Intent intent = new Intent(ShowSong.this, ShowArtist.class);
                    intent.putExtra("artist", new Gson().toJson(this.artist));
                    startActivity(intent);
                }
        );
    }
}