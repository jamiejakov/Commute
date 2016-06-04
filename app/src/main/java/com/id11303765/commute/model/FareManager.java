package com.id11303765.commute.model;


import android.database.Cursor;
import java.util.ArrayList;
import java.util.Collections;

public class FareManager {
    private static FareManager ourInstance = new FareManager();
    private static ArrayList<Fare> mFares;
    private static DatabaseHelper mDatabaseHelper;

    static final String KEY_TABLE = "opal_fares";
    static final String KEY_DISTANCE = "opal_distance";
    static final String KEY_TYPE = "opal_type";
    static final String KEY_TRANSPORT = "opal_transport";
    static final String KEY_PEAK = "opal_peak";
    static final String KEY_VALUE = "opal_value";

    public static FareManager getInstance() {
        return ourInstance;
    }

    private FareManager() {
        mFares = new ArrayList<>();
    }

    public static void setDatabaseHelper(DatabaseHelper dbHelper) {
        mDatabaseHelper = dbHelper;
    }

    static Fare getFare(int type, int transport, boolean peak, float distance) {
        Fare fare = findFare(type, transport, peak, distance);

        if (fare == null) {
            makeAllFaresList();
            fare = findFare(type, transport, peak, distance);
        }

        return fare;
    }

    private static void makeAllFaresList() {
        Cursor cursor = mDatabaseHelper.getAllFares();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Fare fare;
                int distance = cursor.getInt(cursor.getColumnIndex(KEY_DISTANCE));
                int type = cursor.getInt(cursor.getColumnIndex(KEY_TYPE));
                int transport = cursor.getInt(cursor.getColumnIndex(KEY_TRANSPORT));
                boolean peak = cursor.getInt(cursor.getColumnIndex(KEY_PEAK)) == 1;
                double value = cursor.getDouble(cursor.getColumnIndex(KEY_VALUE));
                fare = new Fare(value, distance, type, transport, peak);
                mFares.add(fare);
            } while (cursor.moveToNext());

        }
    }

    private static Fare findFare(int type, int transport, boolean peak, float distance) {
        Fare fare = null;
        ArrayList<Fare> matchingFares = new ArrayList<>();
        for (Fare f : mFares) {
            if (type == f.getType() && transport == f.getTransport() && peak == f.isPeak()) {
                matchingFares.add(f);
            }
        }
        Collections.sort(matchingFares);
        for (int i = matchingFares.size() - 1; i >= 0; i--) {
            Fare f = matchingFares.get(i);
            if (i==matchingFares.size()-1 && distance > f.getDistance()){
                fare = f;
                break;
            }
            if (distance < f.getDistance()) {
                fare = f;
            }
        }
        return fare;
    }

}
