package com.id11303765.commute.model;


import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;

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
        ArrayList<Stop> startStops = StopManager.getStopsByName(startStopShortName, true);
        ArrayList<Stop> endStops = StopManager.getStopsByName(endStopShortName, true);
        ArrayList<Stop> allStops = new ArrayList<>();
        allStops.addAll(startStops);
        allStops.addAll(endStops);

        if (commute == null) {
            Cursor cursor = mDatabaseHelper.getTripsContainingStops(startStops, endStops);
            if (cursor != null && cursor.moveToFirst()) {
                ArrayList<Timetable> tripTimetables = new ArrayList<>();
                do {
                    String tripID = cursor.getString(cursor.getColumnIndex(TripManager.KEY_ID));
                    Trip trip = TripManager.getTrip(tripID);
                    if (trip.getCalendar()[Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1]){
                        tripTimetables.add(TimetableManager.getTimetable(trip, allStops));
                    }
                } while (cursor.moveToNext());

                commute = new Commute(startStopShortName, endStopShortName, tripTimetables);
                cursor.close();
            }
            mCommutes.add(commute);
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

    public static void setDatabaseHelper(DatabaseHelper dbHelper) {
        mDatabaseHelper = dbHelper;
    }
}
