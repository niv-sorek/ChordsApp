package com.example.musicapp.models;

import com.example.musicapp.Utils;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@IgnoreExtraProperties
public class Song {
    private final List<User> likes = new ArrayList<>();
    private String chords;
    private String id;
    private String name;
    private Artist artist;
    private String artistId;
    private boolean rtl;

    public Song() {
    }

    public Song(String name) {
        this.name = name;
    }

    public Song(String name, boolean rtl) {
        this.name = name;
        this.rtl = rtl;
    }

    public List<User> getRanks() {
        return likes;
    }

    public void like(User user) {
        likes.add(user);
    }

    public String getChords() {
        return chords;
    }

    public Song setChords(String chords) {
        this.chords = chords;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return likes.size();
    }

    public boolean isUserLiked(User user) {
        for (User u : likes) {
            if (u.getUid().equals(user.getUid())) return true;
        }
        return false;
    }

    public boolean toggleLike(User user) {
        if (this.isUserLiked(user)) {
            this.likes.remove(user);
            return false;
        }
        this.like(user);
        return true;
    }

    public boolean isRtl() {
        return rtl;
    }

    public Song setRtl(boolean rtl) {
        this.rtl = rtl;
        return this;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("artistId", this.artist.getId());
        result.put("chords", this.chords);

        return result;
    }

    public String getArtistId() {
        return this.artistId;
    }

    public Song setArtistId(String artistId) {
        this.artistId = artistId;
        return this;
    }

    @Exclude
    public Artist getArtist() {
        return artist;
    }


    public Song setArtist(Artist artist) {
        this.artist = artist;
        this.artistId = this.artist.getId();
        return this;
    }

    public String getId() {
        if (id == null)
            this.id = Utils.makeId(this.artist.getName() + " " + this.name);
        return this.id;
    }

}
