package com.id11303765.commute.utils;

import com.id11303765.commute.model.Stop;
import com.id11303765.commute.model.StopTime;
import com.id11303765.commute.model.Timetable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Common {
    private static Common ourInstance = new Common();

    public static Common getInstance() {
        return ourInstance;
    }

    private Common() {
    }

    public static String getDurationTime(long millis, boolean showHours, boolean showMinutes, boolean showSeconds){
        String time ="";
        if (millis > Constants.ONE_MINUTE * 60) {
            if (showHours){
                time = String.format(Locale.ENGLISH, "%2dhr ", TimeUnit.MILLISECONDS.toHours(millis));
            }
            if (showMinutes){
                time += String.format(Locale.ENGLISH, "%02dmin ",
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
                );
            }
            if (showSeconds){
                time += String.format(Locale.ENGLISH, "%02dsec",
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)) -
                                TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
                );
            }


        } else if (millis > Constants.ONE_MINUTE) {
            if (showMinutes){
                time = String.format(Locale.ENGLISH, "%02dmin ",
                        TimeUnit.MILLISECONDS.toMinutes(millis));
            }
            if (showSeconds){
                time += String.format(Locale.ENGLISH, "%02dsec",
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            }
        } else {
            time = String.format(Locale.ENGLISH, "%02dsec",
                    TimeUnit.MILLISECONDS.toSeconds(millis)
            );
        }
        return time;
    }

    public static Timetable findClosestTimetable(ArrayList<Timetable> timetables, String stopName, Calendar now, boolean forNext, boolean forDeparture) {
        Timetable timetable = null;
        Calendar currentTimetableTime = Calendar.getInstance();
        for (Timetable t : timetables) {
            StopTime st;
            Calendar departureOrArrivalTime = Calendar.getInstance();
            if (forDeparture){
                st = t.getStopTimes().get(t.getStopTimes().size() - 2);
                departureOrArrivalTime.setTime(st.getDepartureTime());
            }else{
                st = t.getStopTimes().get(t.getStopTimes().size() - 1);
                departureOrArrivalTime.setTime(st.getArrivalTime());
            }
            boolean correctStop = st.getStop().getShortName().equals(stopName);

            if (forNext) {
                if (correctStop && departureOrArrivalTime.after(now)) {
                    if (timetable == null || departureOrArrivalTime.before(currentTimetableTime)) {
                        timetable = t;
                        currentTimetableTime.setTime(st.getDepartureTime());
                    }

                }
            } else {
                if (correctStop && departureOrArrivalTime.before(now)) {
                    if (timetable == null || departureOrArrivalTime.after(currentTimetableTime)) {
                        timetable = t;
                        currentTimetableTime.setTime(st.getDepartureTime());
                    }
                }
            }

        }
        return timetable;
    }

    public static Calendar getNow(){
        Calendar now = Calendar.getInstance();
        now.set(1970, 0, 1);
        return now;
    }

}
