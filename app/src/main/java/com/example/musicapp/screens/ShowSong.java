package com.example.musicapp.screens;


import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.ChordsUtils;
import com.example.musicapp.R;
import com.example.musicapp.Utils;
import com.example.musicapp.Viewable;
import com.example.musicapp.models.Artist;
import com.example.musicapp.models.Song;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.concurrent.atomic.AtomicInteger;

public class ShowSong extends AppCompatActivity implements Viewable {

    private TextView song_TXT_songName;
    private TextView song_TXT_artistName;
    private LinearLayout song_LAY_chords;
    private Song song;
    private RelativeLayout song_LAY_details;
    private ProgressBar song_PRG_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setFullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_song);
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.

        ActionBar actionBar = getActionBar();
        actionBar.hide();
        findViews();
        initViews();
    }

    @Override
    public void findViews() {
        this.song_LAY_details = findViewById(R.id.song_LAY_details);
        this.song_LAY_chords = findViewById(R.id.song_LAY_chords);
        this.song_TXT_songName = findViewById(R.id.song_TXT_songName);
        this.song_TXT_artistName = findViewById(R.id.song_TXT_artistName);
        ImageView song_ICN_like = findViewById(R.id.song_ICN_like);
        TextView song_TXT_likesCount = findViewById(R.id.song_TXT_likesCount);
        RelativeLayout song_LAY_main = findViewById(R.id.song_LAY_main);
        this.song_PRG_loading = findViewById(R.id.song_PRG_loading);
    }

    @Override
    public void initViews() {

        this.song_LAY_details.setVisibility(View.INVISIBLE);


        String songId = getIntent().getStringExtra("song");
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.document("songs/" + songId).get().addOnSuccessListener(songSnapshot -> {
            this.song = songSnapshot.toObject(Song.class);
            song_TXT_songName.setText(song.getName());
            song_LAY_details.setLayoutDirection(song.isRtl() ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
            if (this.song.getChords() != null && this.song.getChords().length() > 0) {
                updateChords();
            }
            DocumentReference artistDocument = songSnapshot.getDocumentReference("artist");
            artistDocument.get().addOnSuccessListener(artistSnapshot -> {
                song.setArtist(artistSnapshot.toObject(Artist.class));
                song_TXT_artistName.setText(song.getArtist().getName());
                this.song_TXT_artistName.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ShowArtist.class);
                    intent.putExtra("artist", new Gson().toJson(this.song.getArtist()));
                    this.startActivity(intent);
                });

                this.song_PRG_loading.setVisibility(View.INVISIBLE);
                this.song_LAY_details.setVisibility(View.VISIBLE);
            });
        });
    }

    private void updateChords() {
        AtomicInteger index = new AtomicInteger();
        ChordsUtils.formatChordsString(this.song.getChords(), "_", 0).forEach(s -> {
            TextView textView = new TextView(this);
            textView.setTextSize(18);
            if (index.get() % 2 == 0) textView.setTextColor(Color.BLUE);
            textView.setText(s);
            song_LAY_chords.addView(textView);
            index.getAndIncrement();
        });
    }
}