package com.example.marcos.smartparking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static final String DATE_PATTERN =  "dd-MM-yyyy";
    public static final String TIME_PATTERN =  "h:mm a";

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        return sdf.format(date.getTime());
    }

    public static String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
        return sdf.format(date.getTime());
    }
}
