package com.id11303765.commute.model;

public class Trip {
    private Route mRoute;
    private boolean[] mCalendar;
    private String mID;
    private String mHeadSign;
    private String mDirectionID;
    private String mBlockID;
    private boolean mWheelchairAccessible;

    public Trip(Route mRoute, boolean[] mCalendar, String mID, String mHeadSign, String mDirectionID, String mBlockID, boolean mWheelchairAccessible) {

        this.mRoute = mRoute;
        this.mCalendar = mCalendar;
        this.mID = mID;
        this.mHeadSign = mHeadSign;
        this.mDirectionID = mDirectionID;
        this.mBlockID = mBlockID;
        this.mWheelchairAccessible = mWheelchairAccessible;
    }

    public Route getRoute() {

        return mRoute;
    }

    public boolean[] getCalendar() {
        return mCalendar;
    }

    public String getID() {
        return mID;
    }

    public String getHeadSign() {
        return mHeadSign;
    }

    public String getDirectionID() {
        return mDirectionID;
    }

    public String getBlockID() {
        return mBlockID;
    }

    public boolean isWheelchairAccessible() {
        return mWheelchairAccessible;
    }
}
