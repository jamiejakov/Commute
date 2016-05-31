package com.id11303765.commute.model;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

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

    public static Agency getAgency(String id) {
        Agency agency = findAngency(id);
        if (agency == null){
            Cursor cursor = mDatabaseHelper.getAgency(id);
            if (cursor.moveToFirst()) {
                agency = new Agency(cursor.getString(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            }
            cursor.close();
        }
        return agency;
    }

    public static void setDatabaseHelper(DatabaseHelper dbHelper) {
        mDatabaseHelper = dbHelper;
    }

    private static Agency findAngency(String id) {
        for (Agency a : mAgencies) {
            if (a.getID() == id) {
                return a;
            }
        }
        return null;
    }
}
