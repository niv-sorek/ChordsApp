package com.example.musicapp.views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.musicapp.R;
import com.example.musicapp.boundaries.Artist;
import com.example.musicapp.boundaries.Playlist;
import com.example.musicapp.screens.ShowPlaylist;
import com.example.musicapp.screens.ShowSong;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.List;


public class PlaylistListAdapter extends BaseAdapter {
    final LayoutInflater inflater;
    final List<Playlist> playlists;
    final Gson gson = new Gson();
    private final Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    Artist a;

    public PlaylistListAdapter(Context context, List<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
        this.inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return playlists.size();
    }

    @Override
    public Playlist getItem(int position) {
        return this.playlists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.activity_playlist_item, null);
        TextView playlists_TXT_name = (TextView) view.findViewById(R.id.playlist_TXT_name);
        TextView playlist_TXT_count = (TextView) view.findViewById(R.id.playlist_TXT_count);

        playlist_TXT_count.setText("" + getItem(i).getSongs().size());
        playlists_TXT_name.setText(getItem(i).getName());

        view.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowPlaylist.class);
            intent.putExtra("playlist", new Gson().toJson(getItem(i)));
            context.startActivity(intent);
        });

        return view;

    }
}
