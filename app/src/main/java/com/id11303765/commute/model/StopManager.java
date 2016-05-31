package com.id11303765.commute.model;

import android.database.Cursor;
import android.util.Log;

import com.id11303765.commute.utils.Constants;

import java.text.ParseException;
import java.util.ArrayList;

public class StopManager {
    private static StopManager ourInstance = new StopManager();
    private static DatabaseHelper mDatabaseHelper;
    private static ArrayList<Stop> mStops;

    static final String KEY_TABLE = "stop";
    static final String KEY_ID = "stop_id";
    static final String KEY_CODE = "stop_code";
    static final String KEY_NAME = "stop_name";
    static final String KEY_LAT = "stop_lat";
    static final String KEY_LON = "stop_lon";
    static final String KEY_LOCATION_TYPE = "stop_location_type";
    static final String KEY_PARENT_STATION = "stop_parent_station";
    static final String KEY_WHEELCHAIR_BOARDING = "stop_wheelchair_boarding";
    static final String KEY_PLATFORM_CODE = "stop_platform_code";

    public static StopManager getInstance() {
        return ourInstance;
    }

    private StopManager() {
        mStops = new ArrayList<>();
    }

    public static Stop getStopById(String id) {
        Stop stop = findStopById(id);

        if (stop == null) {
            getAllStops();
            stop = findStopById(id);
        }

        return stop;
    }

    public static ArrayList<Stop> getStopsByName(String name) {
        if (mStops.isEmpty()) {
            getAllStops();
        }
        ArrayList<Stop> list = new ArrayList<>();
        findStopByNameAndAddToList(name, list);
        if (list.size() != 0) {
            return list;
        }

        return mStops;
    }

    private static void getAllStops() {
        Cursor cursor = mDatabaseHelper.getAllStops();

        if (cursor.moveToFirst()) {
            do {
                mStops.add(makeStop(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private static Stop makeStop(Cursor cursor) {
        Stop stop = new Stop(cursor.getString(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_CODE)),
                cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LAT))),
                Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LON))),
                cursor.getString(cursor.getColumnIndex(KEY_PLATFORM_CODE)));
        return stop;
    }

    private static void findStopByNameAndAddToList(String name, ArrayList<Stop> list) {
        for (Stop s : mStops) {
            if (s.getShortName().toLowerCase().contains(name.toLowerCase())) {
                list.add(s);
            }
        }
    }

    private static Stop findStopById(String id) {
        for (Stop s : mStops) {
            if (s.getID().equals(id)) {
                return s;
            }
        }
        return null;
    }

    public static void setDatabaseHelper(DatabaseHelper dbHelper) {
        mDatabaseHelper = dbHelper;
    }
}
