package com.id11303765.commute.model;

import android.database.Cursor;
import android.graphics.Color;

import com.id11303765.commute.utils.Constants;

import java.util.ArrayList;

public class TripManager {
    private static TripManager ourInstance = new TripManager();
    private static DatabaseHelper mDatabaseHelper;
    private static ArrayList<Trip> mTrips;

    static final String KEY_TABLE = "trip";
    static final String KEY_ID = "trip_id";
    static final String KEY_SHARPE_ID = "shape_id";
    static final String KEY_HEADSIGN = "trip_headsign";
    static final String KEY_DIRECTION_ID = "direction_id";
    static final String KEY_BLOCK_ID = "block_id";
    static final String KEY_WHEELCHAIR_ACCESSIBLE = "wheelchair_accessible";

    public static TripManager getInstance() {
        return ourInstance;
    }

    private TripManager() {
    }

    public static void setDatabaseHelper(DatabaseHelper dbHelper){
        mDatabaseHelper = dbHelper;
    }

    public static Trip getTrip(String id){
        Trip trip = findTrip(id);

        if (trip == null){
            Cursor cursor = mDatabaseHelper.getTrip(id);
            Route route = RouteManager.getRoute(cursor.getString(cursor.getColumnIndex(RouteManager.KEY_ID)));
            Cursor calCurs = mDatabaseHelper.getCalendar(cursor.getString(cursor.getColumnIndex(Constants.DATABASE_TABLE_SERVICE_ID)));

            boolean[] calendar = {
                    convertToBoolean(calCurs.getString(calCurs.getColumnIndex(Constants.DATABASE_TABLE_CALENDAR_MONDAY))),
                    convertToBoolean(calCurs.getString(calCurs.getColumnIndex(Constants.DATABASE_TABLE_CALENDAR_TUESDAY))),
                    convertToBoolean(calCurs.getString(calCurs.getColumnIndex(Constants.DATABASE_TABLE_CALENDAR_WEDNESDAY))),
                    convertToBoolean(calCurs.getString(calCurs.getColumnIndex(Constants.DATABASE_TABLE_CALENDAR_THURSDAY))),
                    convertToBoolean(calCurs.getString(calCurs.getColumnIndex(Constants.DATABASE_TABLE_CALENDAR_FRIDAY))),
                    convertToBoolean(calCurs.getString(calCurs.getColumnIndex(Constants.DATABASE_TABLE_CALENDAR_SATURDAY))),
                    convertToBoolean(calCurs.getString(calCurs.getColumnIndex(Constants.DATABASE_TABLE_CALENDAR_SUNDAY)))
            };


            trip = new Trip(route, calendar,
                    cursor.getString(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_HEADSIGN)),
                    cursor.getString(cursor.getColumnIndex(KEY_DIRECTION_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_BLOCK_ID)),
                    convertToBoolean(cursor.getString(cursor.getColumnIndex(KEY_WHEELCHAIR_ACCESSIBLE)))
            );
            calCurs.close();
            cursor.close();
        }

        return trip;
    }

    private static Trip findTrip(String id){
        for(Trip t : mTrips){
            if (t.getID() == id){
                return t;
            }
        }
        return null;
    }

    private static boolean convertToBoolean(String value) {
        boolean returnValue = false;
        if ("1".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) ||
                "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value))
            returnValue = true;
        return returnValue;
    }
}
