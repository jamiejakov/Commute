package com.id11303765.commute.model;

class Fare implements Comparable<Fare>{
    private double mValue;
    private double mDistance;
    private int mType;
    private int mTransport;
    private boolean mPeak;

    Fare(double mValue, double mDistance, int mType, int mTransport, boolean mPeak) {
        this.mValue = mValue;
        this.mDistance = mDistance;
        this.mType = mType;
        this.mTransport = mTransport;
        this.mPeak = mPeak;
    }

    double getValue() {
        return mValue;
    }

    double getDistance() {
        return mDistance;
    }

    int getType() {
        return mType;
    }

    int getTransport() {
        return mTransport;
    }

    boolean isPeak() {
        return mPeak;
    }

    @Override
    public int compareTo(Fare fare) {
        return Double.compare(mDistance, fare.getDistance());
    }
}
