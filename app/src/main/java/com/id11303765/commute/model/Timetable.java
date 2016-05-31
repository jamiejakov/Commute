package com.id11303765.commute.model;

import java.util.ArrayList;
import java.util.Comparator;

public class Timetable implements Comparable<Timetable>{

    private Trip mTrip;
    private ArrayList<StopTime> mStopTimes;
    private Stop mStop;

    public Timetable(Trip trip, ArrayList<StopTime> mStopTimes) {
        this.mTrip = trip;
        this.mStopTimes = mStopTimes;
    }

    public Timetable(Stop stop, ArrayList<StopTime> mStopTimes) {
        mStop = stop;
        this.mStopTimes = mStopTimes;
    }

    public void setTrip(Trip mTrip) {

        this.mTrip = mTrip;
    }

    public void setStopTimes(ArrayList<StopTime> mStopTimes) {
        this.mStopTimes = mStopTimes;
    }

    public Trip getTrip() {

        return mTrip;
    }

    public ArrayList<StopTime> getStopTimes() {
        return mStopTimes;
    }

    public Stop getStop() {
        return mStop;
    }

    public void setStop(Stop mStop) {
        this.mStop = mStop;
    }


    @Override
    public int compareTo(Timetable timetable) {
        return mStopTimes.get(0).compareTo(timetable.getStopTimes().get(0));
    }
}
