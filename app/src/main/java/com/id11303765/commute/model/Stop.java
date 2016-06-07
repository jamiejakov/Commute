package com.id11303765.commute.model;

import com.id11303765.commute.R;
import com.id11303765.commute.utils.Constants;

public class Stop {
    private String mID;
    private String mName;
    private Double mLat;
    private Double mLon;
    private String mPlatformCode;
    private int mStopType;
    private String mShortName;

    public Stop(String mID, String mName, Double mLat, Double mLon, String mPlatformCode) {
        this.mID = mID;
        this.mName = mName;
        this.mLat = mLat;
        this.mLon = mLon;
        this.mPlatformCode = mPlatformCode;
        mShortName = shortenName();
        setStopType();
    }

    private void setStopType(){
        if (mName.toLowerCase().contains("wharf")) {
            mStopType = 3;
        } else if (mName.toLowerCase().contains("light rail")) {
            mStopType = 4;
        } else if (mName.toLowerCase().contains("platform")) {
            mStopType = 1;
        } else {
            mStopType = 2;
        }
    }

    private String shortenName(){
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

    public String getShortName(){
        return mShortName;
    }

    public int getStopType() {
        return mStopType;
    }

    public String getID() {
        return mID;
    }

    public int getImage() {
        int[] images = new int[]{R.drawable.tnsw_icon_train, R.drawable.tnsw_icon_bus,
                R.drawable.tnsw_icon_ferry, R.drawable.tnsw_icon_light_rail};
        return images[mStopType-1];
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
