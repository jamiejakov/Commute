package com.id11303765.commute.model;

import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TimetableManager {
    private static TimetableManager ourInstance = new TimetableManager();
    private static DatabaseHelper mDatabaseHelper;
    private static ArrayList<Timetable> mTripTimetables;
    private static ArrayList<Timetable> mStopTimetables;

    static final String KEY_TABLE = "stoptime";
    static final String KEY_ARRIVAL_TIME = "arrival_time";
    static final String KEY_DEPARTURE_TIME = "departure_time";
    static final String KEY_STOP_SEQUENCE = "stop_sequence";

    public static TimetableManager getInstance() {
        return ourInstance;
    }

    private TimetableManager() {
    }

    public static void setDatabaseHelper(DatabaseHelper dbHelper){
        mDatabaseHelper = dbHelper;
    }

    public static Timetable getTimetable(Trip trip){
        Timetable timetable = findTripTimetable(trip.getID());

        if (timetable == null){
            Cursor cursor = mDatabaseHelper.getStopTimes(TripManager.KEY_ID, trip.getID());

            ArrayList<StopTime> stopTimes = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);

            if (cursor.moveToFirst()) {
                do {
                    StopTime stopTime = null;
                    Stop stop = StopManager.getStopById(cursor.getString(cursor.getColumnIndex(StopManager.KEY_ID)));
                    try {
                        stopTime = new StopTime(trip,
                                simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_ARRIVAL_TIME))),
                                simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_DEPARTURE_TIME))),
                                stop, Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_STOP_SEQUENCE)))
                        );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    stopTimes.add(stopTime);
                } while (cursor.moveToNext());
            }
            cursor.close();
            timetable = new Timetable(trip, stopTimes);
        }

        return timetable;
    }

    public static Timetable getTimetable(Stop stop){
        Timetable timetable = findStopTimetable(stop.getID());

        if (timetable == null){
            Cursor cursor = mDatabaseHelper.getStopTimes(StopManager.KEY_ID, stop.getID());

            ArrayList<StopTime> stopTimes = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);

            if (cursor.moveToFirst()) {
                do {
                    StopTime stopTime = null;
                    Trip trip = TripManager.getTrip(cursor.getString(cursor.getColumnIndex(TripManager.KEY_ID)));
                    try {
                        stopTime = new StopTime(trip,
                                simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_ARRIVAL_TIME))),
                                simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_DEPARTURE_TIME))),
                                stop, Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_STOP_SEQUENCE)))
                        );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    stopTimes.add(stopTime);
                } while (cursor.moveToNext());
            }
            cursor.close();
            timetable = new Timetable(stop, stopTimes);
        }

        return timetable;
    }

    private static Timetable findTripTimetable(String id){
        for (Timetable t : mTripTimetables) {
            if (t.getTrip().getID() == id) {
                return t;
            }
        }
        return null;
    }

    private static Timetable findStopTimetable(String id){
        for (Timetable t : mStopTimetables) {
            if (t.getStop().getID() == id) {
                return t;
            }
        }
        return null;
    }


}