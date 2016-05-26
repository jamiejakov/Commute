package com.id11303765.commute.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.id11303765.commute.utils.Constants;

/**
 * SQL query class for accessing the DataBase
 * Should I put exception handling here?
 * Maybe
 * But I think I gotta catch them all
 * and be the very best
 * the best there ever was
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private SQLiteDatabase mTransportDatabase;

    public DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mTransportDatabase = db;
        createAgencyTable();
        createCalendarTable();
        createRouteTable();
        createStopTable();
        createTripTable();
        createStopTimesTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    private void createAgencyTable(){
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_AGENCY);
        mTransportDatabase.execSQL("CREATE TABLE " +
                Constants.DATABASE_TABLE_AGENCY_ID + "( TEXT PRIMARY KEY," +
                Constants.DATABASE_TABLE_AGENCY_NAME + " TEXT NOT NULL);"
        );
    }

    private void createCalendarTable(){
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_CALENDAR);
        mTransportDatabase.execSQL("CREATE TABLE " +
                Constants.DATABASE_TABLE_SERVICE_ID + "( TEXT PRIMARY KEY," +
                Constants.DATABASE_TABLE_CALENDAR_MONDAY + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_CALENDAR_TUESDAY + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_CALENDAR_WEDNESDAY + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_CALENDAR_THURSDAY + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_CALENDAR_FRIDAY + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_CALENDAR_SATURDAY + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_CALENDAR_SUNDAY + " TEXT NOT NULL);"
        );
    }

    private void createRouteTable(){
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_ROUTE);
        mTransportDatabase.execSQL("CREATE TABLE " +
                Constants.DATABASE_TABLE_ROUTE_ID + "( TEXT PRIMARY KEY," +
                Constants.DATABASE_TABLE_AGENCY_ID + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_ROUTE_SHORT_NAME + " TEXT," +
                Constants.DATABASE_TABLE_ROUTE_LONG_NAME + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_ROUTE_DESC + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_ROUTE_TYPE + " INTEGER NOT NULL," +
                Constants.DATABASE_TABLE_ROUTE_COLOR + " TEXT NOT NULL," +
                " FOREIGN KEY ("+Constants.DATABASE_TABLE_AGENCY_ID+") REFERENCES "+
                Constants.DATABASE_TABLE_AGENCY+"("+Constants.DATABASE_TABLE_AGENCY_ID+"));"
        );
    }


    private void createStopTable(){
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_STOP);
        mTransportDatabase.execSQL("CREATE TABLE " +
                Constants.DATABASE_TABLE_STOP_ID + "( TEXT PRIMARY KEY," +
                Constants.DATABASE_TABLE_STOP_CODE + " TEXT," +
                Constants.DATABASE_TABLE_STOP_NAME + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_STOP_LAT + " DOUBLE NOT NULL," +
                Constants.DATABASE_TABLE_STOP_LON + " DOUBLE NOT NULL," +
                Constants.DATABASE_TABLE_STOP_LOCATION_TYPE + " TEXT," +
                Constants.DATABASE_TABLE_STOP_PARENT_STATION + " TEXT," +
                Constants.DATABASE_TABLE_STOP_WHEELCHAIR_BOARDING + " INTEGER NOT NULL," +
                Constants.DATABASE_TABLE_STOP_PLATFORM_CODE + " TEXT);"
        );
    }
    private void createTripTable(){
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_TRIP);
        mTransportDatabase.execSQL("CREATE TABLE " +
                Constants.DATABASE_TABLE_ROUTE_ID + "( TEXT NOT NULL," +
                Constants.DATABASE_TABLE_SERVICE_ID + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_TRIP_ID + " TEXT PRIMARY KEY," +
                Constants.DATABASE_TABLE_SHARPE_ID + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_TRIP_HEADSIGN + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_TRIP_DIRECTION_ID + " INTEGER NOT NULL," +
                Constants.DATABASE_TABLE_TRIP_BLOCK_ID + " TEXT," +
                Constants.DATABASE_TABLE_TRIP_WHEELCHAIR_ACCESSIBLE + " INTEGER NOT NULL," +
                " FOREIGN KEY ("+Constants.DATABASE_TABLE_ROUTE_ID+") REFERENCES " +
                Constants.DATABASE_TABLE_ROUTE+"("+Constants.DATABASE_TABLE_ROUTE_ID +")" +
                " FOREIGN KEY ("+Constants.DATABASE_TABLE_SERVICE_ID+") REFERENCES " +
                Constants.DATABASE_TABLE_CALENDAR+"("+Constants.DATABASE_TABLE_SERVICE_ID +")" +
                ");"
        );
    }

    private void createStopTimesTable() {
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_STOP_TIME);
        mTransportDatabase.execSQL("CREATE TABLE " +
                Constants.DATABASE_TABLE_TRIP_ID + "( TEXT NOT NULL," +
                Constants.DATABASE_TABLE_STOP_TIME_ARRIVAL_TIME + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_STOP_TIME_DEPARTURE_TIME + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_STOP_ID + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_STOP_TIME_STOP_SEQUENCE + " TEXT NOT NULL" +
                " FOREIGN KEY ("+Constants.DATABASE_TABLE_TRIP_ID+") REFERENCES " +
                Constants.DATABASE_TABLE_TRIP+"("+Constants.DATABASE_TABLE_TRIP_ID +")" +
                " FOREIGN KEY ("+Constants.DATABASE_TABLE_STOP_ID+") REFERENCES " +
                Constants.DATABASE_TABLE_STOP+"("+Constants.DATABASE_TABLE_STOP_ID +")" +
                ");"
        );
    }


}
