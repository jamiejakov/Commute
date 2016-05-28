package com.id11303765.commute.model;


import android.database.Cursor;
import android.graphics.Color;

import java.util.ArrayList;

public class RouteManager {
    private static RouteManager ourInstance = new RouteManager();
    private static DatabaseHelper mDatabaseHelper;
    private static ArrayList<Route> mRoutes;

    static final String KEY_TABLE = "route";
    static final String KEY_ID = "route_id";
    static final String KEY_SHORT_NAME = "route_short_name";
    static final String KEY_LONG_NAME = "route_long_name";
    static final String KEY_DESC = "route_desc";
    static final String KEY_TYPE = "route_type";
    static final String KEY_COLOR = "route_color";

    public static RouteManager getInstance() {
        return ourInstance;
    }

    private RouteManager() {
    }

    public static Route getRoute(String id){
        Route route = findRoute(id);

        if (route == null){
            Cursor cursor = mDatabaseHelper.getRoute(id);
            Agency agency = AgencyManager.getAgency(cursor.getString(cursor.getColumnIndex(AgencyManager.KEY_ID)));
            route = new Route(cursor.getString(cursor.getColumnIndex(KEY_ID)), agency,
                    cursor.getString(cursor.getColumnIndex(KEY_SHORT_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_LONG_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_DESC)),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_TYPE))),
                    Color.parseColor(cursor.getString(cursor.getColumnIndex(KEY_COLOR))));
            cursor.close();
        }

        return route;
    }

    public static void setDatabaseHelper(DatabaseHelper dbHelper){
        mDatabaseHelper = dbHelper;
    }

    private static Route findRoute(String id){
        for(Route r : mRoutes){
            if (r.getID() == id){
                return r;
            }
        }
        return null;
    }
}
