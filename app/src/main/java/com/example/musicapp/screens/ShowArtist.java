package com.example.musicapp.screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musicapp.Artist;
import com.example.musicapp.R;
import com.example.musicapp.Viewable;
import com.example.musicapp.views.SongsListAdapter;
import com.google.gson.Gson;

public class ShowArtist extends AppCompatActivity implements Viewable {

    private ImageView artist_IMG_avatar;
    private TextView artist_TXT_artistName;
    private RatingBar artist_RATE_rating;
    private ListView artist_LST_topSongs;

    private Artist artist;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_artist);

        findViews();
        initViews();

    }

    @Override
    public void findViews() {
        this.artist_IMG_avatar = findViewById(R.id.artist_IMG_avatar);
        this.artist_TXT_artistName = findViewById(R.id.artist_TXT_artistName);
        this.artist_RATE_rating = findViewById(R.id.artist_RATE_rating);
        this.artist_LST_topSongs = findViewById(R.id.artist_LST_topSongs);
    }

    @Override
    public void initViews() {
        String json = getIntent().getStringExtra("artist");
        Gson gson = new Gson();
        Log.d("pttt", json);
        this.artist = gson.fromJson(json, Artist.class);
        this.artist_TXT_artistName.setText(artist.getName());
        Glide.with(this).load(this.artist.getImagePath()).circleCrop().into(this.artist_IMG_avatar);
        this.artist_RATE_rating.setProgress(this.artist.getRank());
        this.artist_LST_topSongs.setAdapter(new SongsListAdapter(this.getApplicationContext(), this.artist.getSongs()));


    }

    public static class SongListItem extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_song_list_item);
        }
    }
}
