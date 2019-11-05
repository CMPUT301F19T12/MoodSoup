package com.example.test.moodsoup;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class StringDateComparator implements Comparator<Mood> {
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public int compare(Mood o1, Mood o2) {
        try {
            return dateFormat.parse(o2.getDate()).compareTo(dateFormat.parse(o1.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
