package com.id11303765.commute.model;


import java.util.Date;

/**
 * POJO for the stopTime table of the DB
 */
public class StopTime implements Comparable<StopTime>{
    private Trip mTrip;
    private Date mArrivalTime;
    private Date mDepartureTime;
    private Stop mStop;
    private int mStopSequence;

    public StopTime(Trip trip, Date mArrivalTime, Date mDepartureTime, Stop stop, int mStopSequence) {
        this.mTrip = trip;
        this.mArrivalTime = mArrivalTime;
        this.mDepartureTime = mDepartureTime;
        this.mStop = stop;
        this.mStopSequence = mStopSequence;
    }

    public Trip getTrip() {
        return mTrip;
    }

    public Date getArrivalTime() {
        return mArrivalTime;
    }

    public Date getDepartureTime() {
        return mDepartureTime;
    }

    public Stop getStop() {
        return mStop;
    }

    public int getStopSequence() {
        return mStopSequence;
    }

    @Override
    public int compareTo(StopTime stopTime) {
        return this.mDepartureTime.compareTo(stopTime.getDepartureTime());
    }
}
