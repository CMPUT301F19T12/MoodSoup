package com.example.test.moodsoup;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * StringDateComparator
 * V1.0
 *
 * The function is used to compare moods and sort it by date and time
 *
 * @author smayer
 */
public class StringDateComparator implements Comparator<Mood> {
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    public int compare(Mood o1, Mood o2) {
        try {
            return dateFormat.parse(o2.getDate() + " " + o2.getTime()).compareTo(dateFormat.parse(o1.getDate() + " " + o1.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
