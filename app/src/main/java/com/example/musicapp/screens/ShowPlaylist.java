package com.example.musicapp.screens;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.boundaries.Playlist;
import com.example.musicapp.views.SongsListAdapter;
import com.google.gson.Gson;

public class ShowPlaylist extends AppCompatActivity {
    private Playlist playlist;
    private TextView playlists_TXT_name;
    private ListView playlists_LST_songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_playlist);

        findViews();
        initViews();

    }

    private void findViews() {
        this.playlists_LST_songs = findViewById(R.id.playlist_LST_songs);
        this.playlists_TXT_name = findViewById(R.id.playlists_TXT_name);

    }

    private void initViews() {
        String playlistJSON = getIntent().getStringExtra("playlist");
        playlist = new Gson().fromJson(playlistJSON, Playlist.class);

        this.playlists_TXT_name.setText(this.playlist.getName());
        this.playlists_LST_songs.setAdapter(new SongsListAdapter(this, this.playlist.getSongs()));

    }
}
