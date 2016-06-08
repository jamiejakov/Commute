package com.id11303765.commute.model;


import android.database.Cursor;
import android.location.Location;

import com.id11303765.commute.R;
import com.id11303765.commute.utils.Common;
import com.id11303765.commute.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Manger singleton for the journey POJO and data
 */
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

    /**
     * Make journey based on the passed in parameters
     *
     * @param stops        - the stops which to get the journey for
     * @param departAt     - true: depart at? false: arrive by?
     * @param time         - time to leave
     * @param opalCardType - opal card type gathered from preferences
     * @return Journey POJO
     */
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

    /**
     * Make a Journey Leg POJO based on the passed in parameters
     *
     * @param startStopShortName -
     * @param endStopShortName   -
     * @param departAt           - true: depart at? false: arrive by?
     * @param time               - time to leave
     * @param opalCardType       - opal card type gathered from preferences
     * @return JourneyLeg POJO
     */
    private static JourneyLeg getJourneyLeg(String startStopShortName, String endStopShortName, boolean departAt, Calendar time, int opalCardType) {
        ArrayList<Stop> startStops = StopManager.getStopsByName(startStopShortName, true);
        ArrayList<Stop> endStops = StopManager.getStopsByName(endStopShortName, true);
        ArrayList<Stop> allStops = new ArrayList<>();
        allStops.addAll(startStops);
        allStops.addAll(endStops);
        Cursor cursor = mDatabaseHelper.getTripsContainingStops(startStops, endStops);

        if (cursor != null && cursor.moveToFirst()) {
            ArrayList<Timetable> smallTripTimetables = new ArrayList<>();
            ArrayList<Timetable> smallTripTimetablesNextDay = new ArrayList<>();

            // Make a timetable list that can be used to find the best journey leg
            do {
                String tripID = cursor.getString(cursor.getColumnIndex(TripManager.KEY_ID));
                Trip trip = TripManager.getTrip(tripID);
                if (trip.getCalendar()[time.get(Calendar.DAY_OF_WEEK) - 1]) {
                    smallTripTimetables.add(TimetableManager.getTimetable(trip, allStops));
                }
                if (trip.getCalendar()[time.get(Calendar.DAY_OF_WEEK)]) {
                    smallTripTimetablesNextDay.add(TimetableManager.getTimetable(trip, allStops));
                }

            } while (cursor.moveToNext());

            // find the closest small (only start and end stops in it) timetable to chosen time, with chosen settings
            Calendar justTimeNoDate = Common.getNow();
            justTimeNoDate.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
            justTimeNoDate.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
            Timetable closestSmallTimetable;
            if (departAt) {
                closestSmallTimetable = Common.findClosestTimetable(smallTripTimetables, startStopShortName, justTimeNoDate, true, true);
            } else {
                closestSmallTimetable = Common.findClosestTimetable(smallTripTimetables, endStopShortName, justTimeNoDate, false, false);
            }
            if (closestSmallTimetable == null) {
                Calendar cal = Common.parseStringToCal(Constants.NEXT_MORNING_1AM, Constants.DATE_FORMAT_HH24_MM);
                closestSmallTimetable = Common.findClosestTimetable(smallTripTimetablesNextDay, startStopShortName, cal, true, true);
            }

            //Create a proper timetable with all the stops in between
            Timetable closestTimetable = TimetableManager.getTimetable(TripManager.getTrip(closestSmallTimetable.getTrip().getID()));

            // Get all the stops in a list and the find the Start stop POJO and end stop POJO for this Timetable
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

            // get the departure and arrival times
            Calendar departingAt = Calendar.getInstance();
            Calendar arrivingBy = Calendar.getInstance();
            departingAt.setTime(closestSmallTimetable.getStopTimes().get(closestSmallTimetable.getStopTimes().size() - 2).getDepartureTime());
            arrivingBy.setTime(closestSmallTimetable.getStopTimes().get(closestSmallTimetable.getStopTimes().size() - 1).getDepartureTime());

            // make the other calculations (price, distance, etc) and create the JourneyLeg
            removeExtraStopTimes(stopSequences, closestTimetable);
            int type = endStop.getStopType();
            float distance = calculateDistance(startStop, endStop);
            double price = FareManager.getFare(opalCardType, type, Common.isPeak(Common.getNow()), distance).getValue();
            if (time.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && price > 2.50) {
                price = 2.50;
            }
            String uniqueID = UUID.randomUUID().toString();
            return new JourneyLeg(uniqueID, closestTimetable, startStop, endStop, departingAt, arrivingBy, price);
        }
        return null;
    }

    /**
     * Calculate the distance between the two stops
     *
     * @param startStop -
     * @param endStop   -
     * @return distance in km
     */
    private static float calculateDistance(Stop startStop, Stop endStop) {
        Location startLoc = new Location("");
        startLoc.setLatitude(startStop.getLat());
        startLoc.setLongitude(startStop.getLon());

        Location endLoc = new Location("");
        endLoc.setLatitude(endStop.getLat());
        endLoc.setLongitude(endStop.getLon());
        return startLoc.distanceTo(endLoc) / 1000;
    }

    /**
     * Removes the StopTimes before departure stop and after arrival stop
     * ex: Int Airport -> Mascot - everything before Airport, and after Mascot removed
     * Saves space and simplifies calculations later on
     *
     * @param stopSequences - sequence number of this stop
     * @param timetable     to go through and do the removal
     */
    private static void removeExtraStopTimes(ArrayList<Integer> stopSequences, Timetable timetable) {
        int startStopSequence = stopSequences.get(stopSequences.size() - 2);
        int endStopSequence = stopSequences.get(stopSequences.size() - 1);

        ArrayList<StopTime> stopTimesToRemove = new ArrayList<>();
        for (StopTime stopTime : timetable.getStopTimes()) {
            if (stopTime.getStopSequence() < startStopSequence || stopTime.getStopSequence() > endStopSequence) {
                stopTimesToRemove.add(stopTime);
            }
        }
        timetable.getStopTimes().removeAll(stopTimesToRemove);
    }

    /**
     * Find journeys from local based on the passed in values
     *
     * @param stops    list of stops
     * @param departAt whether its deptart at or arrive by
     * @param time     time to leave/arrive
     * @return Journey POJO if found
     */
    private static Journey findJourney(ArrayList<String> stops, boolean departAt, Calendar time) {
        for (Journey journey : mJourneys) {
            if (departAt) {
                if (journey.getJourneyLegs().size() == stops.size() &&
                        journey.getDepartureTime().after(time.getTime())) {
                    return journey;
                }
            } else {
                if (journey.getJourneyLegs().size() == stops.size() &&
                        journey.getArrivalTime().before(time.getTime())) {
                    return journey;
                }
            }
        }
        return null;
    }

    /**
     * Find journeys from local based on the PK (unique ID)
     *
     * @param PK to search by
     * @return Journey POJO
     */
    public static Journey findJourney(String PK) {
        for (Journey journey : mJourneys) {
            if (journey.getPK().equals(PK)) {
                return journey;
            }
        }
        return null;
    }

    /**
     * Set the db helper to use for queries
     *
     * @param dbHelper -
     */
    public static void setDatabaseHelper(DatabaseHelper dbHelper) {
        mDatabaseHelper = dbHelper;
    }
}
