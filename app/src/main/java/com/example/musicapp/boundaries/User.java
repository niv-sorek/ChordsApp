package com.example.musicapp.boundaries;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    @Exclude
    public ArrayList<User> friends;
    private String name;
    private String phone;
    @Exclude
    private List<Song> likedSongEntities = new ArrayList<>();
    @Exclude
    private List<Playlist> playlists = new ArrayList<>();
    private ArrayList<String> instruments;
    private String uid;

    public User() {
        this.playlists.clear();
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public User setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
        return this;
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public User setFriends(ArrayList<User> friends) {
        this.friends = friends;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public List<Song> getLikedSongs() {
        return likedSongEntities;
    }

    public User setLikedSongs(List<Song> likedSongEntities) {
        this.likedSongEntities = likedSongEntities;
        return this;
    }

    public ArrayList<String> getInstruments() {
        return instruments;
    }

    public User setInstruments(ArrayList<String> instruments) {
        this.instruments = instruments;
        return this;
    }


    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("phone", phone);
        map.put("instruments", instruments);
        //map.put("likedSongs", likedSongs);
        return map;

    }
}
