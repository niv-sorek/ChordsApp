package com.example.musicapp.models;

import com.example.musicapp.Utils;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Artist {

    private String id = "";
    @Exclude
    private transient List<Song> songs = new ArrayList<>();
    private String name;
    private String imageURL;

    public Artist(String name, String imageURL) {
        this.name = name;
        this.imageURL = imageURL;
        this.id = Utils.makeId(name);
    }

    public Artist() {

    }

    public String getImageURL() {
        return imageURL;
    }

    public Artist setImageURL(String imageURL) {
        this.imageURL = imageURL;
        return this;
    }

    @Exclude
    public List<Song> getSongs() {
        return songs;
    }

    public Artist setSongs(List<Song> songs) {
        this.songs = songs;
        return this;
    }

    public String getName() {
        return name;
    }

    public Artist setName(String name) {
        this.name = name;
        return this;
    }

    public Artist addSong(Song song) {
        this.songs.add(song);
        song.setArtist(this);
        return this;
    }

    public int getRank() {

        if (songs.size() == 0) return 0;
        int sum = 0;
        for (Song s :
                this.songs) {
            sum += s.getRank();
        }
        return sum / songs.size();
    }

    public String getId() {
        return this.id;
    }

    public Artist setId(String id) {
        this.id = id;
        return this;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("imageURL", imageURL);
        //result.put("songs", songs);
        return result;
    }
}
