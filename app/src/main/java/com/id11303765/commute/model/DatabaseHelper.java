package com.id11303765.commute.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.id11303765.commute.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * SQL query class for accessing the DataBase
 * Should I put exception handling here?
 * Maybe
 * But I think I gotta catch them all
 * and be the very best
 * the best there ever was
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "transport.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase mTransportDatabase;
    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
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

    /*-------------- GET POJOs --------------*/

    public Cursor getAgency(String id){
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = new String[]{AgencyManager.KEY_ID, AgencyManager.KEY_NAME};
        String selection = AgencyManager.KEY_ID + " = " + id;
        Cursor cursor = db.query(AgencyManager.KEY_TABLE, columns, selection, null, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        db.close();
        return cursor;
    }

    public Cursor getCalendar(String id){
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = new String[]{Constants.DATABASE_TABLE_CALENDAR_MONDAY,
                Constants.DATABASE_TABLE_CALENDAR_TUESDAY,
                Constants.DATABASE_TABLE_CALENDAR_WEDNESDAY,
                Constants.DATABASE_TABLE_CALENDAR_THURSDAY,
                Constants.DATABASE_TABLE_CALENDAR_FRIDAY,
                Constants.DATABASE_TABLE_CALENDAR_SATURDAY,
                Constants.DATABASE_TABLE_CALENDAR_SUNDAY};
        String selection = Constants.DATABASE_TABLE_SERVICE_ID + " = " + id;
        Cursor cursor = db.query(Constants.DATABASE_TABLE_CALENDAR, columns, selection, null, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        db.close();
        return cursor;
    }

    public Cursor getStopById(String id){
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = new String[]{StopManager.KEY_ID, StopManager.KEY_CODE, StopManager.KEY_NAME,
        StopManager.KEY_LAT, StopManager.KEY_LON, StopManager.KEY_PLATFORM_CODE};
        String selection = StopManager.KEY_ID + " = " + id;
        Cursor cursor = db.query(StopManager.KEY_TABLE, columns, selection, null, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        db.close();
        return cursor;
    }

    public Cursor getAllStops(){
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = new String[]{StopManager.KEY_ID, StopManager.KEY_CODE, StopManager.KEY_NAME,
                StopManager.KEY_LAT, StopManager.KEY_LON, StopManager.KEY_PLATFORM_CODE};
        Cursor cursor = db.query(StopManager.KEY_TABLE, columns, null, null, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        db.close();
        return cursor;
    }

    public Cursor getRoute(String id){
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = new String[]{RouteManager.KEY_ID, AgencyManager.KEY_ID, RouteManager.KEY_SHORT_NAME, RouteManager.KEY_LONG_NAME,
                RouteManager.KEY_DESC, RouteManager.KEY_TYPE, RouteManager.KEY_COLOR};
        String selection = RouteManager.KEY_ID + " = " + id;
        Cursor cursor = db.query(RouteManager.KEY_TABLE, columns, selection, null, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        db.close();
        return cursor;
    }

    public Cursor getTrip(String id){
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = new String[]{RouteManager.KEY_ID, Constants.DATABASE_TABLE_SERVICE_ID, TripManager.KEY_ID, TripManager.KEY_HEADSIGN,
                TripManager.KEY_DIRECTION_ID, TripManager.KEY_BLOCK_ID, TripManager.KEY_WHEELCHAIR_ACCESSIBLE};
        String selection = TripManager.KEY_ID + " = " + id;
        Cursor cursor = db.query(TripManager.KEY_TABLE, columns, selection, null, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        db.close();
        return cursor;
    }

    public Cursor getStopTimes(String idType, String id){
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = new String[]{TripManager.KEY_ID, TimetableManager.KEY_ARRIVAL_TIME, TimetableManager.KEY_DEPARTURE_TIME,
                StopManager.KEY_ID, TimetableManager.KEY_STOP_SEQUENCE};
        String selection = idType + " = " + id;
        Cursor cursor = db.query(TimetableManager.KEY_TABLE, columns, selection, null, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        db.close();
        return cursor;
    }

    /*-------------- POPULATE TABLES --------------*/

    public void populateMainTables(){
        readCSV(AgencyManager.KEY_TABLE, "agency.txt");
        readCSV(Constants.DATABASE_TABLE_CALENDAR, "calendar.txt");
        readCSV(RouteManager.KEY_TABLE, "routes.txt");
        readCSV(StopManager.KEY_TABLE, "stops.txt");
        readCSV(TripManager.KEY_TABLE, "trips.txt");

    }

    public void populateStopTimesTable(){
        readCSV(TimetableManager.KEY_TABLE, "stop_times.txt");
    }

    private void readCSV(String table, String file) {
        SQLiteDatabase db = getReadableDatabase();
        AssetManager manager = mContext.getAssets();
        InputStream inStream = null;
        try {
            inStream = manager.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        db.beginTransaction();

        switch (table) {
            case AgencyManager.KEY_TABLE:
                populateAgencyTable(db, buffer);
                break;
            case Constants.DATABASE_TABLE_CALENDAR:
                populateCalendarTable(db, buffer);
                break;
            case RouteManager.KEY_TABLE:
                populateRouteTable(db, buffer);
                break;
            case StopManager.KEY_TABLE:
                populateStopTable(db, buffer);
                break;
            case TripManager.KEY_TABLE:
                populateTripTable(db, buffer);
                break;
            case TimetableManager.KEY_TABLE:
                populateStopTimeTable(db, buffer);
                break;
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    private void populateAgencyTable(SQLiteDatabase db, BufferedReader buffer) {
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(Constants.CSV_SPLIT);
                if (columns.length != 6) {
                    Log.d(Constants.CSV_PARSER_LOG, "Skipping Bad CSV Row in Agency");
                    continue;
                }
                ContentValues cv = new ContentValues();
                cv.put(AgencyManager.KEY_ID, columns[0].trim());
                cv.put(AgencyManager.KEY_NAME, columns[1].trim());
                db.insert(AgencyManager.KEY_TABLE, null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.CSV_PARSER_LOG, "Agencies Added" );
    }

    private void populateCalendarTable(SQLiteDatabase db, BufferedReader buffer) {
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(Constants.CSV_SPLIT);
                if (columns.length != 10) {
                    Log.d(Constants.CSV_PARSER_LOG, "Skipping Bad CSV Row in Calendar");
                    continue;
                }
                ContentValues cv = new ContentValues();
                cv.put(Constants.DATABASE_TABLE_SERVICE_ID, columns[0].trim());
                cv.put(Constants.DATABASE_TABLE_CALENDAR_MONDAY, Integer.parseInt(columns[1].replaceAll("\"", "").trim()));
                cv.put(Constants.DATABASE_TABLE_CALENDAR_TUESDAY, Integer.parseInt(columns[2].replaceAll("\"", "").trim()));
                cv.put(Constants.DATABASE_TABLE_CALENDAR_WEDNESDAY, Integer.parseInt(columns[3].replaceAll("\"", "").trim()));
                cv.put(Constants.DATABASE_TABLE_CALENDAR_THURSDAY, Integer.parseInt(columns[4].replaceAll("\"", "").trim()));
                cv.put(Constants.DATABASE_TABLE_CALENDAR_FRIDAY, Integer.parseInt(columns[5].replaceAll("\"", "").trim()));
                cv.put(Constants.DATABASE_TABLE_CALENDAR_SATURDAY, Integer.parseInt(columns[6].replaceAll("\"", "").trim()));
                cv.put(Constants.DATABASE_TABLE_CALENDAR_SUNDAY, Integer.parseInt(columns[7].replaceAll("\"", "").trim()));
                db.insert(Constants.DATABASE_TABLE_CALENDAR, null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.CSV_PARSER_LOG, "Calendars Added" );
    }

    private void populateRouteTable(SQLiteDatabase db, BufferedReader buffer) {
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(Constants.CSV_SPLIT);
                if (columns.length != 8) {
                    Log.d(Constants.CSV_PARSER_LOG, "Skipping Bad CSV Row in Route.");
                    continue;
                }
                ContentValues cv = new ContentValues();
                cv.put(RouteManager.KEY_ID, columns[0].replaceAll("\"", "").trim());
                cv.put(AgencyManager.KEY_ID, columns[1].replaceAll("\"", "").trim());
                cv.put(RouteManager.KEY_SHORT_NAME, columns[2].replaceAll("\"", "").trim());
                cv.put(RouteManager.KEY_LONG_NAME, columns[3].replaceAll("\"", "").trim());
                cv.put(RouteManager.KEY_DESC, columns[4].replaceAll("\"", "").trim());
                cv.put(RouteManager.KEY_TYPE, Integer.parseInt(columns[5].replaceAll("\"", "").trim()));
                cv.put(RouteManager.KEY_COLOR, columns[6].replaceAll("\"", "").trim());
                db.insert(RouteManager.KEY_TABLE, null, cv);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.CSV_PARSER_LOG, "Routes Added" );
    }

    private void populateStopTable(SQLiteDatabase db, BufferedReader buffer) {
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(Constants.CSV_SPLIT);
                if (columns.length != 9) {
                    Log.d(Constants.CSV_PARSER_LOG, "Skipping Bad CSV Row in Stop.");
                    continue;
                }
                ContentValues cv = new ContentValues();
                cv.put(StopManager.KEY_ID, columns[0].replaceAll("\"", "").trim());
                cv.put(StopManager.KEY_CODE, columns[1].replaceAll("\"", "").trim());
                cv.put(StopManager.KEY_NAME, columns[2].replaceAll("\"", "").trim());
                cv.put(StopManager.KEY_LAT, Double.parseDouble(columns[3].replaceAll("\"", "").trim()));
                cv.put(StopManager.KEY_LON, Double.parseDouble(columns[4].replaceAll("\"", "").trim()));
                cv.put(StopManager.KEY_LOCATION_TYPE, columns[5].replaceAll("\"", "").trim());
                cv.put(StopManager.KEY_PARENT_STATION, columns[6].replaceAll("\"", "").trim());
                cv.put(StopManager.KEY_WHEELCHAIR_BOARDING, Integer.parseInt(columns[7].replaceAll("\"", "").trim()));
                cv.put(StopManager.KEY_PLATFORM_CODE, columns[8].replaceAll("\"", "").trim());
                db.insert(StopManager.KEY_TABLE, null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.CSV_PARSER_LOG, "Stops Added" );
    }

    private void populateTripTable(SQLiteDatabase db, BufferedReader buffer) {
        String line;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(Constants.CSV_SPLIT);
                if (columns.length != 8) {
                    Log.d("CSVParser", "Skipping Bad CSV Row in Trip.");
                    continue;
                }
                ContentValues cv = new ContentValues();
                cv.put(RouteManager.KEY_ID, columns[0].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_SERVICE_ID, columns[1].replaceAll("\"", "").trim());
                cv.put(TripManager.KEY_ID, columns[2].replaceAll("\"", "").trim());
                cv.put(TripManager.KEY_SHARPE_ID, columns[3].replaceAll("\"", "").trim());
                cv.put(TripManager.KEY_HEADSIGN, columns[4].replaceAll("\"", "").trim());
                cv.put(TripManager.KEY_DIRECTION_ID, Integer.parseInt(columns[5].replaceAll("\"", "").trim()));
                cv.put(TripManager.KEY_BLOCK_ID, columns[6].replaceAll("\"", "").trim());
                cv.put(TripManager.KEY_WHEELCHAIR_ACCESSIBLE, Integer.parseInt(columns[7].replaceAll("\"", "").trim()));
                db.insert(TripManager.KEY_TABLE, null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.CSV_PARSER_LOG, "Trips Added" );
    }

    private void populateStopTimeTable(SQLiteDatabase db, BufferedReader buffer) {
        String line;
        int i = 0;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(Constants.CSV_SPLIT);
                if (i >= 5000){
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    db.beginTransaction();
                    i=0;
                }
                if (columns.length != 9) {
                    Log.d("CSVParser", "Skipping Bad CSV Row in Stop Time");
                    continue;
                }
                ContentValues cv = new ContentValues();
                cv.put(TripManager.KEY_ID, columns[0].replaceAll("\"", "").trim());
                cv.put(TimetableManager.KEY_ARRIVAL_TIME, columns[1].replaceAll("\"", "").trim());
                cv.put(TimetableManager.KEY_DEPARTURE_TIME, columns[2].replaceAll("\"", "").trim());
                cv.put(StopManager.KEY_ID, columns[3].replaceAll("\"", "").trim());
                cv.put(TimetableManager.KEY_STOP_SEQUENCE, Integer.parseInt(columns[4].replaceAll("\"", "").trim()));
                db.insert(TimetableManager.KEY_TABLE, null, cv);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.CSV_PARSER_LOG, "Stop Times Added" );
    }

    /*-------------- CREATE TABLES --------------*/

    private void createAgencyTable() {
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + AgencyManager.KEY_TABLE);
        mTransportDatabase.execSQL("CREATE TABLE " + AgencyManager.KEY_TABLE + "( " +
                AgencyManager.KEY_ID + " TEXT PRIMARY KEY," +
                AgencyManager.KEY_NAME + " TEXT NOT NULL);"
        );
    }

    private void createCalendarTable() {
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_CALENDAR);
        mTransportDatabase.execSQL("CREATE TABLE " + Constants.DATABASE_TABLE_CALENDAR + "( " +
                Constants.DATABASE_TABLE_SERVICE_ID + " TEXT PRIMARY KEY," +
                Constants.DATABASE_TABLE_CALENDAR_MONDAY + " INTEGER NOT NULL," +
                Constants.DATABASE_TABLE_CALENDAR_TUESDAY + " INTEGER NOT NULL," +
                Constants.DATABASE_TABLE_CALENDAR_WEDNESDAY + " INTEGER NOT NULL," +
                Constants.DATABASE_TABLE_CALENDAR_THURSDAY + " INTEGER NOT NULL," +
                Constants.DATABASE_TABLE_CALENDAR_FRIDAY + " INTEGER NOT NULL," +
                Constants.DATABASE_TABLE_CALENDAR_SATURDAY + " INTEGER NOT NULL," +
                Constants.DATABASE_TABLE_CALENDAR_SUNDAY + " INTEGER NOT NULL);"
        );
    }

    private void createRouteTable() {
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + RouteManager.KEY_TABLE);
        mTransportDatabase.execSQL("CREATE TABLE " + RouteManager.KEY_TABLE + "( " +
                RouteManager.KEY_ID + " TEXT PRIMARY KEY," +
                AgencyManager.KEY_ID + " TEXT NOT NULL," +
                RouteManager.KEY_SHORT_NAME + " TEXT," +
                RouteManager.KEY_LONG_NAME + " TEXT NOT NULL," +
                RouteManager.KEY_DESC + " TEXT NOT NULL," +
                RouteManager.KEY_TYPE + " INTEGER NOT NULL," +
                RouteManager.KEY_COLOR + " TEXT NOT NULL," +
                " FOREIGN KEY (" + AgencyManager.KEY_ID + ") REFERENCES " +
                AgencyManager.KEY_TABLE + "(" + AgencyManager.KEY_ID + "));"
        );
    }

    private void createStopTable() {
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + StopManager.KEY_TABLE);
        mTransportDatabase.execSQL("CREATE TABLE " + StopManager.KEY_TABLE + "( " +
                StopManager.KEY_ID + " TEXT PRIMARY KEY," +
                StopManager.KEY_CODE + " TEXT," +
                StopManager.KEY_NAME + " TEXT NOT NULL," +
                StopManager.KEY_LAT + " DOUBLE NOT NULL," +
                StopManager.KEY_LON + " DOUBLE NOT NULL," +
                StopManager.KEY_LOCATION_TYPE + " TEXT," +
                StopManager.KEY_PARENT_STATION + " TEXT," +
                StopManager.KEY_WHEELCHAIR_BOARDING + " INTEGER NOT NULL," +
                StopManager.KEY_PLATFORM_CODE + " TEXT);"
        );
    }

    private void createTripTable() {
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + TripManager.KEY_TABLE);
        mTransportDatabase.execSQL("CREATE TABLE " + TripManager.KEY_TABLE + "( " +
                RouteManager.KEY_ID + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_SERVICE_ID + " TEXT NOT NULL," +
                TripManager.KEY_ID + " TEXT PRIMARY KEY," +
                TripManager.KEY_SHARPE_ID + " TEXT NOT NULL," +
                TripManager.KEY_HEADSIGN + " TEXT NOT NULL," +
                TripManager.KEY_DIRECTION_ID + " INTEGER NOT NULL," +
                TripManager.KEY_BLOCK_ID + " TEXT," +
                TripManager.KEY_WHEELCHAIR_ACCESSIBLE + " INTEGER NOT NULL," +
                " FOREIGN KEY (" + RouteManager.KEY_ID + ") REFERENCES " +
                RouteManager.KEY_TABLE + "(" + RouteManager.KEY_ID + ")" +
                " FOREIGN KEY (" + Constants.DATABASE_TABLE_SERVICE_ID + ") REFERENCES " +
                Constants.DATABASE_TABLE_CALENDAR + "(" + Constants.DATABASE_TABLE_SERVICE_ID + ")" +
                ");"
        );
    }

    private void createStopTimesTable() {
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + TimetableManager.KEY_TABLE);
        mTransportDatabase.execSQL("CREATE TABLE " + TimetableManager.KEY_TABLE + "( " +
                TripManager.KEY_ID + " TEXT NOT NULL," +
                TimetableManager.KEY_ARRIVAL_TIME + " TEXT NOT NULL," +
                TimetableManager.KEY_DEPARTURE_TIME + " TEXT NOT NULL," +
                StopManager.KEY_ID + " TEXT NOT NULL," +
                TimetableManager.KEY_STOP_SEQUENCE + " INTEGER NOT NULL," +
                " FOREIGN KEY (" + TripManager.KEY_ID + ") REFERENCES " +
                TripManager.KEY_TABLE + "(" + TripManager.KEY_ID + ")," +
                " FOREIGN KEY (" + StopManager.KEY_ID + ") REFERENCES " +
                StopManager.KEY_TABLE + "(" + StopManager.KEY_ID + ")" +
                ");"
        );
    }
}