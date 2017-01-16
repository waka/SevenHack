package io.github.waka.sevenhack.utils;

import android.text.Html;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class StringUtil {

    public static String fromHtml(String str) {
        if (str == null) return "";
        String replacedText = str.replaceAll("<(p|\n)*?>", "");
        return Html.fromHtml(replacedText).toString();
    }

    public static String omitArticle(String str) {
        String[] arr = str.split("。");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (2 > i && !arr[i].isEmpty() && !arr[i].equals(" ")) {
                builder.append(arr[i]);
                builder.append("。");
            }
        }
        return builder.toString();
    }

    public static String seekPositionToString(int position) {
        return DurationFormatter.format(position);
    }

    private static final class DurationFormatter {

        static String format(int source) {
            StringBuilder stringBuilder = new StringBuilder();
            Formatter formatter = new Formatter(stringBuilder, Locale.getDefault());

            int totalSeconds = source / 1000;

            int seconds = totalSeconds % 60;
            int minutes = (totalSeconds / 60) % 60;
            int hours = totalSeconds / 3600;

            stringBuilder.setLength(0);
            if (hours > 0) {
                return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
            } else {
                return formatter.format("%02d:%02d", minutes, seconds).toString();
            }
        }

        private DurationFormatter() {}
    }

    public static String fromDateString(String str) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz", Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateFormatter.setCalendar(new GregorianCalendar());

        Date date;
        try {
            date = dateFormatter.parse(str);
        } catch (ParseException ex) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE);
        return sdf.format(date);
    }
}
