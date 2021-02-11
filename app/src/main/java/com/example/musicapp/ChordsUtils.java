package com.example.musicapp;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChordsUtils {
    public static List<String> formatChordsString(String rawString, String delimiter, int scale) {
        String[] splitted = rawString.split(delimiter);
        //Arrays.stream(splitted).forEach(s -> System.out.println(s));
        List<String> chordsList = new ArrayList<>();
        //chordsList.add(0, "");

        for (int i = 0; i < splitted.length; i++) {
            chordsList.add("");
            chordsList.add(splitted[i]);
        }
        for (int line = 1; line < chordsList.size(); line = line + 2) {
            if (chordsList.get(line).indexOf('{') >= 0) {
            List<String> splitLine = new ArrayList<String>(Arrays.asList(chordsList.get(line).split("[{}]")));
                int firstChord = chordsList.get(line).indexOf('{') > 0 ? 1 : 0;
                for (int part = firstChord; part < splitLine.size(); part += 2) {
                    String chord = getChord(splitLine.get(part), scale);
                    int spaces = 0;
                    if (part > 0)
                        spaces = splitLine.get(part - 1).length();
                    chordsList.set(line - 1, chordsList.get(line - 1) + Strings.repeat(" ", spaces) + chord);
                    splitLine.remove(part);
                }
            chordsList.set(line, splitLine.stream().collect(Collectors.joining("")));
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
