package com.id11303765.commute.utils;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import com.id11303765.commute.R;
import com.id11303765.commute.model.StopTime;
import com.id11303765.commute.model.Timetable;
import com.id11303765.commute.view.CommuteFragment;
import com.id11303765.commute.view.SavedRoutesFragment;
import com.id11303765.commute.view.WelcomeFragment;
import com.id11303765.commute.view.journey.JourneyFragment;
import com.id11303765.commute.view.timetables.TimetablesFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Common {
    private static Common ourInstance = new Common();
    private static Context mContext;

    public static Common getInstance() {
        return ourInstance;
    }

    private Common() {
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static String getDurationTime(long millis, boolean showHours, boolean showMinutes, boolean showSeconds) {
        String time = "";
        if (millis > Constants.ONE_MINUTE * 60) {
            if (showHours) {
                time = String.format(Locale.ENGLISH, "%2dhr ", TimeUnit.MILLISECONDS.toHours(millis));
            }
            if (showMinutes) {
                time += String.format(Locale.ENGLISH, "%02dmin ",
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
                );
            }
            if (showSeconds) {
                time += String.format(Locale.ENGLISH, "%02dsec",
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)) -
                                TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
                );
            }


        } else if (millis > Constants.ONE_MINUTE) {
            if (showMinutes) {
                time = String.format(Locale.ENGLISH, "%02dmin ",
                        TimeUnit.MILLISECONDS.toMinutes(millis));
            }
            if (showSeconds) {
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
            if (forDeparture) {
                st = t.getStopTimes().get(t.getStopTimes().size() - 2);
                departureOrArrivalTime.setTime(st.getDepartureTime());
            } else {
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

    public static void setTransportModes(int mode, View train, View bus, View ferry, View lightRail) {
        int num = getTransportModeNumber(mode);
        switch (num) {
            case 1:
                Common.makeViewVisible(train, true);
                break;
            case 2:
                Common.makeViewVisible(bus, true);
                break;
            case 3:
                Common.makeViewVisible(ferry, true);
                break;
            case 4:
                Common.makeViewVisible(lightRail, true);
                break;
        }
    }

    public static int getTransportModeNumber(int mode) {
        switch (mode) {
            case R.drawable.tnsw_icon_train:
                return 1;
            case R.drawable.tnsw_icon_bus:
                return 2;
            case R.drawable.tnsw_icon_ferry:
                return 3;
            case R.drawable.tnsw_icon_light_rail:
                return 4;
        }
        return 1;
    }

    public static void makeViewVisible(View view, boolean enabled) {
        if (enabled) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public static Calendar getNow() {
        Calendar now = Calendar.getInstance();
        now.set(1970, 0, 1);
        return now;
    }

    public static boolean isPeak(Calendar time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        boolean result = false;
        try {
            Date morningPeakStart = simpleDateFormat.parse(mContext.getString(R.string.morning_peak_start));
            Date morningPeakEnd = simpleDateFormat.parse(mContext.getString(R.string.morning_peak_end));
            Date eveningPeakStart = simpleDateFormat.parse(mContext.getString(R.string.evening_peak_start));
            Date eveningPeakEnd = simpleDateFormat.parse(mContext.getString(R.string.evening_peak_end));
            result = time.getTime().after(morningPeakStart) && time.getTime().before(morningPeakEnd) ||
                    time.getTime().after(eveningPeakStart) && time.getTime().before(eveningPeakEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Calendar parseStringToCal(String string, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        Date date = null;
        try {
            date = sdf.parse(string);
        } catch (ParseException e) {
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

}
