package com.id11303765.commute.model;

import com.id11303765.commute.R;
import com.id11303765.commute.utils.Constants;

/**
 * POJO for the stop table in the DB
 */
public class Stop {
    private String mID;
    private String mName;
    private Double mLat;
    private Double mLon;
    private String mPlatformCode;
    private int mStopType;
    String mShortName;

    public Stop(String mID, String mName, Double mLat, Double mLon, String mPlatformCode) {
        this.mID = mID;
        this.mName = mName;
        this.mLat = mLat;
        this.mLon = mLon;
        this.mPlatformCode = mPlatformCode;
        setStopType();
        mShortName = shortenName();
    }

    /**
     * Sets the stop type based on the string found in the name
     */
    private void setStopType() {
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

    /**
     * Get the short name for the stop based on stopType
     * @return short name
     */
    private String shortenName() {
        String[] columns = mName.split(Constants.COMMA_SPLIT);
        switch(mStopType){
            case 3:
                return columns[0].trim();
            case 4:
                return mName.replaceAll("(?i)light rail", "").replaceAll("(?i)station", "").trim();
            case 1:
                return columns[0].replaceAll("(?i)station", "").trim();
        }
        return null;
    }

    public String getShortName() {
        return mShortName;
    }

    public int getStopType() {
        return mStopType;
    }

    public String getID() {
        return mID;
    }

    /**
     *
     * @return the correct Transport NSW image corresponding to the type of stop
     */
    public int getImage() {
        int[] images = new int[]{R.drawable.tnsw_icon_train, R.drawable.tnsw_icon_bus,
                R.drawable.tnsw_icon_ferry, R.drawable.tnsw_icon_light_rail};
        return images[mStopType - 1];
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
