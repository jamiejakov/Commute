package com.id11303765.commute.model;


import java.util.ArrayList;

public class Commute {
    private Stop mStartStop;
    private Stop mEndStop;
    private ArrayList<Timetable> mTripTimetables;

    public Commute(Stop start, Stop end, ArrayList<Timetable> tripTimetables) {
        mStartStop = start;
        mEndStop = end;
        mTripTimetables = tripTimetables;
    }


    public Stop getStartStop() {
        return mStartStop;
    }

    public Stop getEndStop() {
        return mEndStop;
    }

    public ArrayList<Timetable> getTripTimetables() {
        return mTripTimetables;
    }

    public void setStartStop(Stop mStartStop) {
        this.mStartStop = mStartStop;
    }

    public void setEndStop(Stop mEndStop) {
        this.mEndStop = mEndStop;
    }

    public void setTripTimetables(ArrayList<Timetable> mTripTimetables) {
        this.mTripTimetables = mTripTimetables;
    }
}
