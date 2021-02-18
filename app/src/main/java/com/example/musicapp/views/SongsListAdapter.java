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
import com.example.musicapp.boundaries.Artist;
import com.example.musicapp.boundaries.Song;
import com.example.musicapp.screens.ShowSong;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.List;


public class SongsListAdapter extends BaseAdapter {
    final LayoutInflater inflater;
    final List<Song> songEntities;
    final Intent intent;
    private final Context context;

    public SongsListAdapter(Context context, List<Song> songEntities) {
        this.context = context;
        this.songEntities = songEntities;


        intent = new Intent(context, ShowSong.class);
//        this.intent.putExtra("user", gson.toJson(user));
        this.inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return songEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return this.songEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.activity_song_list_item, null);
        ImageView avatar = view.findViewById(R.id.songList_IMG_artist);
        TextView songName = view.findViewById(R.id.songList_TXT_songName);
        TextView artistName = view.findViewById(R.id.songList_TXT_artistName);
        songName.setText(this.songEntities.get(i).getName());

        artistName.setText(songEntities.get(i).getArtist().getName());
        Glide.with(this.context).load(songEntities.get(i).getArtist().getImageURL()).circleCrop().into(avatar);
        view.setOnClickListener(v -> {
            intent.putExtra("song", songEntities.get(i).getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        });


        return view;

    }
}
