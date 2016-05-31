package com.id11303765.commute.model;


import android.database.Cursor;

import java.util.ArrayList;

public class CommuteManager {
    private static CommuteManager ourInstance = new CommuteManager();
    private static DatabaseHelper mDatabaseHelper;
    private static ArrayList<Commute> mCommutes;

    public static CommuteManager getInstance() {
        return ourInstance;
    }

    private CommuteManager() {
        mCommutes = new ArrayList<>();
    }

    public static Commute getCommute(String startStopShortName, String endStopShortName) {
        Commute commute = findCommute(startStopShortName, endStopShortName);
        int position = 1;
        if (commute == null) {
            Cursor cursor = mDatabaseHelper.getTripsContainingStops(startStopShortName, endStopShortName);
            if (cursor.moveToFirst()) {
                ArrayList<Timetable> tripTimetables = new ArrayList<>();
                ArrayList<Stop> stops = new ArrayList<>();
                do {
                    if (position == 1){
                        stops.addAll(StopManager.getStopsByName(startStopShortName));
                        stops.addAll(StopManager.getStopsByName(endStopShortName));
                    }
                    String tripID = cursor.getString(cursor.getColumnIndex(TripManager.KEY_ID));
                    tripTimetables.add(TimetableManager.getTimetable(TripManager.getTrip(tripID),stops));

                    position++;
                } while (cursor.moveToNext());

                commute = new Commute(startStopShortName, endStopShortName, tripTimetables);
            }
            cursor.close();
        }

        return commute;
    }

    private static Commute findCommute(String startShortName, String endShortName) {
        for (Commute commute : mCommutes) {
            if (commute.getStartStopShortName().equals(startShortName) &&
                    commute.getEndStopShortName().equals(endShortName)) {
                return commute;
            }
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
