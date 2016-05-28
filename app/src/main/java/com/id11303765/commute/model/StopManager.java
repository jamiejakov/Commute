package com.id11303765.commute.model;

import android.database.Cursor;

public class StopManager {
    private static StopManager ourInstance = new StopManager();
    private static DatabaseHelper mDatabaseHelper;

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
    }

    public static Stop getStop(String id){
        Cursor cursor = mDatabaseHelper.getStop(id);
        Stop stop = new Stop(cursor.getString(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_CODE)),
                cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LAT))),
                Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LON))),
                cursor.getString(cursor.getColumnIndex(KEY_PARENT_STATION)),
                cursor.getString(cursor.getColumnIndex(KEY_PLATFORM_CODE)));
        cursor.close();
        return stop;
    }

    public static void setDatabaseHelper(DatabaseHelper dbHelper){
        mDatabaseHelper = dbHelper;
    }
}
