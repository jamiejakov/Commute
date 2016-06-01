package com.id11303765.commute.model;


import java.util.ArrayList;

public class Journey {
    private long mTimeInMillis;
    private boolean mSpeed;
    private boolean mCost;
    private boolean mConvenience;
    private ArrayList<String> mStops;
    private ArrayList<Timetable> mTripTimetables;


    public Journey(long mTimeInMillis, boolean mSpeed, boolean mCost, boolean mConvenience, ArrayList<String> mStops, ArrayList<Timetable> mTripTimetables) {
        this.mTimeInMillis = mTimeInMillis;
        this.mSpeed = mSpeed;
        this.mCost = mCost;
        this.mConvenience = mConvenience;
        this.mStops = mStops;
        this.mTripTimetables = mTripTimetables;
    }

    public long getTimeInMillis() {
        return mTimeInMillis;
    }

    public boolean isFast() {
        return mSpeed;
    }

    public boolean isCheap() {
        return mCost;
    }

    public boolean isConvenient() {
        return mConvenience;
    }

    public ArrayList<Timetable> getTripTimetables() {
        return mTripTimetables;
    }

    public ArrayList<String> getStops() {
        return mStops;
    }
}
