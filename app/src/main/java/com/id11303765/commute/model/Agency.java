package com.id11303765.commute.model;

public class Agency {
    private String mID;
    private String mName;


    public Agency(String id, String name){
        mID = id;
        mName = name;
    }

    public String getName(){
        return mName;
    }

    public String getID(){
        return mID;
    }
}
