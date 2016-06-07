package com.id11303765.commute.model;

/**
 * POJO for the agency database table
 */
public class Agency {
    private String mID;
    private String mName;


    Agency(String id, String name) {
        mID = id;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public String getID() {
        return mID;
    }
}
