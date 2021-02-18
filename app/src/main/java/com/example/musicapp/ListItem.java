package com.example.musicapp;

import androidx.annotation.NonNull;

public class ListItem {
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
