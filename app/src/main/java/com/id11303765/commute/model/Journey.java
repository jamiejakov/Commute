package com.id11303765.commute.model;


import java.util.ArrayList;
import java.util.Date;

public class Journey {
    private Date mDepartureTime;
    private Date mArrivalTime;
    private double mPrice;
    private boolean mSpeed;
    private boolean mCost;
    private boolean mConvenience;
    private ArrayList<JourneyLeg> mJourneyLegs;
    private String mPK;


    Journey(Date mDepartureTime, Date mArrivalTime, double mPrice, boolean mSpeed, boolean mCost, boolean mConvenience, ArrayList<JourneyLeg> mJourneyLegs, String mPK) {
        this.mDepartureTime = mDepartureTime;
        this.mArrivalTime = mArrivalTime;
        this.mPrice = mPrice;
        this.mSpeed = mSpeed;
        this.mCost = mCost;
        this.mConvenience = mConvenience;
        this.mJourneyLegs = mJourneyLegs;
        this.mPK = mPK;
    }

    public boolean isFast() {
        return mSpeed;
    }

    public boolean isCheap() {
        return mCost;
    }

    public boolean isConvenient() {
        return mConvenience;
    }

    public ArrayList<JourneyLeg> getJourneyLegs() {
        return mJourneyLegs;
    }

    public Date getArrivalTime() {
        return mArrivalTime;
    }

    public Date getDepartureTime() {
        return mDepartureTime;
    }

    public double getmPrice() {
        return mPrice;
    }

    public String getPK() {
        return mPK;
    }

    public Stop getStartStop(){
        return mJourneyLegs.get(0).getStartStop();
    }

    public Stop getEndStop(){
        return mJourneyLegs.get(mJourneyLegs.size()-1).getEndStop();
    }
}
