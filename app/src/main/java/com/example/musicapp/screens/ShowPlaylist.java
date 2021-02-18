package com.example.musicapp.screens;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.ListItem;
import com.example.musicapp.R;
import com.example.musicapp.Utils;
import com.example.musicapp.boundaries.Artist;
import com.example.musicapp.boundaries.Playlist;
import com.example.musicapp.boundaries.Song;
import com.example.musicapp.views.SongsListAdapter;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.List;
import java.util.stream.Collectors;

enum SEARCH {
    Users, Songs
}

public class ShowPlaylist extends AppCompatActivity {
    final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private Playlist playlist;
    private SEARCH searchType = SEARCH.Songs;
    private TextView playlist_TXT_name;
    private ListView playlist_LST_songs;
    private ImageView playlist_IMG_share;
    private ImageView playlist_IMG_addSong;
    private TextInputLayout playlist_INP_search;
    private LinearLayout playlist_LAY_input;
    private AutoCompleteTextView editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.setFullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_playlist);
        String playlistJSON = getIntent().getStringExtra("playlist");
        playlist = new Gson().fromJson(playlistJSON, Playlist.class);
        findViews();
        initViews();

    }

    private void findViews() {
        this.playlist_TXT_name = findViewById(R.id.playlist_TXT_name);
        this.playlist_IMG_addSong = findViewById(R.id.playlist_IMG_addSong);
        this.playlist_IMG_share = findViewById(R.id.playlist_IMG_share);
        this.playlist_INP_search = findViewById(R.id.playlist_INP_search);
        this.playlist_LAY_input = findViewById(R.id.playlist_LAY_input);
        this.playlist_LST_songs = findViewById(R.id.playlist_LST_songs);
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

        this.editText = (AutoCompleteTextView) this.playlist_INP_search.getEditText();

        editText.setOnItemClickListener((parent, view, position, id) -> {

            // Close Keyboard
            InputMethodManager inputManager =
                    (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

            View view1 = this.getCurrentFocus();

            if (view1 != null) {

                this.getWindow()
                        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                inputManager.hideSoftInputFromWindow(view1
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            this.playlist_INP_search.getEditText().setText("");
            /// ---------------------------------------------------------------------------------

            if (searchType == SEARCH.Songs) {
                Song item = (Song) ((ListItem) parent.getAdapter().getItem(position)).getData();

                database.collection("playlists").document(playlist.getId())
                        .update("songs", FieldValue.arrayUnion(item.getId()));
                database.collection("artists").document(item.getArtistId()).get()
                        .addOnSuccessListener(artistDoc -> {
                            Artist artist = artistDoc.toObject(Artist.class);
                            item.setArtist(artist);
                            playlist.getSongs().add(item);
                            initViews();
                            Toast t = Toast.makeText(this, parent
                                    .getAdapter().getItem(position)
                                    .toString() + "Added to playlist " + playlist
                                    .getName(), Toast.LENGTH_LONG);
                            t.show();
                        });
            } else if (searchType == SEARCH.Users) {
                String userId =
                        (String) ((ListItem) parent.getAdapter().getItem(position)).getData();
                database.collection("users").document(userId)
                        .update("playlists", FieldValue.arrayUnion(playlist.getId()))
                        .addOnSuccessListener(v ->
                        {
                            Toast t = Toast.makeText(this, "Playlist shared With " + parent
                                    .getAdapter().getItem(position).toString(), Toast.LENGTH_LONG);
                            t.show();
                        });
            }

        });
    }


    private void updateUI() {
        this.playlist_LAY_input.setVisibility(this.playlist_LAY_input
                .getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
        if (this.searchType == SEARCH.Songs) {
            updateUISearchSong();
        } else if (searchType == SEARCH.Users) {
            updateUISearchUser();
        }
    }

    private void updateUISearchUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        database.collection("users").get().addOnSuccessListener(v -> {
            ArrayAdapter<ListItem> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, v.getDocuments()
                    .stream().filter(x -> !x.getId().equals(user.getUid())
                    )
                    .map(x -> new ListItem(x.getString("name"), x.getId()))
                    .collect(Collectors.toList()));

            editText.setAdapter(adapter);

        });
    }


    private void updateUISearchSong() {
        database.collection("songs").get().addOnSuccessListener(v -> {

            List<ListItem> items = v.getDocuments().stream()
                    .map(x -> {
                        return new ListItem(x.getString("name") + " - " + MainActivity.artistsList
                                .stream()
                                .filter(a -> a.getId().equals(x.getString("artistId"))).findFirst()
                                .get()
                                .getName(), x.toObject(Song.class));
                    })
                    .collect(Collectors.toList());

            ArrayAdapter<ListItem> adapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
            editText.setAdapter(adapter);


        });
    }
}
