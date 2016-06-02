package com.id11303765.commute.model;


import android.database.Cursor;

import com.id11303765.commute.utils.Common;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class JourneyManager {
    private static JourneyManager ourInstance = new JourneyManager();
    private static DatabaseHelper mDatabaseHelper;
    private static ArrayList<Journey> mJourneys;
    private static ArrayList<JourneyLeg> mJourneyLegs;

    public static JourneyManager getInstance() {
        return ourInstance;
    }

    private JourneyManager() {
        mJourneys = new ArrayList<>();
        mJourneyLegs = new ArrayList<>();
    }

    public static Journey getJourney(ArrayList<String> stops, boolean departAt, Calendar time) {
        mJourneyLegs.clear();
        Journey journey = findJourney(stops, departAt, time);
        JourneyLeg journeyLeg;

        if (journey == null) {
            if (departAt) {
                Calendar departAfter = null;
                for (int i = 0; i < stops.size() - 1; i++) {
                    if (departAfter == null) {
                        departAfter = time;
                    }
                    journeyLeg = getJourneyLeg(stops.get(i), stops.get(i + 1), true, departAfter);
                    if (journeyLeg != null) {
                        departAfter = journeyLeg.getArriveBy();
                        mJourneyLegs.add(journeyLeg);
                    }
                }
            } else {
                Calendar arriveBy = null;
                for (int i = stops.size() - 1; i >= 0; i--) {
                    if (arriveBy == null) {
                        arriveBy = time;
                    }
                    journeyLeg = getJourneyLeg(stops.get(i - 1), stops.get(i), false, arriveBy);
                    if (journeyLeg != null) {
                        arriveBy = journeyLeg.getDepartAt();
                        mJourneyLegs.add(journeyLeg);
                    }
                }
            }
            Date depTime = mJourneyLegs.get(0).getDepartAt().getTime();
            Date arrTime = mJourneyLegs.get(mJourneyLegs.size()-1).getArriveBy().getTime();
            journey = new Journey(depTime, arrTime, 2.95, true, true, true, mJourneyLegs);

        }
        return journey;
    }

    private static JourneyLeg getJourneyLeg(String startStopShortName, String endStopShortName, boolean departAt, Calendar time) {
        ArrayList<Stop> startStops = StopManager.getStopsByName(startStopShortName);
        ArrayList<Stop> endStops = StopManager.getStopsByName(endStopShortName);
        ArrayList<Stop> allStops = new ArrayList<>();
        allStops.addAll(startStops);
        allStops.addAll(endStops);
        Cursor cursor = mDatabaseHelper.getTripsContainingStops(startStops, endStops);
        if (cursor.moveToFirst()) {
            ArrayList<Timetable> smallTripTimetables = new ArrayList<>();
            do {
                String tripID = cursor.getString(cursor.getColumnIndex(TripManager.KEY_ID));
                smallTripTimetables.add(TimetableManager.getTimetable(TripManager.getTrip(tripID), allStops));
            } while (cursor.moveToNext());

            Timetable closestSmallTimetable;
            if (departAt) {
                closestSmallTimetable = Common.findClosestTimetable(smallTripTimetables, startStopShortName, time, true, true);
            } else {
                closestSmallTimetable = Common.findClosestTimetable(smallTripTimetables, startStopShortName, time, false, false);
            }
            Timetable closestTimetable = TimetableManager.getTimetable(TripManager.getTrip(closestSmallTimetable.getTrip().getID()));

            ArrayList<Stop> stops = new ArrayList<>();

            for (StopTime stopTime : closestTimetable.getStopTimes()) {
                if (stopTime.getStop().getShortName().equals(startStopShortName)) {
                    stops.add(stopTime.getStop());
                }
                if (stopTime.getStop().getShortName().equals(endStopShortName)) {
                    stops.add(stopTime.getStop());
                }
            }

            Calendar departingAt = Calendar.getInstance();
            departingAt.setTime(closestSmallTimetable.getStopTimes().get(closestSmallTimetable.getStopTimes().size() - 2).getDepartureTime());
            Calendar arrivingBy = Calendar.getInstance();
            arrivingBy.setTime(closestSmallTimetable.getStopTimes().get(closestSmallTimetable.getStopTimes().size() - 1).getDepartureTime());

            return new JourneyLeg(closestTimetable, stops.get(stops.size() - 2), stops.get(stops.size() - 1), departingAt, arrivingBy);
        }
        return null;
    }


    private static Journey findJourney(ArrayList<String> stops, boolean departAt, Calendar time) {
        for (Journey journey : mJourneys) {
            if (departAt){
                if (journey.getJourneyLegs().size() == stops.size() &&
                        journey.getJourneyLegs().get(0).getDepartAt().after(time.getTime())) {
                    return journey;
                }
            }else{
                if (journey.getJourneyLegs().size() == stops.size() &&
                        journey.getJourneyLegs().get(journey.getJourneyLegs().size()-1).getArriveBy().before(time.getTime())) {
                    return journey;
                }
            }
        }
        return null;
    }

    private void findStartEndStops(String start, String end) {

    }

    private void findStartEndTimetables() {

    }

    public static void setDatabaseHelper(DatabaseHelper dbHelper) {
        mDatabaseHelper = dbHelper;
    }


}
