package com.id11303765.commute.model;

import java.util.Calendar;

public class JourneyLeg{
    private Timetable mTimetable;
    private Stop mStartStop;
    private Stop mEndStop;
    private Calendar mDepartAt;
    private Calendar mArriveBy;
    private double mPrice;
    private String mPK;

    JourneyLeg(String pk, Timetable mTimetable, Stop mStartStop, Stop mEndStop, Calendar mDepartAt, Calendar mArriveBy, double mPrice) {
        this.mPK = pk;
        this.mTimetable = mTimetable;
        this.mStartStop = mStartStop;
        this.mEndStop = mEndStop;
        this.mDepartAt = mDepartAt;
        this.mArriveBy = mArriveBy;
        this.mPrice = mPrice;
    }

    public Stop getStartStop() {
        return mStartStop;
    }

    public Stop getEndStop() {
        return mEndStop;
    }

    public Calendar getDepartAt() {
        return mDepartAt;
    }

    public Calendar getArriveBy() {
        return mArriveBy;
    }

    public Timetable getTimetable() {
        return mTimetable;
    }

    public double getPrice() {
        return mPrice;
    }

    public String getPK(){
        return mPK;
    }

}