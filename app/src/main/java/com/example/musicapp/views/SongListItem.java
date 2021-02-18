package com.example.musicapp.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.musicapp.R;

public class SongListItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list_item);
    }

    public static class ListItem {
        private final String text;
        private final Object data;

        public Object getData() {
            return data;
        }

        public ListItem(String text, Object data) {
            this.text = text;
            this.data = data;
        }

        @NonNull
        @Override
        public String toString() {
            return this.text;
        }
    }

    public static class PlaylistItem extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_playlist_item);
        }
    }
}