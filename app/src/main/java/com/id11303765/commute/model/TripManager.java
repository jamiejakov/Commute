package com.id11303765.commute.model;

import android.database.Cursor;
import android.graphics.Color;

import com.id11303765.commute.utils.Common;
import com.id11303765.commute.utils.Constants;

import java.util.ArrayList;

/**
 * Manger singleton for the trip POJO and the DB object
 */
public class TripManager {
    private static TripManager ourInstance = new TripManager();
    private static DatabaseHelper mDatabaseHelper;
    private static ArrayList<Trip> mTrips;

    static final String KEY_TABLE = "trip";
    static final String KEY_ID = "trip_id";
    static final String KEY_HEADSIGN = "trip_headsign";
    static final String KEY_INDEX = "trip_id_index";

    static final String KEY_CAL_TABLE = "calendar";
    static final String KEY_CAL_SERVICE_ID = "service_id";
    static final String KEY_CAL_MONDAY = "monday";
    static final String KEY_CAL_TUESDAY = "tuesday";
    static final String KEY_CAL_WEDNESDAY = "wednesday";
    static final String KEY_CAL_THURSDAY = "thursday";
    static final String KEY_CAL_FRIDAY = "friday";
    static final String KEY_CAL_SATURDAY = "saturday";
    static final String KEY_CAL_SUNDAY = "sunday";

    public static TripManager getInstance() {
        return ourInstance;
    }

    private TripManager() {
        mTrips = new ArrayList<>();
    }

    /**
     * Gets the trip based on the ID passed
     * if not in memory, query the db and then save as a POJO in memory
     *
     * @param id - id to check
     * @return trip POJO
     */
    static Trip getTrip(String id) {
        Trip trip = findTrip(id);

        if (trip == null) {
            Cursor cursor = mDatabaseHelper.getTrip(id);
            trip = makeTrip(cursor);
            if (trip != null) {
                mTrips.add(trip);
            }
        }
        return trip;
    }

    /**
     * Makes a trip object from the cursor
     *
     * @param cursor - db cursor to get data from
     * @return a created trip
     */
    private static Trip makeTrip(Cursor cursor) {
        Trip trip = null;
        Cursor calCurs = mDatabaseHelper.getCalendar(cursor.getString(cursor.getColumnIndex(KEY_CAL_SERVICE_ID)));
        Route route = RouteManager.getRoute(cursor.getString(cursor.getColumnIndex(RouteManager.KEY_ID)));

        if (cursor.moveToFirst() && calCurs.moveToFirst()) {
            boolean[] calendar = {
                    Common.convertToBoolean(calCurs.getString(calCurs.getColumnIndex(KEY_CAL_SUNDAY))),
                    Common.convertToBoolean(calCurs.getString(calCurs.getColumnIndex(KEY_CAL_MONDAY))),
                    Common.convertToBoolean(calCurs.getString(calCurs.getColumnIndex(KEY_CAL_TUESDAY))),
                    Common.convertToBoolean(calCurs.getString(calCurs.getColumnIndex(KEY_CAL_WEDNESDAY))),
                    Common.convertToBoolean(calCurs.getString(calCurs.getColumnIndex(KEY_CAL_THURSDAY))),
                    Common.convertToBoolean(calCurs.getString(calCurs.getColumnIndex(KEY_CAL_FRIDAY))),
                    Common.convertToBoolean(calCurs.getString(calCurs.getColumnIndex(KEY_CAL_SATURDAY)))
            };

            trip = new Trip(route, calendar,
                    cursor.getString(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_HEADSIGN))
            );
            calCurs.close();
            cursor.close();
        }
        return trip;
    }

    /**
     * find trip in local list and return it if found
     *
     * @param id - id to check
     * @return trip pojo from memory
     */
    private static Trip findTrip(String id) {
        for (Trip t : mTrips) {
            if (t.getID().equals(id)) {
                return t;
            }
        }
        return null;
    }


    /**
     * Set the db helper to use for queries
     *
     * @param dbHelper -
     */
    public static void setDatabaseHelper(DatabaseHelper dbHelper) {
        mDatabaseHelper = dbHelper;
    }
}
