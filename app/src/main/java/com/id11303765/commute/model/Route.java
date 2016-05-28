package com.id11303765.commute.model;


import android.graphics.Color;

public class Route {
    private String mID;
    private Agency mAgency;
    private String mShortName;
    private String mLongName;
    private String mDescription;
    private int mType;
    private int mColor;

    public Route(String id, Agency agency, String shortName, String longName, String desc, int type, int color){
        setID(id);
        setAgency(agency);
        setShortName(shortName);
        setLongName(longName);
        setDescription(desc);
        setType(type);
        setColor(color);
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

    public void setID(String mID) {
        this.mID = mID;
    }

    public void setAgency(Agency mAgency) {
        this.mAgency = mAgency;
    }

    public void setShortName(String mShortName) {
        this.mShortName = mShortName;
    }

    public void setLongName(String mLongName) {
        this.mLongName = mLongName;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }
}
