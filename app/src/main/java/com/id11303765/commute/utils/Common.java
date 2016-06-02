package com.id11303765.commute.utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Common {
    private static Common ourInstance = new Common();

    public static Common getInstance() {
        return ourInstance;
    }

    private Common() {
    }

    public static String getDurationTime(long millis){
        String time;
        if (millis > Constants.ONE_MINUTE * 60) {
            time = String.format(Locale.ENGLISH, "%2dhr %02dmin %02dsec",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)) -
                            TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
            );
        } else if (millis > Constants.ONE_MINUTE) {
            time = String.format(Locale.ENGLISH, "%02dmin %02dsec",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
        } else {
            time = String.format(Locale.ENGLISH, "%02dsec",
                    TimeUnit.MILLISECONDS.toSeconds(millis)
            );
        }
        return time;
    }
}
