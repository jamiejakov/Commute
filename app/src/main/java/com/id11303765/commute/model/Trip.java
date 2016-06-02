package com.id11303765.commute.model;

public class Trip {
    private Route mRoute;
    private boolean[] mCalendar;
    private String mID;
    private String mHeadSign;

    Trip(Route mRoute, boolean[] mCalendar, String mID, String mHeadSign) {

        this.mRoute = mRoute;
        this.mCalendar = mCalendar;
        this.mID = mID;
        this.mHeadSign = mHeadSign;
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

}
