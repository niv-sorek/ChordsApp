package com.example.musicapp.screens;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.boundaries.Artist;
import com.example.musicapp.boundaries.Playlist;
import com.example.musicapp.boundaries.Song;
import com.example.musicapp.views.SongsListAdapter;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.stream.Collectors;

enum SEARCH {
    Users, Songs
}

public class ShowPlaylist extends AppCompatActivity {
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    private Playlist playlist;
    private SEARCH searchType = SEARCH.Songs;
    private TextView playlist_TXT_name;
    private ListView playlist_LST_songs;
    private ImageView playlist_IMG_share;
    private ImageView playlist_IMG_addSong;
    private AutoCompleteTextView playlist_INP_search;
    private Button playlist_BTN_ok;
    private LinearLayout playlist_LAY_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_playlist);
        String playlistJSON = getIntent().getStringExtra("playlist");
        playlist = new Gson().fromJson(playlistJSON, Playlist.class);
        findViews();
        initViews();

    }

    private void findViews() {
        this.playlist_BTN_ok = findViewById(R.id.playlist_BTN_ok);
        this.playlist_LST_songs = findViewById(R.id.playlist_LST_songs);
        this.playlist_TXT_name = findViewById(R.id.playlist_TXT_name);
        this.playlist_IMG_addSong = findViewById(R.id.playlist_IMG_addSong);
        this.playlist_IMG_share = findViewById(R.id.playlist_IMG_share);
        this.playlist_INP_search = findViewById(R.id.playlist_INP_search);
        this.playlist_LAY_input = findViewById(R.id.playlist_LAY_input);
    }

    private void initViews() {

        this.playlist_LAY_input.setVisibility(View.INVISIBLE);
        this.playlist_TXT_name.setText(this.playlist.getName());
        this.playlist_LST_songs.setAdapter(new SongsListAdapter(this, this.playlist.getSongs()));

        this.playlist_IMG_share.setOnClickListener(v ->
        {
            this.playlist_INP_search.setHint("Search User");
            this.searchType = SEARCH.Users;
            updateUI();
        });
        this.playlist_IMG_addSong.setOnClickListener(v -> {
            this.playlist_INP_search.setHint("Add Song ");
            this.searchType = SEARCH.Songs;
            updateUI();
        });
        this.playlist_BTN_ok.setOnClickListener(v -> {
            if (searchType == SEARCH.Songs) {
                database.collection("songs")
                        .whereEqualTo("name", this.playlist_INP_search.getText().toString())
                        .get().addOnSuccessListener(t -> {
                    if (t.getDocuments().size() > 0) {
                        Song song = t.getDocuments().get(0).toObject(Song.class);
                        database.collection("playlists").document(playlist.getId()).update("songs", FieldValue.arrayUnion(song.getId()));
                        database.collection("artists").document(song.getArtistId()).get().addOnSuccessListener(artistDoc -> {
                            Artist artist = artistDoc.toObject(Artist.class);
                            song.setArtist(artist);
                            playlist.getSongs().add(song);
                            initViews();
                        });
                    }
                });
            } else if (searchType == SEARCH.Users) {
            }

        });
    }


    private void updateUI() {
        this.playlist_LAY_input.setVisibility(this.playlist_LAY_input.getVisibility() == View.INVISIBLE? View.VISIBLE: View.INVISIBLE);
        if (this.searchType == SEARCH.Songs) {
            database.collection("songs").get().addOnSuccessListener(v -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_dropdown_item_1line, v.getDocuments()
                        .stream()
                        .map(x -> x.getString("name"))
                        .collect(Collectors.toList()));

                this.playlist_INP_search.setAdapter(adapter);
                this.playlist_INP_search.setOnEditorActionListener((v1, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        this.playlist_BTN_ok.performClick();
                        return true;
                    }
                    return false;
                });
            });
        } else if (searchType == SEARCH.Users) {

        }
    }
}
