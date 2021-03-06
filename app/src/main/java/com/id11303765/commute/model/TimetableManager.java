package com.id11303765.commute.model;

import android.database.Cursor;
import android.util.Log;

import com.id11303765.commute.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Manger singleton for the timetable POJO and data
 */
public class TimetableManager {
    private static TimetableManager ourInstance = new TimetableManager();
    private static DatabaseHelper mDatabaseHelper;
    private static ArrayList<Timetable> mTripTimetables;
    private static ArrayList<Timetable> mStopTimetables;
    private static ArrayList<Timetable> mSmallTripTimetables;

    static final String KEY_TABLE = "stoptime";
    static final String KEY_ARRIVAL_TIME = "arrival_time";
    static final String KEY_DEPARTURE_TIME = "departure_time";
    static final String KEY_STOP_SEQUENCE = "stop_sequence";
    static final String KEY_INDEX = "stop_time_trip_station_index";

    public static TimetableManager getInstance() {
        return ourInstance;
    }

    private TimetableManager() {
        mTripTimetables = new ArrayList<>();
        mStopTimetables = new ArrayList<>();
        mSmallTripTimetables = new ArrayList<>();
    }

    /**
     * Gets the timetable from local or DB based on the trip (trip ID)
     *
     * @param trip - trip to look for
     * @return the Timetable POJO
     */
    static Timetable getTimetable(Trip trip) {
        Timetable timetable = findTripTimetable(trip.getID());

        if (timetable == null) {
            Cursor cursor = mDatabaseHelper.getStopTimes(TripManager.KEY_ID, trip.getID());

            ArrayList<StopTime> stopTimes = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_HH24_MM_SS, Locale.US);

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
            Collections.sort(stopTimes);
            timetable = new Timetable(trip, stopTimes);
            mTripTimetables.add(timetable);
            Collections.sort(mTripTimetables);
        }

        return timetable;
    }

    /**
     * Get timetable from local or DB based on Stop
     * To be used in the Timetable module in V2 of the app
     *
     * @param stop to check by
     * @return the created Timetable
     */
    public static Timetable getTimetable(Stop stop) {
        Timetable timetable = findStopTimetable(stop.getID());

        if (timetable == null) {
            Cursor cursor = mDatabaseHelper.getStopTimes(StopManager.KEY_ID, stop.getID());

            ArrayList<StopTime> stopTimes = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_HH24_MM_SS, Locale.US);

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
            Collections.sort(stopTimes);
            timetable = new Timetable(stop, stopTimes);
            mStopTimetables.add(timetable);
            Collections.sort(mSmallTripTimetables);
        }

        return timetable;
    }

    /**
     * Gets the timetable from local or DB based on the Trip but must only have the StopTimes for the passed in Stops
     *
     * @param trip  to check by
     * @param stops to find StopTimes for
     * @return created Timetable
     */
    static Timetable getTimetable(Trip trip, ArrayList<Stop> stops) {
        Timetable timetable = findSmallTripTimetable(trip.getID());

        if (timetable == null) {
            Cursor cursor = mDatabaseHelper.getStopTimesForTripAndStop(trip.getID(), stops);

            ArrayList<StopTime> stopTimes = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_HH24_MM_SS, Locale.US);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    StopTime stopTime = null;
                    try {
                        Stop stop = StopManager.getStopById(cursor.getString(cursor.getColumnIndex(StopManager.KEY_ID)));
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
            Collections.sort(stopTimes);
            timetable = new Timetable(trip, stopTimes);
            mSmallTripTimetables.add(timetable);
            Collections.sort(mSmallTripTimetables);
        }

        return timetable;
    }

    /**
     * Find the timetable based on trip locally
     *
     * @param id to search for
     * @return the found timetable if exists
     */
    private static Timetable findTripTimetable(String id) {
        for (Timetable t : mTripTimetables) {
            if (t.getTrip().getID().equals(id)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Find the timetable based on stop locally
     *
     * @param id to search for
     * @return the found timetable if exists
     */
    private static Timetable findStopTimetable(String id) {
        for (Timetable t : mStopTimetables) {
            if (t.getStop().getID().equals(id)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Find the timetable based on trip but only with the smaller set StopTimes locally
     *
     * @param id to search for
     * @return the found timetable if exists
     */
    private static Timetable findSmallTripTimetable(String id) {
        for (Timetable t : mSmallTripTimetables) {
            if (t.getTrip().getID().equals(id)) {
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
