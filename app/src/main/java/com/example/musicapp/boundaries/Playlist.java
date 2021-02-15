package com.example.musicapp.boundaries;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    @DocumentId
    private String id;
    private String name;
    private String creatorId;
    @Exclude
    private List<Song> songs = new ArrayList<>();

    public Playlist() {
    }

    public String getCreatorId() {
        return creatorId;
    }

    public Playlist setCreatorId(String creatorId) {
        this.creatorId = creatorId;
        return this;
    }

    public String getId() {
        return id;
    }

    public Playlist setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Playlist setName(String name) {
        this.name = name;
        return this;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public Playlist setSongs(List<Song> songs) {
        this.songs = songs;
        return this;
    }
}
