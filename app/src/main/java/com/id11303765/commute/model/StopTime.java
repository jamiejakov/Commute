package com.id11303765.commute.model;


import java.util.Date;

public class StopTime {
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

    public void setTrip(Trip trip) {

        this.mTrip = trip;
    }

    public void setArrivalTime(Date mArrivalTime) {
        this.mArrivalTime = mArrivalTime;
    }

    public void setDepartureTime(Date mDepartureTime) {
        this.mDepartureTime = mDepartureTime;
    }

    public void setStop(Stop stop) {
        this.mStop = stop;
    }

    public void setStopSequence(int mStopSequence) {
        this.mStopSequence = mStopSequence;
    }
}
