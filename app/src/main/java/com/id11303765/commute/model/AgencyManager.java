package com.id11303765.commute.model;

import android.database.Cursor;

public class AgencyManager {
    private static AgencyManager ourInstance = new AgencyManager();
    private static DatabaseHelper mDatabaseHelper;

    static final String KEY_TABLE = "agency";
    static final String KEY_ID = "agency_id";
    static final String KEY_NAME = "agency_name";

    public static AgencyManager getInstance() {
        return ourInstance;
    }

    private AgencyManager() {

    }

    public static Agency getAgency(String id){
        Cursor cursor = mDatabaseHelper.getAgency(id);
        Agency agency = new Agency(cursor.getString(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        cursor.close();
        return agency;
    }

    public static void setDatabaseHelper(DatabaseHelper dbHelper){
        mDatabaseHelper = dbHelper;
    }
}
