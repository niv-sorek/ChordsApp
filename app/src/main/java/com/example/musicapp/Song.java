package com.example.musicapp;

import java.util.ArrayList;
import java.util.List;

public class Song {
    public String chords;
    private String name;
    private Artist artist;
    private List<Rank> ranks = new ArrayList<>();

    public Song() {
    }

    public Song(String name) {
        this.name = name;
    }

    public Song(String name, Artist artist) {
        this.name = name;
        this.artist = artist;

    }

    public List<Rank> getRanks() {
        return ranks;
    }

    public Song setRanks(ArrayList<Rank> ranks) {
        this.ranks = ranks;
        return this;
    }

    public String getChords() {
        return chords;
    }

    public Song setChords(String chords) {
        this.chords = chords;
        return this;
    }

    public void addRank(Rank rank) {
        this.ranks.add(rank);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public int getRank() {
        if (ranks.size() == 0) return 0;
        int sum = 0;
        for (Rank i : this.ranks) {
            sum += i.score;
        }
        return sum / ranks.size();
    }
}
