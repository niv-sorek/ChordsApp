package com.example.musicapp.screens;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.Utils;
import com.example.musicapp.Viewable;
import com.example.musicapp.models.Artist;
import com.example.musicapp.models.Song;
import com.example.musicapp.views.SongsListAdapter;
import com.google.firebase.firestore.FirebaseFirestore;


public class ShowArtist extends AppCompatActivity implements Viewable {

    private ImageView artist_IMG_avatar;
    private TextView artist_TXT_artistName;
    private RatingBar artist_RATE_rating;
    private ListView artist_LST_topSongs;

    private Artist artist;
    private LinearLayout artist_LAY_details;
    private ProgressBar artist_SPN_spinner;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setFullScreen(this);
        setContentView(R.layout.activity_show_artist);

        findViews();
        initViews();

    }

    @Override
    public void findViews() {
        this.artist_LAY_details = findViewById(R.id.artist_LAY_details);
        this.artist_IMG_avatar = findViewById(R.id.artist_IMG_avatar);
        this.artist_TXT_artistName = findViewById(R.id.artist_TXT_artistName);
        this.artist_RATE_rating = findViewById(R.id.artist_RATE_rating);
        this.artist_LST_topSongs = findViewById(R.id.artist_LST_topSongs);
        this.artist_SPN_spinner = findViewById(R.id.artist_SPN_spinner);
    }

    @Override
    public void initViews() {
        this.artist_LAY_details.setVisibility(View.INVISIBLE);

        String artistId = getIntent().getStringExtra("artistId");

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("artists").document(artistId).get().addOnSuccessListener(artistRef ->
        {
            this.artist = artistRef.toObject(Artist.class);
            this.artist_TXT_artistName.setText(artist.getName());
            Glide.with(this).load(this.artist.getImageURL()).circleCrop().into(this.artist_IMG_avatar);
            database.collection("songs").whereEqualTo("artistId", artist.getId()).get().addOnSuccessListener(songsRef -> {
                songsRef.forEach(song -> this.artist.getSongs().add(song.toObject(Song.class).setArtist(this.artist)));
                this.artist_RATE_rating.setProgress(this.artist.getRank());
                this.artist_SPN_spinner.setVisibility(View.INVISIBLE);
                artist_LAY_details.setVisibility(View.VISIBLE);
                this.artist_LST_topSongs.setAdapter(new SongsListAdapter(this, this.artist.getSongs(), false));
            });
        });
    }
}
