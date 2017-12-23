package com.acpnctr.acpnctr.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by guiguette on 15/12/2017.
 */

public class DateFormatUtil {

    // timestamp must be in milliseconds so multiply by 1000 if timestamp doesn't include them
    // must be 13 digits long
    public static String convertTimestampToString(long timestamp) {
        // TODO: insert auto Locale recog for date pattern
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }


    public static long convertStringToTimestamp(String dateString) throws ParseException {
        DateFormat formatter= new SimpleDateFormat("dd/MM/yyyy");
        Date date = formatter.parse(dateString);
        return date.getTime()/1000; // we divide by 1000 to get rid of the milliseconds
    }
}
