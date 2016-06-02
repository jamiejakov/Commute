package com.id11303765.commute.model;


import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;

public class JourneyManager {
    private static JourneyManager ourInstance = new JourneyManager();
    private static DatabaseHelper mDatabaseHelper;
    private static ArrayList<Journey> mJourneys;

    public static JourneyManager getInstance() {
        return ourInstance;
    }

    private JourneyManager() {
        mJourneys = new ArrayList<>();
    }

    public static Journey getJoureney(String startStopShortName, String endStopShortName, Date timeNow) {
        Journey journey = findJourney(startStopShortName, endStopShortName);
        if (journey == null) {

        }
        return journey;
    }


    private static Journey findJourney(String startShortName, String endShortName) {
        for (Journey journey : mJourneys) {
            //if (journey.getStartStopShortName().equals(startShortName) &&
            //        journey.getEndStopShortName().equals(endShortName)) {
            //    return journey;
            //}
        }
        return null;
    }

    private void findStartEndStops(String start, String end) {

    }

    private void findStartEndTimetables() {

    }

    public static void setDatabaseHelper(DatabaseHelper dbHelper) {
        mDatabaseHelper = dbHelper;
    }
}
