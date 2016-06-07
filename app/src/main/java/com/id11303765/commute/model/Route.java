package com.id11303765.commute.model;


/**
 * POJO for the route table in the DB.
 */
public class Route {
    private String mID;
    private Agency mAgency;
    private String mShortName;
    private String mLongName;
    private String mDescription;
    private int mType;
    private int mColor;

    public Route(String mID, Agency mAgency, String mShortName, String mLongName, String mDescription, int mType, int mColor) {
        this.mID = mID;
        this.mAgency = mAgency;
        this.mShortName = mShortName;
        this.mLongName = mLongName;
        this.mDescription = mDescription;
        this.mType = mType;
        this.mColor = mColor;
    }

    public String getID() {
        return mID;
    }

    public Agency getAgency() {
        return mAgency;
    }

    public String getShortName() {
        return mShortName;
    }

    public String getLongName() {
        return mLongName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getType() {
        return mType;
    }

    public int getColor() {
        return mColor;
    }
}
