package com.id11303765.commute.utils;

public class Constants {
    public static final int JOURNEY_FAB_TO_LIST_REQUEST = 10;
    public static final int JOURNEY_DEPARTURE_TO_SEARCH_REQUEST = 11;
    public static final int JOURNEY_DESTINATION_TO_SEARCH_REQUEST = 12;
    public static final int JOURNEY_OPTIONS_TO_ACTIVITY_REQUEST = 13;
    public static final int JOURNEY_ROUTE_LIST_TO_ROUTE_REQUEST = 14;
    public static final int JOURNEY_TIME_OPTIONS_TO_ACTIVITY_REQUEST = 15;
    public static final int JOURNEY_STOP_MAP_REQUEST = 16;
    public static final int SETTINGS_HOME_TO_SEARCH_REQUEST = 61;
    public static final int SETTINGS_WORK_TO_SEARCH_REQUEST = 62;
    public static final int WELCOME_TO_SETTINGS_REQUEST = 1;

    public static final String INTENT_SELECTED_STOP_NAME = "INTENT_SELECTED_STOP_NAME";
    public static final String INTENT_TIME_OPTION = "INTENT_TIME_OPTION";
    public static final String INTENT_TIME_DEPART_AT_BOOL = "INTENT_TIME_DEPART_AT_BOOL";
    public static final String INTENT_REQUEST = "INTENT_REQUEST";
    public static final String INTENT_SEARCH_EXCLUDE = "INTENT_SEARCH_EXCLUDE";
    public static final String INTENT_SEARCH_JOURNEY_START_STOP = "INTENT_SEARCH_JOURNEY_START_STOP";
    public static final String INTENT_SEARCH_JOURNEY_END_STOP = "INTENT_SEARCH_JOURNEY_END_STOP";
    public static final String INTENT_SETTINGS_BACK = "INTENT_SETTINGS_BACK";
    public static final String INTENT_JOURNEY_ROUTE = "INTENT_JOURNEY_ROUTE";
    public static final String INTENT_JOURNEY_ROUTE_NUMBER = "INTENT_JOURNEY_ROUTE_NUMBER";
    public static final String INTENT_JOURNEY_STOP_NAME = "INTENT_JOURNEY_STOP_NAME";
    public static final String INTENT_JOURNEY_STOP_LAT = "INTENT_JOURNEY_STOP_LAT";
    public static final String INTENT_JOURNEY_STOP_LON = "INTENT_JOURNEY_STOP_LON";

    public static final String COMMUTE_TO_OR_FROM_PREF = "COMMUTE_TO_OR_FROM_PREF";

    public static final String CSV_SPLIT = "\",\"";
    public static final String COMMA_SPLIT = "\",\"";

    public static final int OPAQUE = 255;
    public static final int DESELECTED = 160;
    public static final int ONE_MINUTE = 1000*60;

    public static final String TAG_ERROR_LOG = "TAG_ERROR_LOG";
    public static final String TAG_COMMON_FIND_TIMETABLE_NEXT_SET = "Next Timetable Time";
    public static final String TAG_COMMON_FIND_TIMETABLE_PREV_SET = "Prev Timetable Time";
    public static final String TAG_STOP_SEARCH = "onQueryTextChange ";
    public static final String TAG_ADD_TO_CALENDAR = "calendar description ";
    public static final String TAG_CSV_PARSER_LOG = "CSVParser";

    public static final String MORNING_PEAK_START = "07:00";
    public static final String MORNING_PEAK_END = "09:00";
    public static final String EVENING_PEAK_START = "16:00";
    public static final String EVENING_PEAK_END = "18:30";

    public static final String DATE_FORMAT_HH24_MM = "HH:mm";
    public static final String DATE_FORMAT_HH_MM_SPACE_AM = "hh:mm a";
    public static final String DATE_FORMAT_HH_MM_AM = "hh:mma";
    public static final String DATE_FORMAT_DAY_MONTH_YEAR_WEEKDAY = "dd MMM yyyy (EE)";
    public static final String DATE_FORMAT_HH_MM_AM_DAY_MONTH_YEAR_WEEKDAY = "hh:mma, dd MMM yyy (EE)";

    public static final String TIME_FORMAT_HR = "%2dhr ";
    public static final String TIME_FORMAT_MIN = "%02dmin ";
    public static final String TIME_FORMAT_SEC = "%02dsec";

    /*          DATABASE STUFF                */
    public static final String DATABASE_TABLE_CALENDAR = "calendar";
    public static final String DATABASE_TABLE_SERVICE_ID = "service_id";
    public static final String DATABASE_TABLE_CALENDAR_MONDAY = "monday";
    public static final String DATABASE_TABLE_CALENDAR_TUESDAY = "tuesday";
    public static final String DATABASE_TABLE_CALENDAR_WEDNESDAY = "wednesday";
    public static final String DATABASE_TABLE_CALENDAR_THURSDAY = "thursday";
    public static final String DATABASE_TABLE_CALENDAR_FRIDAY = "friday";
    public static final String DATABASE_TABLE_CALENDAR_SATURDAY = "saturday";
    public static final String DATABASE_TABLE_CALENDAR_SUNDAY = "sunday";

}
