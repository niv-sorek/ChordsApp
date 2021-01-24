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
import com.example.musicapp.Song;
import com.example.musicapp.screens.ShowSong;
import com.google.gson.Gson;

import java.util.ArrayList;


public class SongsListAdapter extends BaseAdapter {
    LayoutInflater inflter;
    ArrayList<Song> songs;
    private final Context context;

    public SongsListAdapter(Context context, ArrayList<Song> songs) {
        this.context = context;
        this.songs = songs;
        this.inflter = (LayoutInflater.from(context));
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
        view = inflter.inflate(R.layout.activity_song_list_item, null);
        ImageView avatar = (ImageView) view.findViewById(R.id.songList_IMG_artist);
        TextView songName = (TextView) view.findViewById(R.id.songList_TXT_songName);
        TextView artistName = (TextView) view.findViewById(R.id.songList_TXT_artistName);
        songName.setText(songs.get(i).getName());
        artistName.setText(songs.get(i).getArtist().getName());
        Glide.with(this.context).load(songs.get(i).getArtist().getImagePath()).circleCrop().into(avatar);
        view.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowSong.class);
            Gson gson = new Gson();
            intent.putExtra("song", gson.toJson(songs.get(i)));
            intent.putExtra("artist", gson.toJson(songs.get(i).getArtist()));
            context.startActivity(intent);

        });
        return view;
    }
}
