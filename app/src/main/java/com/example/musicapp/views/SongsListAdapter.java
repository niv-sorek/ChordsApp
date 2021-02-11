package com.example.musicapp.views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.models.Artist;
import com.example.musicapp.models.Song;
import com.example.musicapp.models.User;
import com.example.musicapp.screens.ShowSong;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.List;


public class SongsListAdapter extends BaseAdapter {
    private final Context context;
    final LayoutInflater inflater;
    final List<Song> songs;
    final Intent intent;
    final Gson gson = new Gson();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    Artist a;

    public SongsListAdapter(Context context, List<Song> songs, User user) {
        this.context = context;
        this.songs = songs;


        intent = new Intent(context, ShowSong.class);
        this.intent.putExtra("user", gson.toJson(user));
        this.inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return this.songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.activity_song_list_item, null);
        ImageView avatar = (ImageView) view.findViewById(R.id.songList_IMG_artist);
        TextView songName = (TextView) view.findViewById(R.id.songList_TXT_songName);
        TextView artistName = (TextView) view.findViewById(R.id.songList_TXT_artistName);
        songName.setText(this.songs.get(i).getName());
        artistName.setText(songs.get(i).getArtist().getName());
        Glide.with(this.context).load(songs.get(i).getArtist().getImageURL()).circleCrop().into(avatar);
        view.setOnClickListener(v -> {
            intent.putExtra("song", songs.get(i).getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.putExtra("artist", gson.toJson(songs.get(i).getArtist()));

            context.startActivity(intent);

        });


        return view;

    }
}
