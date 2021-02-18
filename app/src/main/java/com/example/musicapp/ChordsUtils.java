package com.example.musicapp;

import android.os.Build;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChordsUtils {
    public static List<String> formatChordsString(String rawString, String delimiter, int scale) {
        String[] splitted = rawString.split(delimiter);
        List<String> chordsList = new ArrayList<>();

        for (String s : splitted) {
            chordsList.add("");
            chordsList.add(s);
        }
        for (int line = 1; line < chordsList.size(); line = line + 2) {
            if (chordsList.get(line).indexOf('{') >= 0) {
            List<String> splitLine =
                    new ArrayList<>(Arrays.asList(chordsList.get(line).split("[{}]")));
                int firstChord = chordsList.get(line).indexOf('{') > 0 ? 1 : 0;
                for (int part = firstChord; part < splitLine.size(); part += 2) {
                    String chord = getChord(splitLine.get(part), scale);
                    int spaces = 0;
                    if (part > 0)
                        spaces = splitLine.get(part - 1).length();
                    chordsList.set(line - 1, chordsList.get(line - 1) + Strings.repeat(" ", spaces) + chord);
                    splitLine.remove(part);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    chordsList.set(line, String.join("", splitLine));
                }
            }

        }
        return chordsList;
    }

    private static String getChord(String part, int scale) {
        try {
            String[] split = part.split(",");

            String chordLetter = "" + (char) ('A' + ((scale + Integer.parseInt(split[0])) % 8));
            if (split.length >= 1)
                return chordLetter + split[1];
            return chordLetter;
        } catch (Exception e) {
            return part;
        }

    }
}
