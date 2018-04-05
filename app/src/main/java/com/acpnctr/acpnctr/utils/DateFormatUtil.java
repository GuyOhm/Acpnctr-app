package com.acpnctr.acpnctr.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util class with date format manipulation methods
 */

public class DateFormatUtil {

    // Regex for date wi format dd/mm/yyyy
    // see https://stackoverflow.com/questions/17416595/date-validation-in-android
    private static final String DATE_PATTERN =
            "(0?[1-9]|[12][0-9]|3[01])[/](0?[1-9]|1[012])[/]((19|20)\\d\\d)";

    // timestamp must be in milliseconds so multiply by 1000 if timestamp doesn't include them
    // must be 13 digits long
    public static String convertTimestampToString(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }


    public static long convertStringToTimestamp(String dateString) throws ParseException {
        DateFormat formatter= new SimpleDateFormat("dd/MM/yyyy");
        Date date = formatter.parse(dateString);
        return date.getTime()/1000; // we divide by 1000 to get rid of the milliseconds
    }

    public static String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(Calendar.getInstance().getTime());
    }

    /**
     * Validate date format with regular expression
     * @param date date address for validation
     * @return true valid date format, false invalid date format
     */
    public static boolean validate(final String date){

        // initialize pattern matcher
        Matcher matcher = Pattern.compile(DATE_PATTERN).matcher(date);

        if(matcher.matches()){
            matcher.reset();

            if(matcher.find()){
                String day = matcher.group(1);
                String month = matcher.group(2);
                int year = Integer.parseInt(matcher.group(3));

                if (day.equals("31") &&
                        (month.equals("4") || month .equals("6") || month.equals("9") ||
                                month.equals("11") || month.equals("04") || month.equals("06") ||
                                month.equals("09"))) {
                    return false; // only 1,3,5,7,8,10,12 has 31 days
                }

                else if (month.equals("2") || month.equals("02")) {
                    //leap year
                    if(year % 4==0){
                        if(day.equals("30") || day.equals("31")){
                            return false;
                        }
                        else{
                            return true;
                        }
                    }
                    else{
                        if(day.equals("29")||day.equals("30")||day.equals("31")){
                            return false;
                        }
                        else{
                            return true;
                        }
                    }
                }

                else{
                    return true;
                }
            }

            else{
                return false;
            }
        }
        else{
            return false;
        }
    }
}
