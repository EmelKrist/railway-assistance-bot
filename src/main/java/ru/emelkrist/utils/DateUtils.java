package ru.emelkrist.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Date format validation method.
     *
     * @param date checked date
     * @return true of false
     */
    public static boolean isValid(String date) {
        try {
            parse(date, false);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Method for checking if a date is greater than a current date.
     *
     * @param date checked date
     * @return ture of false
     */
    public static boolean isGreaterThanNow(String date) {
        try {
            Date checkedDate = parse(date, true);
            Date now = new Date();
            return checkedDate.after(now);
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * String date formatting method.
     *
     * @param date string date
     * @return formatted date string
     */
    public static String format(String date) {
        try {
            Date parsedDate = parse(date, true);
            return dateFormat.format(parsedDate);
        } catch (ParseException e) {
            return date;
        }
    }

    /**
     * Method for parsing of string date into a Data object.
     *
     * @param date          string date
     * @param lenientStatus lenient status of date format
     * @return parsed date
     */
    private static Date parse(String date, boolean lenientStatus) throws ParseException {
        dateFormat.setLenient(lenientStatus);
        return dateFormat.parse(date);
    }

    /**
     * Date format setting method.
     *
     * @param dateFormat format of date
     */
    public static void setDateFormat(String dateFormat) {
        DateUtils.dateFormat = new SimpleDateFormat(dateFormat);
    }
}
