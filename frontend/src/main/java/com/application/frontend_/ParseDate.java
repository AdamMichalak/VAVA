package com.application.frontend_;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ParseDate {

    public static LocalDateTime parseDateFromDBToLocalDateTime(String date) {
        Date myDate = Date.from(Instant.parse(date));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = formatter.format(myDate);
        DateTimeFormatter frm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return LocalDateTime.parse(formattedDate, frm);
    }

    public static LocalDate parseDateFromDBToLocalDate(String date) {
        Date myDate = Date.from(Instant.parse(date));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = formatter.format(myDate);
        DateTimeFormatter frm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(formattedDate, frm);

        return LocalDate.from(dateTime);
    }

    public static String parseDateFromDBToLocalDateString(String date) {
        Date myDate = Date.from(Instant.parse(date));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = formatter.format(myDate);
        DateTimeFormatter frm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(formattedDate, frm);

        return LocalDate.from(dateTime).toString();
    }

    public static String getMinutesFromDateTime (LocalDateTime dateTime) {

        return dateTime.getMinute() < 10 ? "0" + dateTime.getMinute() : String.valueOf(dateTime.getMinute());
    }

    public static String getHoursFromDateTime (LocalDateTime dateTime) {

        return dateTime.getHour() < 10 ? "0" + dateTime.getHour() : String.valueOf(dateTime.getHour());
    }

    public static String getTimeFromDateTime (LocalDateTime dateTime) {
        String minutes = dateTime.getMinute() < 10 ? "0" + dateTime.getMinute() : String.valueOf(dateTime.getMinute());
        String hours = dateTime.getHour() < 10 ? "0" + dateTime.getHour() : String.valueOf(dateTime.getHour());

        return hours + ":" + minutes;
    }
}
