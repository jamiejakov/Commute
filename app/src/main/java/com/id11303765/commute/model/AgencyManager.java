package com.id11303765.commute.model;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Manger singleton for the agency POJO and DB table
 */
public class AgencyManager {
    private static AgencyManager ourInstance = new AgencyManager();
    private static DatabaseHelper mDatabaseHelper;
    private static ArrayList<Agency> mAgencies;

    static final String KEY_TABLE = "agency";
    static final String KEY_ID = "agency_id";
    static final String KEY_NAME = "agency_name";

    public static AgencyManager getInstance() {
        return ourInstance;
    }

    private AgencyManager() {
        mAgencies = new ArrayList<>();
    }

    /**
     * @param id - search parameter
     * @return - POJO
     */
    public static Agency getAgency(String id) {
        Agency agency = findAgency(id);
        if (agency == null) {
            Cursor cursor = mDatabaseHelper.getAgency(id);
            if (cursor.moveToFirst()) {
                agency = new Agency(cursor.getString(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            }
            cursor.close();
        }
        return agency;
    }

    /**
     * Set the db helper to use for queries
     *
     * @param dbHelper -
     */
    public static void setDatabaseHelper(DatabaseHelper dbHelper) {
        mDatabaseHelper = dbHelper;
    }

    private static Agency findAgency(String id) {
        for (Agency a : mAgencies) {
            if (a.getID().equals(id)) {
                return a;
            }
        }
        return null;
    }
}
