package com.example.musicapp;

public class Rank {
    public int score;
    public User user;

    public Rank(int score, User user) {
        this.score = score;
        this.user = user;
    }

    public Rank() {
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
