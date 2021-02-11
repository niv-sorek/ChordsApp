package com.example.musicapp.models;

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
    private List<Song> likedSongs = new ArrayList<>();
    @Exclude

    private ArrayList<Instrument> instruments;
    private String uid;

    public User() {
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
        return  likedSongs;
    }

    public User setLikedSongs(List<Song> likedSongs) {
        this.likedSongs = likedSongs;
        return this;
    }

    public ArrayList<Instrument> getInstruments() {
        return instruments;
    }

    public User setInstruments(ArrayList<Instrument> instruments) {
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
