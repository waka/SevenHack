package io.github.waka.sevenhack.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    public static int durationToInt(String duration) {
        String[] dateStructure = duration.split(":");

        int sec = 0;
        for (int i = 0; i < dateStructure.length - 1; i++) {
            sec = (sec + Integer.valueOf(dateStructure[i])) * 60;
        }
        sec += Integer.valueOf(dateStructure[dateStructure.length - 1]);

        return sec * 1000;
    }

    public static Date fromString(String str) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz", Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateFormatter.setCalendar(new GregorianCalendar());

        Date date;
        try {
            date = dateFormatter.parse(str);
        } catch (ParseException ex) {
            return null;
        }
        return date;
    }
}
