package com.example.musicapp.screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.Artist;
import com.example.musicapp.R;
import com.example.musicapp.Rank;
import com.example.musicapp.Song;
import com.example.musicapp.Viewable;
import com.example.musicapp.views.SongsListAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Viewable {

    private ListView main_LST_topSongs;

     private ArrayList<Song> songs = new ArrayList<Song>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initViews();
//        Gson  gson =  new Gson();

//        Intent intent = new Intent(MainActivity.this, ShowArtist.class);
//        intent.putExtra("artist", gson.toJson(a));
//        startActivity(intent);
    }

    @Override
    public void findViews() {
        this.main_LST_topSongs
                = findViewById(R.id.main_LST_topSongs);
    }

    @Override
    public void initViews() {
        createDummyData();
        SongsListAdapter adapter = new SongsListAdapter(this, songs);
        this.main_LST_topSongs.setAdapter(adapter);
    }

    private void createDummyData() {
        Artist freddie_mercury = new Artist().setName("Freddie Mercury").setImagePath("https://ucarecdn.com/2ffd0337-37d0-429d-a1a5-485165bb3006/-/crop/333x333/107,32/-/preview/-/progressive/yes/-/format/auto/-/scale_crop/900x900/");
        Artist beatles = new Artist().setName("The Beatles").setImagePath("https://upload.wikimedia.org/wikipedia/commons/9/9f/Beatles_ad_1965_just_the_beatles_crop.jpg");
        ArrayList<Artist> artists = new ArrayList<>();

        Log.d("pttt", new Gson().toJson(beatles));
        Song let_it_be = new Song("Let It Be", beatles);
        beatles.addSong(let_it_be);
        let_it_be.addRank(new Rank(10, null));
        songs.add(new Song("Love of my life", freddie_mercury));
        songs.add(new Song("Radio ga ga", freddie_mercury));
        songs.add(let_it_be);
        songs.add(new Song("We will rock you", freddie_mercury));
    }
}