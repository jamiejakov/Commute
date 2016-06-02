package com.id11303765.commute.model;

import java.util.Calendar;

public class JourneyLeg{
    private Timetable mTimetable;
    private Stop mStartStop;
    private Stop mEndStop;
    private Calendar mDepartAt;
    private Calendar mArriveBy;

    JourneyLeg(Timetable mTimetable, Stop mStartStop, Stop mEndStop, Calendar mDepartAt, Calendar mArriveBy) {
        this.mTimetable = mTimetable;
        this.mStartStop = mStartStop;
        this.mEndStop = mEndStop;
        this.mDepartAt = mDepartAt;
        this.mArriveBy = mArriveBy;
    }

    public Stop getStartStop() {
        return mStartStop;
    }

    public Stop getEndStop() {
        return mEndStop;
    }

    Calendar getDepartAt() {
        return mDepartAt;
    }

    Calendar getArriveBy() {
        return mArriveBy;
    }

    public Timetable getTimetable() {
        return mTimetable;
    }
}