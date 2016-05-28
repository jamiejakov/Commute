package com.id11303765.commute.model;

import android.database.Cursor;
import android.graphics.Color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class StopTimeManager {
    private static StopTimeManager ourInstance = new StopTimeManager();
    private static DatabaseHelper mDatabaseHelper;

    static final String KEY_TABLE = "stoptime";
    static final String KEY_ARRIVAL_TIME = "arrival_time";
    static final String KEY_DEPARTURE_TIME = "departure_time";
    static final String KEY_STOP_SEQUENCE = "stop_sequence";

    public static StopTimeManager getInstance() {
        return ourInstance;
    }

    private StopTimeManager() {
    }

    public static void setDatabaseHelper(DatabaseHelper dbHelper){
        mDatabaseHelper = dbHelper;
    }

    public static ArrayList<StopTime> getStopTimeForTrip(String id){
        try {
            return getStopTimes(TripManager.KEY_ID, id);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<StopTime> getStopTimeForStop(String id){
        try {
            return getStopTimes(StopManager.KEY_ID, id);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ArrayList<StopTime> getStopTimes(String type, String id) throws ParseException {
        Cursor cursor = mDatabaseHelper.getStopTimes(type, id);

        ArrayList<StopTime> stopTimes = new ArrayList<>();

        Trip trip = TripManager.getTrip(cursor.getString(cursor.getColumnIndex(TripManager.KEY_ID)));
        Stop stop = StopManager.getStop(cursor.getString(cursor.getColumnIndex(StopManager.KEY_ID)));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);

        if (cursor.moveToFirst()) {
            do {
                StopTime stopTime = new StopTime(trip,
                        simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_ARRIVAL_TIME))),
                        simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex(KEY_DEPARTURE_TIME))),
                        stop, Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_STOP_SEQUENCE)))
                );
                stopTimes.add(stopTime);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return stopTimes;
    }


}
