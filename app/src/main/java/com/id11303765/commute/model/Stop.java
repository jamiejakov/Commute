package com.id11303765.commute.model;

import com.id11303765.commute.R;
import com.id11303765.commute.utils.Constants;

public class Stop {
    private String mID;
    private String mCode;
    private String mName;
    private Double mLat;
    private Double mLon;
    private String mPlatformCode;
    private int mStopType;
    private String mShortName;

    public Stop(String mID, String mCode, String mName, Double mLat, Double mLon, String mPlatformCode) {
        this.mID = mID;
        this.mCode = mCode;
        this.mName = mName;
        this.mLat = mLat;
        this.mLon = mLon;
        this.mPlatformCode = mPlatformCode;
        mStopType = getImage();
        mShortName = shortenName();
    }

    private int getImage() {
        if (mName.toLowerCase().contains("wharf")) {
            return R.drawable.tnsw_icon_ferry;
        } else if (mName.toLowerCase().contains("light rail")) {
            return R.drawable.tnsw_icon_light_rail;
        } else if (mName.toLowerCase().contains("platform")) {
            return R.drawable.tnsw_icon_train;
        }
        return 0;
    }

    private String shortenName(){
        String s;
        if (mName.toLowerCase().contains("wharf")) {
            String[] columns = mName.split(",");
            return columns[0].trim();
        } else if (mName.toLowerCase().contains("light rail")) {
            return mName.replaceAll("(?i)light rail", "").replaceAll("(?i)station", "").trim();
        } else if (mName.toLowerCase().contains("platform")) {
            String[] columns = mName.split(",");
            return columns[0].replaceAll("(?i)station", "").trim();
        }
        return null;
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

    public String getShortName(){
        return mShortName;
    }

    public int getStopType() {
        return mStopType;
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
