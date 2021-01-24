package com.example.musicapp;

import java.util.ArrayList;

public class Artist {
    private ArrayList<Song> songs = new ArrayList<Song>();
    private String name;
    private String imagePath;

    public Artist(String name) {
        this.name = name;
    }

    public Artist() {
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public Artist setSongs(ArrayList<Song> songs) {
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

    public String getImagePath() {
        return imagePath;
    }

    public Artist setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public void addSong(Song song) {
        this.songs.add(song);
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

}
