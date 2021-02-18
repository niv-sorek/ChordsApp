package com.example.musicapp;

import android.os.Build;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChordsUtils {
    static String[] chordsLetters = {"A", "Bb", "B", "C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab"};

    public static List<String> formatChordsString(String rawString, String delimiter, int scale) {
        String[] splitted = rawString.split(delimiter);
        List<String> chordsList = new ArrayList<>();

        for (String s : splitted) {
            chordsList.add("");
            chordsList.add(s);
        }
        for (int line = 1; line < chordsList.size(); line = line + 2) {
            if (chordsList.get(line).indexOf('{') >= 0) {
                List<String> splitLine = new ArrayList<>(Arrays.asList(chordsList.get(line).split("[{}]")));
                int firstChord = chordsList.get(line).indexOf('{') > 0 ? 1 : 0;
                for (int part = firstChord; part < splitLine.size(); part++) {
                    if (splitLine.get(part).charAt(0) == '#') {
                        String chord = getChord(splitLine.get(part), scale);
                        int spaces = 0;
                        if (part > 0) spaces = splitLine.get(part - 1).length();
                        chordsList.set(line - 1, chordsList.get(line - 1) + Strings.repeat(" ", spaces) + chord);
                        splitLine.remove(part);
                    }
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
            part = part.replaceAll("#", "");
            part = part.replaceAll(" ", "");
            String baseChord;
            String chordSign = "";
            if (part.indexOf(',') > 0) {
                String[] split;
                split = part.split(",");
                baseChord = split[0];
                chordSign = split[1];
            } else baseChord = part;
            int index = (scale + Integer.parseInt(baseChord)) % 12;
            if (index < 0) index += 12;

            return "" + chordsLetters[index] + chordSign;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return part;
        }
    }

//    public static String fromUltimateGuitar(String s) {
//        String converted = "";
//
//        String[] split = s.split("\n");
//        for (int i = 0; i < split.length - 1; i ++) {
//            int spaces = 0;
//            String chord = "";
//            for (int j = 0; j < split[i].length(); j++) {
//                if (split[i].charAt(j) == ' ') {
//                    if (chord.length() > 0) {
//                        split[i + 1] = String.valueOf(new StringBuilder().insert(spaces, "{#" + chord + "}"));
//                    }
//                    spaces++;
//                } else {
//                    chord += split[i].charAt(j);
//
//                }
//            }
//        }
//        for (String s1 : split)
//            converted = converted + "_" + s1;
//        return converted;
//    }
}
