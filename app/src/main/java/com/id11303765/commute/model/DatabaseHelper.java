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

    private SQLiteDatabase mTransportDatabase;
    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
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

    public int getRowCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT Count(*) FROM " + Constants.DATABASE_TABLE_STOP_TIME, null);
        c.moveToFirst();
        int i = c.getInt(0);
        c.close();
        return i;
    }

    public void populateDb(){
        readCSV(Constants.DATABASE_TABLE_AGENCY, "agency.txt");
        readCSV(Constants.DATABASE_TABLE_CALENDAR, "calendar.txt");
        readCSV(Constants.DATABASE_TABLE_ROUTE, "routes.txt");
        readCSV(Constants.DATABASE_TABLE_STOP, "stops.txt");
        readCSV(Constants.DATABASE_TABLE_TRIP, "trips.txt");
        readCSV(Constants.DATABASE_TABLE_STOP_TIME, "stop_times.txt");
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
            case Constants.DATABASE_TABLE_AGENCY:
                populateAgencyTable(db, buffer);
                break;
            case Constants.DATABASE_TABLE_CALENDAR:
                populateCalendarTable(db, buffer);
                break;
            case Constants.DATABASE_TABLE_ROUTE:
                populateRouteTable(db, buffer);
                break;
            case Constants.DATABASE_TABLE_STOP:
                populateStopTable(db, buffer);
                break;
            case Constants.DATABASE_TABLE_TRIP:
                populateTripTable(db, buffer);
                break;
            case Constants.DATABASE_TABLE_STOP_TIME:
                populateStopTimeTable(db, buffer);
                break;
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private void populateAgencyTable(SQLiteDatabase db, BufferedReader buffer) {
        String line = "";
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(Constants.CSV_SPLIT);
                if (columns.length != 6) {
                    Log.d(Constants.CSV_PARSER_LOG, "Skipping Bad CSV Row in Agency");
                    continue;
                }
                ContentValues cv = new ContentValues();
                cv.put(Constants.DATABASE_TABLE_AGENCY_ID, columns[0].trim());
                cv.put(Constants.DATABASE_TABLE_AGENCY_NAME, columns[1].trim());
                db.insert(Constants.DATABASE_TABLE_AGENCY, null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.CSV_PARSER_LOG, "Agencies Added" );
    }

    private void populateCalendarTable(SQLiteDatabase db, BufferedReader buffer) {
        String line = "";
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
        String line = "";
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(Constants.CSV_SPLIT);
                if (columns.length != 8) {
                    Log.d(Constants.CSV_PARSER_LOG, "Skipping Bad CSV Row in Route.");
                    continue;
                }
                ContentValues cv = new ContentValues();
                cv.put(Constants.DATABASE_TABLE_ROUTE_ID, columns[0].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_AGENCY_ID, columns[1].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_ROUTE_SHORT_NAME, columns[2].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_ROUTE_LONG_NAME, columns[3].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_ROUTE_DESC, columns[4].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_ROUTE_TYPE, Integer.parseInt(columns[5].replaceAll("\"", "").trim()));
                cv.put(Constants.DATABASE_TABLE_ROUTE_COLOR, columns[6].replaceAll("\"", "").trim());
                db.insert(Constants.DATABASE_TABLE_ROUTE, null, cv);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.CSV_PARSER_LOG, "Routes Added" );
    }


    private void populateStopTable(SQLiteDatabase db, BufferedReader buffer) {
        String line = "";
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(Constants.CSV_SPLIT);
                if (columns.length != 9) {
                    Log.d(Constants.CSV_PARSER_LOG, "Skipping Bad CSV Row in Stop.");
                    continue;
                }
                ContentValues cv = new ContentValues();
                cv.put(Constants.DATABASE_TABLE_STOP_ID, columns[0].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_STOP_CODE, columns[1].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_STOP_NAME, columns[2].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_STOP_LAT, Double.parseDouble(columns[3].replaceAll("\"", "").trim()));
                cv.put(Constants.DATABASE_TABLE_STOP_LON, Double.parseDouble(columns[4].replaceAll("\"", "").trim()));
                cv.put(Constants.DATABASE_TABLE_STOP_LOCATION_TYPE, columns[5].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_STOP_PARENT_STATION, columns[6].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_STOP_WHEELCHAIR_BOARDING, Integer.parseInt(columns[7].replaceAll("\"", "").trim()));
                cv.put(Constants.DATABASE_TABLE_STOP_PLATFORM_CODE, columns[8].replaceAll("\"", "").trim());
                db.insert(Constants.DATABASE_TABLE_STOP, null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.CSV_PARSER_LOG, "Stops Added" );
    }


    private void populateTripTable(SQLiteDatabase db, BufferedReader buffer) {
        String line = "";
        int i = 0;
        int l = 0;
        try {
            while ((line = buffer.readLine()) != null) {
                if (i >= 5000){
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    db.beginTransaction();
                    i=0;
                    l++;
                    Log.d("TRIP", "RESET l is: " + l);
                }
                String[] columns = line.split(Constants.CSV_SPLIT);
                if (columns.length != 8) {
                    Log.d("CSVParser", "Skipping Bad CSV Row in Trip.");
                    continue;
                }
                ContentValues cv = new ContentValues();
                cv.put(Constants.DATABASE_TABLE_ROUTE_ID, columns[0].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_SERVICE_ID, columns[1].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_TRIP_ID, columns[2].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_SHARPE_ID, columns[3].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_TRIP_HEADSIGN, columns[4].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_TRIP_DIRECTION_ID, Integer.parseInt(columns[5].replaceAll("\"", "").trim()));
                cv.put(Constants.DATABASE_TABLE_TRIP_BLOCK_ID, columns[6].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_TRIP_WHEELCHAIR_ACCESSIBLE, Integer.parseInt(columns[7].replaceAll("\"", "").trim()));
                db.insert(Constants.DATABASE_TABLE_TRIP, null, cv);

                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.CSV_PARSER_LOG, "Trips Added" );
    }


    private void populateStopTimeTable(SQLiteDatabase db, BufferedReader buffer) {
        String line = "";
        int i = 0;
        int l = 0;
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(Constants.CSV_SPLIT);
                if (i >= 5000){
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    db.beginTransaction();
                    i=0;
                    l++;
                    Log.d("STOPTIME", "RESET l is: " + l);
                }
                if (columns.length != 9) {
                    Log.d("CSVParser", "Skipping Bad CSV Row in Stop Time");
                    continue;
                }
                ContentValues cv = new ContentValues();
                cv.put(Constants.DATABASE_TABLE_TRIP_ID, columns[0].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_STOP_TIME_ARRIVAL_TIME, columns[1].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_STOP_TIME_DEPARTURE_TIME, columns[2].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_STOP_ID, columns[3].replaceAll("\"", "").trim());
                cv.put(Constants.DATABASE_TABLE_STOP_TIME_STOP_SEQUENCE, columns[4].replaceAll("\"", "").trim());
                db.insert(Constants.DATABASE_TABLE_STOP_TIME, null, cv);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(Constants.CSV_PARSER_LOG, "Stop Times Added" );
    }


    private void createAgencyTable() {
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_AGENCY);
        mTransportDatabase.execSQL("CREATE TABLE " + Constants.DATABASE_TABLE_AGENCY + "( " +
                Constants.DATABASE_TABLE_AGENCY_ID + " TEXT PRIMARY KEY," +
                Constants.DATABASE_TABLE_AGENCY_NAME + " TEXT NOT NULL);"
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
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_ROUTE);
        mTransportDatabase.execSQL("CREATE TABLE " + Constants.DATABASE_TABLE_ROUTE + "( " +
                Constants.DATABASE_TABLE_ROUTE_ID + " TEXT PRIMARY KEY," +
                Constants.DATABASE_TABLE_AGENCY_ID + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_ROUTE_SHORT_NAME + " TEXT," +
                Constants.DATABASE_TABLE_ROUTE_LONG_NAME + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_ROUTE_DESC + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_ROUTE_TYPE + " INTEGER NOT NULL," +
                Constants.DATABASE_TABLE_ROUTE_COLOR + " TEXT NOT NULL," +
                " FOREIGN KEY (" + Constants.DATABASE_TABLE_AGENCY_ID + ") REFERENCES " +
                Constants.DATABASE_TABLE_AGENCY + "(" + Constants.DATABASE_TABLE_AGENCY_ID + "));"
        );
    }


    private void createStopTable() {
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_STOP);
        mTransportDatabase.execSQL("CREATE TABLE " + Constants.DATABASE_TABLE_STOP + "( " +
                Constants.DATABASE_TABLE_STOP_ID + " TEXT PRIMARY KEY," +
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

    private void createTripTable() {
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_TRIP);
        mTransportDatabase.execSQL("CREATE TABLE " + Constants.DATABASE_TABLE_TRIP + "( " +
                Constants.DATABASE_TABLE_ROUTE_ID + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_SERVICE_ID + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_TRIP_ID + " TEXT PRIMARY KEY," +
                Constants.DATABASE_TABLE_SHARPE_ID + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_TRIP_HEADSIGN + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_TRIP_DIRECTION_ID + " INTEGER NOT NULL," +
                Constants.DATABASE_TABLE_TRIP_BLOCK_ID + " TEXT," +
                Constants.DATABASE_TABLE_TRIP_WHEELCHAIR_ACCESSIBLE + " INTEGER NOT NULL," +
                " FOREIGN KEY (" + Constants.DATABASE_TABLE_ROUTE_ID + ") REFERENCES " +
                Constants.DATABASE_TABLE_ROUTE + "(" + Constants.DATABASE_TABLE_ROUTE_ID + ")" +
                " FOREIGN KEY (" + Constants.DATABASE_TABLE_SERVICE_ID + ") REFERENCES " +
                Constants.DATABASE_TABLE_CALENDAR + "(" + Constants.DATABASE_TABLE_SERVICE_ID + ")" +
                ");"
        );
    }

    private void createStopTimesTable() {
        mTransportDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_STOP_TIME);
        mTransportDatabase.execSQL("CREATE TABLE " + Constants.DATABASE_TABLE_STOP_TIME + "( " +
                Constants.DATABASE_TABLE_TRIP_ID + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_STOP_TIME_ARRIVAL_TIME + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_STOP_TIME_DEPARTURE_TIME + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_STOP_ID + " TEXT NOT NULL," +
                Constants.DATABASE_TABLE_STOP_TIME_STOP_SEQUENCE + " TEXT NOT NULL," +
                " FOREIGN KEY (" + Constants.DATABASE_TABLE_TRIP_ID + ") REFERENCES " +
                Constants.DATABASE_TABLE_TRIP + "(" + Constants.DATABASE_TABLE_TRIP_ID + ")," +
                " FOREIGN KEY (" + Constants.DATABASE_TABLE_STOP_ID + ") REFERENCES " +
                Constants.DATABASE_TABLE_STOP + "(" + Constants.DATABASE_TABLE_STOP_ID + ")" +
                ");"
        );
    }
}
