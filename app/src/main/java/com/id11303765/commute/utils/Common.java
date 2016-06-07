package com.id11303765.commute.utils;

import android.util.Log;
import android.view.View;

import com.id11303765.commute.model.Stop;
import com.id11303765.commute.model.StopTime;
import com.id11303765.commute.model.Timetable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * Class that contains commonly used procedures and function.
 * Is static
 * Can be accessed from any class within the app.
 */
public class Common {
    private static Common ourInstance = new Common();

    public static Common getInstance() {
        return ourInstance;
    }

    /**
     * Function that formats a duration and returns a string.
     *
     * @param millis      - duration in milliseconds
     * @param showHours   - option to show hours duration
     * @param showMinutes - option to show minutes duration
     * @param showSeconds - option to show seconds duration
     * @return - the duration formatted in a string: 1hr 10min 35sec
     */
    public static String getDurationTime(long millis, boolean showHours, boolean showMinutes, boolean showSeconds) {
        String time = "";
        if (millis > Constants.ONE_MINUTE * 60) {
            if (showHours) {
                time = String.format(Locale.ENGLISH, Constants.TIME_FORMAT_HR, TimeUnit.MILLISECONDS.toHours(millis));
            }
            if (showMinutes) {
                time += String.format(Locale.ENGLISH, Constants.TIME_FORMAT_MIN,
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
                );
            }
            if (showSeconds) {
                time += String.format(Locale.ENGLISH, Constants.TIME_FORMAT_SEC,
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)) -
                                TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
                );
            }

        } else if (millis > Constants.ONE_MINUTE) {
            if (showMinutes) {
                time = String.format(Locale.ENGLISH, Constants.TIME_FORMAT_MIN,
                        TimeUnit.MILLISECONDS.toMinutes(millis));
            }
            if (showSeconds) {
                time += String.format(Locale.ENGLISH, Constants.TIME_FORMAT_SEC,
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            }
        } else {
            time = String.format(Locale.ENGLISH, Constants.TIME_FORMAT_SEC,
                    TimeUnit.MILLISECONDS.toSeconds(millis)
            );
        }
        return time;
    }

    /**
     * Function that takes the below criteria and finds the timetable matching them.
     *
     * @param timetables   - list of timetables to search through
     * @param stopName     - the stop which to check against
     * @param time         - the time to check against
     * @param forNext      - true: check after current time (next timetable); false: check before current time (previous timetable)
     * @param forDeparture - true: find timetable for time when departing from stop; false: for time when arriving to stop
     * @return - timetable matching the criteria
     */
    public static Timetable findClosestTimetable(ArrayList<Timetable> timetables, String stopName, Calendar time, boolean forNext, boolean forDeparture) {
        Timetable timetable = null;
        Calendar currentTimetableTime = Calendar.getInstance();
        for (Timetable t : timetables) {
            StopTime st;
            Calendar departureOrArrivalTime = Calendar.getInstance();
            if (forDeparture) {
                st = t.getStopTimes().get(t.getStopTimes().size() - 2);
                departureOrArrivalTime.setTime(st.getDepartureTime());
            } else {
                st = t.getStopTimes().get(t.getStopTimes().size() - 1);
                departureOrArrivalTime.setTime(st.getArrivalTime());
            }
            boolean correctStop = st.getStop().getShortName().equals(stopName);

            if (forNext) {
                if (correctStop && departureOrArrivalTime.after(time)) {
                    if (timetable == null || departureOrArrivalTime.before(currentTimetableTime)) {
                        Log.d(Constants.TAG_COMMON_FIND_TIMETABLE_NEXT_SET, String.valueOf(departureOrArrivalTime.getTime()));
                        timetable = t;
                        currentTimetableTime.setTime(st.getDepartureTime());
                    }

                }
            } else {
                if (correctStop && departureOrArrivalTime.before(time)) {
                    if (timetable == null || departureOrArrivalTime.after(currentTimetableTime)) {
                        Log.d(Constants.TAG_COMMON_FIND_TIMETABLE_PREV_SET, String.valueOf(departureOrArrivalTime.getTime()));
                        timetable = t;
                        currentTimetableTime.setTime(st.getDepartureTime());
                    }
                }
            }
        }
        return timetable;
    }

    /**
     * Make the view visible or gone
     *
     * @param view    - view, which to change the visibility of
     * @param enabled - true: visible; false: gone
     */
    public static void makeViewVisible(View view, boolean enabled) {
        if (enabled) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * Gets the time now, resets the date to default.
     * Used for comparing time with peak hour and such.
     *
     * @return - calendar with time now and date at default
     */
    public static Calendar getNow() {
        Calendar now = Calendar.getInstance();
        now.set(1970, 0, 1);
        return now;
    }

    /**
     * Check whether the time passed in is within Sydney Trains peak hour
     *
     * @param time - time to check
     * @return whether time is during Sydney Trains peak hour or not
     */
    public static boolean isPeak(Calendar time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_HH24_MM, Locale.US);
        Calendar justTimeNoDate = Common.getNow();
        justTimeNoDate.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        justTimeNoDate.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
        boolean result = false;
        try {
            Date morningPeakStart = simpleDateFormat.parse(Constants.MORNING_PEAK_START);
            Date morningPeakEnd = simpleDateFormat.parse(Constants.MORNING_PEAK_END);
            Date eveningPeakStart = simpleDateFormat.parse(Constants.EVENING_PEAK_START);
            Date eveningPeakEnd = simpleDateFormat.parse(Constants.EVENING_PEAK_END);
            result = justTimeNoDate.getTime().after(morningPeakStart) && justTimeNoDate.getTime().before(morningPeakEnd) ||
                    justTimeNoDate.getTime().after(eveningPeakStart) && justTimeNoDate.getTime().before(eveningPeakEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Checks whether the passed in day is a workday or weekend
     *
     * @param date - date to check
     * @return - whether date is a workday
     */
    public static boolean isWorkday(Calendar date) {
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY;
    }

    /**
     * Parse the passed in string based on the format and convert it into a calendar object
     *
     * @param string - string to process
     * @param format - the format the string is in
     * @return - calendar object with correct date and time
     */
    public static Calendar parseStringToCal(String string, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        Date date = null;
        try {
            date = sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * Function that finds the StopTime for a stop in a timetable
     * Get the stopTime for the passed in stop
     *
     * @param stop      - stop to look for
     * @param timetable - place to look for it
     * @return - the StopTime
     */
    public static StopTime getStopTime(Stop stop, Timetable timetable) {
        StopTime stopTime = null;
        for (StopTime st : timetable.getStopTimes()) {
            if (st.getStop().equals(stop)) {
                stopTime = st;
            }
        }
        return stopTime;
    }

}
