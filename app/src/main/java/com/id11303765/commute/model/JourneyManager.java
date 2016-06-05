package com.id11303765.commute.model;


import android.database.Cursor;
import android.location.Location;

import com.id11303765.commute.R;
import com.id11303765.commute.utils.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

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

    public static Journey getJourney(ArrayList<String> stops, boolean departAt, Calendar time, int opalCardType) {
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
                    journeyLeg = getJourneyLeg(stops.get(i), stops.get(i + 1), true, departAfter, opalCardType);
                    if (journeyLeg != null) {
                        departAfter = journeyLeg.getArriveBy();
                        mJourneyLegs.add(journeyLeg);
                    }
                }
            } else {
                Calendar arriveBy = null;
                for (int i = stops.size() - 1; i > 0; i--) {
                    if (arriveBy == null) {
                        arriveBy = time;
                    }
                    journeyLeg = getJourneyLeg(stops.get(i - 1), stops.get(i), false, arriveBy, opalCardType);
                    if (journeyLeg != null) {
                        arriveBy = journeyLeg.getDepartAt();
                        mJourneyLegs.add(journeyLeg);
                    }
                }
            }
            if (mJourneyLegs.size() != 0) {
                Date depTime = mJourneyLegs.get(0).getDepartAt().getTime();
                Date arrTime = mJourneyLegs.get(mJourneyLegs.size() - 1).getArriveBy().getTime();
                String uniqueID = UUID.randomUUID().toString();
                double price = 0;
                for (JourneyLeg jl : mJourneyLegs) {
                    price += jl.getPrice();
                }
                journey = new Journey(depTime, arrTime, price, true, true, true, mJourneyLegs, uniqueID);
                mJourneys.add(journey);
            }
        }

        return journey;
    }

    private static JourneyLeg getJourneyLeg(String startStopShortName, String endStopShortName, boolean departAt, Calendar time, int opalCardTime) {
        ArrayList<Stop> startStops = StopManager.getStopsByName(startStopShortName);
        ArrayList<Stop> endStops = StopManager.getStopsByName(endStopShortName);
        ArrayList<Stop> allStops = new ArrayList<>();
        allStops.addAll(startStops);
        allStops.addAll(endStops);
        Cursor cursor = mDatabaseHelper.getTripsContainingStops(startStops, endStops);
        if (cursor != null && cursor.moveToFirst()) {
            ArrayList<Timetable> smallTripTimetables = new ArrayList<>();
            do {
                String tripID = cursor.getString(cursor.getColumnIndex(TripManager.KEY_ID));
                smallTripTimetables.add(TimetableManager.getTimetable(TripManager.getTrip(tripID), allStops));
            } while (cursor.moveToNext());

            Timetable closestSmallTimetable;
            if (departAt) {
                closestSmallTimetable = Common.findClosestTimetable(smallTripTimetables, startStopShortName, time, true, true);
            } else {
                closestSmallTimetable = Common.findClosestTimetable(smallTripTimetables, endStopShortName, time, false, false);
            }
            if (closestSmallTimetable == null){
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                    Calendar cal = Calendar.getInstance();
                    Date morningTrain = simpleDateFormat.parse("04:00");
                    cal.setTime(morningTrain);
                    closestSmallTimetable = Common.findClosestTimetable(smallTripTimetables, startStopShortName,cal, true, true);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            Timetable closestTimetable = TimetableManager.getTimetable(TripManager.getTrip(closestSmallTimetable.getTrip().getID()));

            ArrayList<Stop> stops = new ArrayList<>();
            ArrayList<Integer> stopSequences = new ArrayList<>();

            for (StopTime stopTime : closestTimetable.getStopTimes()) {
                if (stopTime.getStop().getShortName().equals(startStopShortName)) {
                    stops.add(stopTime.getStop());
                    stopSequences.add(stopTime.getStopSequence());
                }
                if (stopTime.getStop().getShortName().equals(endStopShortName)) {
                    stops.add(stopTime.getStop());
                    stopSequences.add(stopTime.getStopSequence());
                }
            }
            Stop startStop = stops.get(stops.size() - 2);
            Stop endStop = stops.get(stops.size() - 1);

            Calendar departingAt = Calendar.getInstance();
            departingAt.setTime(closestSmallTimetable.getStopTimes().get(closestSmallTimetable.getStopTimes().size() - 2).getDepartureTime());
            Calendar arrivingBy = Calendar.getInstance();
            arrivingBy.setTime(closestSmallTimetable.getStopTimes().get(closestSmallTimetable.getStopTimes().size() - 1).getDepartureTime());

            removeExtraStopTimes(stopSequences, closestTimetable);
            int type = Common.getTransportModeNumber(endStop.getStopType());
            float distance = calculateDistance(startStop, endStop);
            double price = FareManager.getFare(opalCardTime, type, Common.isPeak(Common.getNow()), distance).getValue();

            return new JourneyLeg(closestTimetable, startStop, endStop, departingAt, arrivingBy, price);
        }
        return null;
    }

    private static float calculateDistance(Stop startStop, Stop endStop){
        Location startLoc = new Location("");
        startLoc.setLatitude(startStop.getLat());
        startLoc.setLongitude(startStop.getLon());

        Location endLoc = new Location("");
        endLoc.setLatitude(endStop.getLat());
        endLoc.setLongitude(endStop.getLon());
        return startLoc.distanceTo(endLoc) / 1000;
    }

    private static void removeExtraStopTimes(ArrayList<Integer> stopSequences, Timetable closestTimetable){
        int startStopSequence = stopSequences.get(stopSequences.size() - 2);
        int endStopSequence = stopSequences.get(stopSequences.size() - 1);

        ArrayList<StopTime> stopTimesToRemove = new ArrayList<>();
        for (StopTime stopTime : closestTimetable.getStopTimes()){
            if (stopTime.getStopSequence() < startStopSequence || stopTime.getStopSequence() > endStopSequence){
                stopTimesToRemove.add(stopTime);
            }
        }
        closestTimetable.getStopTimes().removeAll(stopTimesToRemove);
    }

    private static Journey findJourney(ArrayList<String> stops, boolean departAt, Calendar time) {
        for (Journey journey : mJourneys) {
            if (departAt) {
                if (journey.getJourneyLegs().size() == stops.size() &&
                        journey.getJourneyLegs().get(0).getDepartAt().after(time.getTime())) {
                    return journey;
                }
            } else {
                if (journey.getJourneyLegs().size() == stops.size() &&
                        journey.getJourneyLegs().get(journey.getJourneyLegs().size() - 1).getArriveBy().before(time.getTime())) {
                    return journey;
                }
            }
        }
        return null;
    }

    public static Journey findJourney(String PK) {
        for (Journey journey : mJourneys) {
            if (journey.getPK().equals(PK)) {
                return journey;
            }
        }
        return null;
    }

    public static void setDatabaseHelper(DatabaseHelper dbHelper) {
        mDatabaseHelper = dbHelper;
    }


}
