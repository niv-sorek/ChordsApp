package com.example.musicapp.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;


@IgnoreExtraProperties
public class Song {
    private List<String> likes = new ArrayList<>();
    private String chords;
    @DocumentId
    private String id;
    private String name;
    private Artist artist;
    private String artistId;
    private boolean rtl;

    public Song() {
    }

    public Song(String name, boolean rtl) {
        this.name = name;
        this.rtl = rtl;
    }

    public List<String> getLikes() {
        return likes;
    }

    public Song setLikes(List<String> likes) {
        this.likes = likes;
        return this;
    }

    public void like(User user) {
        likes.add(user.getUid());
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

    public Song setName(String name) {
        this.name = name;
        return this;
    }

    public int getRank() {
        return likes.size();
    }

    public boolean isUserLiked(User user) {
        for (String u : likes) {
            if (u.equals(user.getUid())) return true;
        }
        return false;
    }

    public boolean isUserLiked(String userId) {
        for (String u : likes) {
            if (u.equals(userId)) return true;
        }
        return false;
    }

    public boolean toggleLike(User user) {
        return this.toggleLike(user.getUid());
    }

    public boolean toggleLike(String userId) {
        if (this.isUserLiked(userId)) {
            this.likes.remove(userId);
            return false;
        }
        this.likes.add(userId);
        return true;
    }

    public boolean isRtl() {
        return rtl;
    }

    public Song setRtl(boolean rtl) {
        this.rtl = rtl;
        return this;
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
        return this.id;
    }
}
