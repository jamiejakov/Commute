package com.id11303765.commute.model;


import android.database.Cursor;
import android.graphics.Color;

import com.id11303765.commute.utils.Constants;

import java.util.ArrayList;

/**
 * Manger singleton for the route POJO and the DB object
 */
public class RouteManager {
    private static RouteManager ourInstance = new RouteManager();
    private static DatabaseHelper mDatabaseHelper;
    private static ArrayList<Route> mRoutes;
    private static String[] mLineColors;

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
        mRoutes = new ArrayList<>();
    }

    /**
     * Get the route based on ID from memory or DB
     *
     * @param id to look on
     * @return route POJO
     */
    public static Route getRoute(String id) {
        Route route = findRoute(id);
        if (route == null) {
            Cursor cursor = mDatabaseHelper.getRoute(id);
            if (cursor.moveToFirst()) {
                route = makeRoute(cursor);
                if (route != null) {
                    mRoutes.add(route);
                }
            }
        }
        return route;
    }

    /**
     * Creates a route POJO based on the data from the DB cursor
     *
     * @param cursor get data from
     * @return a Route POJO
     */
    private static Route makeRoute(Cursor cursor) {
        Route route;
        Agency agency = AgencyManager.getAgency(cursor.getString(cursor.getColumnIndex(AgencyManager.KEY_ID)));
        int color = Color.parseColor("#" + cursor.getString(cursor.getColumnIndex(KEY_COLOR)));
        if (agency.getID().equals(Constants.SYDNEY_TRAINS_AGENCY)) {
            String name = cursor.getString(cursor.getColumnIndex(KEY_LONG_NAME));
            int line = Integer.parseInt(name.substring(1, 2));
            color = getLineColour(line);
        }
        route = new Route(cursor.getString(cursor.getColumnIndex(KEY_ID)), agency,
                cursor.getString(cursor.getColumnIndex(KEY_SHORT_NAME)),
                cursor.getString(cursor.getColumnIndex(KEY_LONG_NAME)),
                cursor.getString(cursor.getColumnIndex(KEY_DESC)),
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_TYPE))),
                color);
        cursor.close();
        return route;
    }

    /**
     * @param line the line number to get the colour for
     * @return correct colour based on number
     */
    private static int getLineColour(int line) {
        return Color.parseColor(mLineColors[line - 1]);
    }

    /**
     * Passes in the array of line colours form resources so that when creating a route, it can accurately set the colour of the service
     * for Sydney Trains only.
     *
     * @param lineColors array of colours
     */
    public static void setLineColors(String[] lineColors) {
        mLineColors = lineColors;
    }

    /**
     * Search local for routes based on it
     *
     * @param id to search by
     * @return the route from local or null
     */
    private static Route findRoute(String id) {
        for (Route r : mRoutes) {
            if (r.getID().equals(id)) {
                return r;
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
