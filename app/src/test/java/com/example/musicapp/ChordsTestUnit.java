package com.example.musicapp;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ChordsTestUnit {
    @Test
    public void CheckChordsAddRows() {

        String rawChords = "Hello {0,m}World_How ar{1,m}e you?_Merav Ash{2,Maj7}erian";
        List<String> strings = ChordsUtils.formatChordsString(rawChords, "_", 1);
        strings.forEach(System.out::println);

        assertEquals((rawChords.chars().filter(c -> c == '_').count() + 1) * 2, strings.size());
    }

    @Test
    public void TestFromUG() {
        String raw = "b    Bb/D      Cm         Fm           Eaug Ab/Eb   Dm7b5\n" + "Mama, ooh_________  didn't mean to make you  cry.\n" + "   Bb7                            Eb               Bb/D         Cm\n" + "If I'm not back again this time tomorrow, car - ry on, car - ry on\n" + "      Abm            Eb          Ab   Eb   Ebdim   Fm7   Bb\n" + "as if nothing really matters.";
      //  System.out.println(ChordsUtils.fromUltimateGuitar(raw));

    }
}