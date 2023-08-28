package ru.emelkrist.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class DateUtils {
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

            Date elevenMonthsAfterNow = Date.from(LocalDate.now()
                    .plusMonths(11)
                    .atStartOfDay(ZoneId.of("Europe/Moscow"))
                    .toInstant()
            );

            Date thirtyDaysBeforeNow = Date.from(LocalDate.now()
                    .minusDays(31)
                    .atStartOfDay(ZoneId.of("Europe/Moscow"))
                    .toInstant()
            );

            return checkedDate.after(thirtyDaysBeforeNow)
                    && checkedDate.before(elevenMonthsAfterNow);
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
     * Method for getting of current date in Moscow time zone.
     *
     * @return string date
     */
    public static String getStringCurrentDateInMoscowTimeZone() {
        return Date.from(LocalDate.now()
                .atStartOfDay(ZoneId.of("Europe/Moscow"))
                .toInstant()
        ).toString();
    }

    /**
     * Method for getting formatted date and time.
     *
     * @param dateTime input date and time
     * @return formatted date and time
     */
    public static String getFormattedDateTime(String dateTime) {
        if (dateTime.length() == 8) { // for 12:00:00 format
            return dateTime.substring(0, 5);
        }
        try { // for ISO 8601 format
            ZonedDateTime outputDateTime = ZonedDateTime.parse(dateTime);
            return outputDateTime.format(dateTimeFormatter);
        } catch (DateTimeParseException e) {
            log.error("DataTimeParseException was thrown: " + e.getMessage());
        }
        return null;
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
