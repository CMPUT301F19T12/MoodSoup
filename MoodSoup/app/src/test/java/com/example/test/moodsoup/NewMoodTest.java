package com.example.test.moodsoup;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author dchen
 */

public class NewMoodTest {
    NewMood cNewMood = new NewMood();

    @Test
    public void getDateStringTest() {
        String correctDate = "2019-01-01";

        // The month in the generatedDate is 0 because it goes with a 0-11 system
        String generatedDate = cNewMood.getDateString(2019, 0, 1);

        assertEquals(correctDate, generatedDate);
    }

    @Test
    public void getTimeStringTest() {
        String correctTime = "11:11:11";
        String generatedTime = cNewMood.getTimeString(11,11,11);

        assertEquals(correctTime, generatedTime);
    }
}
