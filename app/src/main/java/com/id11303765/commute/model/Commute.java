package com.id11303765.commute.model;


import java.util.ArrayList;

/**
 * POJO for a single commute
 */
public class Commute {
    private String mStartStop;
    private String mEndStop;
    private ArrayList<Timetable> mTripTimetables;

    Commute(String start, String end, ArrayList<Timetable> tripTimetables) {
        mStartStop = start;
        mEndStop = end;
        mTripTimetables = tripTimetables;
    }


    public String getStartStopShortName() {
        return mStartStop;
    }

    public String getEndStopShortName() {
        return mEndStop;
    }

    public ArrayList<Timetable> getTripTimetables() {
        return mTripTimetables;
    }

}
