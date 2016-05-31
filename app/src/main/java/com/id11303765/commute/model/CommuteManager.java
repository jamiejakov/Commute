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
        ArrayList<Stop> startStops = StopManager.getStopsByName(startStopShortName);
        ArrayList<Stop> endStops = StopManager.getStopsByName(endStopShortName);
        ArrayList<Stop> allStops = new ArrayList<>();
        allStops.addAll(startStops);
        allStops.addAll(endStops);

        if (commute == null) {
            Cursor cursor = mDatabaseHelper.getTripsContainingStops(startStops, endStops);
            if (cursor.moveToFirst()) {
                ArrayList<Timetable> tripTimetables = new ArrayList<>();
                do {
                    String tripID = cursor.getString(cursor.getColumnIndex(TripManager.KEY_ID));
                    tripTimetables.add(TimetableManager.getTimetable(TripManager.getTrip(tripID),allStops));
                } while (cursor.moveToNext());

                commute = new Commute(startStopShortName, endStopShortName, tripTimetables);
            }
            cursor.close();
            mCommutes.add(commute);
        }

        return commute;
    }

    private static ArrayList<String> getStopIds(ArrayList<Stop> stops){
        ArrayList<String> ids = new ArrayList<>();
        for(Stop s : stops){
            ids.add(s.getID());
        }
        return ids;
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
