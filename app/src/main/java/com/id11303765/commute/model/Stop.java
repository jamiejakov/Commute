package com.id11303765.commute.model;

public class Stop {
    private String mID;
    private String mCode;
    private String mName;
    private Double mLat;
    private Double mLon;
    private String mPlatformCode;

    public Stop(String mID, String mCode, String mName, Double mLat, Double mLon, String mPlatformCode) {
        this.mID = mID;
        this.mCode = mCode;
        this.mName = mName;
        this.mLat = mLat;
        this.mLon = mLon;
        this.mPlatformCode = mPlatformCode;
    }

    public void setID(String mID) {
        this.mID = mID;
    }

    public void setCode(String mCode) {
        this.mCode = mCode;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setLat(Double mLat) {
        this.mLat = mLat;
    }

    public void setLon(Double mLon) {
        this.mLon = mLon;
    }

    public void setPlatformCode(String mPlatformCode) {
        this.mPlatformCode = mPlatformCode;
    }

    public String getID() {

        return mID;
    }

    public String getCode() {
        return mCode;
    }

    public String getName() {
        return mName;
    }

    public Double getLat() {
        return mLat;
    }

    public Double getLon() {
        return mLon;
    }

    public String getPlatformCode() {
        return mPlatformCode;
    }
}
