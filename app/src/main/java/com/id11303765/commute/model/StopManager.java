package com.id11303765.commute.model;

import android.database.Cursor;
import android.util.Log;

import com.id11303765.commute.utils.Constants;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Manger singleton for the stop time POJO and the DB object
 */
public class StopManager {
    private static StopManager ourInstance = new StopManager();
    private static DatabaseHelper mDatabaseHelper;
    private static ArrayList<Stop> mStops;

    static final String KEY_TABLE = "stop";
    static final String KEY_ID = "stop_id";
    static final String KEY_NAME = "stop_name";
    static final String KEY_LAT = "stop_lat";
    static final String KEY_LON = "stop_lon";
    static final String KEY_WHEELCHAIR_BOARDING = "stop_wheelchair_boarding";
    static final String KEY_PLATFORM_CODE = "stop_platform_code";
    static final String KEY_INDEX = "stop_name_id_index";

    public static StopManager getInstance() {
        return ourInstance;
    }

    private StopManager() {
        mStops = new ArrayList<>();
    }

    static Stop getStopById(String id) {
        Stop stop = findStopById(id);

        if (stop == null) {
            getAllStops();
            stop = findStopById(id);
        }

        return stop;
    }

    public static ArrayList<Stop> getStopsByName(String name, boolean checkSpace) {
        if (mStops.isEmpty()) {
            getAllStops();
        }
        ArrayList<Stop> list = findStopByNameAndAddToList(name, checkSpace);

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

    /**
     * Create a Stop object from a cursor
     * @param cursor
     * @return
     */
    private static Stop makeStop(Cursor cursor) {
        return new Stop(cursor.getString(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LAT))),
                Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LON))),
                cursor.getString(cursor.getColumnIndex(KEY_PLATFORM_CODE)));
    }

    /**
     * Find stops matching criteria in local list and return a list with them
     * @param name of stop to check for
     * @param checkSpace - check for whether the string can be the second word in the string or not
     */
    private static ArrayList<Stop> findStopByNameAndAddToList(String name, boolean checkSpace) {
        ArrayList<Stop> list = new ArrayList<>();
        for (Stop s : mStops) {
            if (checkSpace){
                if (s.getShortName().toLowerCase().contains(name.toLowerCase()) &&
                        !s.getShortName().toLowerCase().contains(" " + name.toLowerCase())){
                    list.add(s);
                }
            }
            else{
                if (s.getShortName().toLowerCase().contains(name.toLowerCase())){
                    list.add(s);
                }
            }
        }
        return list;
    }

    /**
     * Find stop in local list and return it if found
     * @param id - id to search stop by
     * @return stop if found
     */
    private static Stop findStopById(String id) {
        for (Stop s : mStops) {
            if (s.getID().equals(id)) {
                return s;
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
