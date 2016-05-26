package com.id11303765.commute.utils;

public class Constants {
    public static final int JOURNEY_FAB_TO_LIST_REQUEST = 10;
    public static final int JOURNEY_DEPARTURE_TO_SEARCH_REQUEST = 11;
    public static final int JOURNEY_DESTINATION_TO_SEARCH_REQUEST = 12;
    public static final int JOURNEY_OPTIONS_TO_ACTIVITY_REQUEST = 13;
    public static final int SETTINGS_HOME_TO_SEARCH_REQUEST = 61;
    public static final int SETTINGS_WORK_TO_SEARCH_REQUEST = 62;
    public static final String SEARCHTAG = "DEBUGGING SEARCH";



    /*          DATABASE STUFF                */

    public static final String DATABASE_NAME = "transport.db";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_TABLE_TRIP = "trip";
    //RouteID
    //ServiceID
    public static final String DATABASE_TABLE_TRIP_ID = "trip_id";
    public static final String DATABASE_TABLE_SHARPE_ID = "shape_id";
    public static final String DATABASE_TABLE_TRIP_HEADSIGN = "trip_headsign";
    public static final String DATABASE_TABLE_TRIP_DIRECTION_ID = "direction_id";
    public static final String DATABASE_TABLE_TRIP_BLOCK_ID = "block_id";
    public static final String DATABASE_TABLE_TRIP_WHEELCHAIR_ACCESSIBLE = "wheelchair_accessible";


    public static final String DATABASE_TABLE_STOP = "stop";
    public static final String DATABASE_TABLE_STOP_ID = "stop_id";
    public static final String DATABASE_TABLE_STOP_CODE = "stop_code";
    public static final String DATABASE_TABLE_STOP_NAME = "stop_name";
    public static final String DATABASE_TABLE_STOP_LAT = "stop_lat";
    public static final String DATABASE_TABLE_STOP_LON = "stop_lon";
    public static final String DATABASE_TABLE_STOP_LOCATION_TYPE = "stop_location_type";
    public static final String DATABASE_TABLE_STOP_PARENT_STATION = "stop_parent_station";
    public static final String DATABASE_TABLE_STOP_WHEELCHAIR_BOARDING = "stop_wheelchair_boarding";
    public static final String DATABASE_TABLE_STOP_PLATFORM_CODE = "stop_platform_code";

    public static final String DATABASE_TABLE_STOP_TIME = "stoptime";
    //tripID
    public static final String DATABASE_TABLE_STOP_TIME_ARRIVAL_TIME = "arrival_time";
    public static final String DATABASE_TABLE_STOP_TIME_DEPARTURE_TIME = "departure_time";
    //stopID
    public static final String DATABASE_TABLE_STOP_TIME_STOP_SEQUENCE = "stop_sequence";

    public static final String DATABASE_TABLE_ROUTE = "route";
    public static final String DATABASE_TABLE_ROUTE_ID = "route_id";
    // AGENCY ID
    public static final String DATABASE_TABLE_ROUTE_SHORT_NAME = "route_short_name";
    public static final String DATABASE_TABLE_ROUTE_LONG_NAME = "route_long_name";
    public static final String DATABASE_TABLE_ROUTE_DESC = "route_desc";
    public static final String DATABASE_TABLE_ROUTE_TYPE = "route_type";
    public static final String DATABASE_TABLE_ROUTE_COLOR = "route_color";

    public static final String DATABASE_TABLE_CALENDAR = "calendar";
    public static final String DATABASE_TABLE_SERVICE_ID = "service_id";
    public static final String DATABASE_TABLE_CALENDAR_MONDAY = "monday";
    public static final String DATABASE_TABLE_CALENDAR_TUESDAY = "tuesday";
    public static final String DATABASE_TABLE_CALENDAR_WEDNESDAY = "wednesday";
    public static final String DATABASE_TABLE_CALENDAR_THURSDAY = "thursday";
    public static final String DATABASE_TABLE_CALENDAR_FRIDAY = "friday";
    public static final String DATABASE_TABLE_CALENDAR_SATURDAY = "saturday";
    public static final String DATABASE_TABLE_CALENDAR_SUNDAY = "sunday";

    public static final String DATABASE_TABLE_AGENCY = "agency";
    public static final String DATABASE_TABLE_AGENCY_ID = "agency_id";
    public static final String DATABASE_TABLE_AGENCY_NAME = "agency_name";

}
