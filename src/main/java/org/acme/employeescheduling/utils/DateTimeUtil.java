package org.acme.employeescheduling.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateTimeUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public static LocalTime toLocalTime(String dateStr) {
        return LocalTime.parse(dateStr, formatter);
    }

    public static LocalDateTime toLocalDateTime(String dateStr) {
        return LocalDate.parse(dateStr).atStartOfDay();
    }

    public static int getISTWeekNumber(LocalDateTime localDateTime) {
        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        WeekFields weekFields = WeekFields.of(Locale.ENGLISH);
        return localDateTime.atZone(istZone).get(weekFields.weekOfWeekBasedYear());
    }

    public static DayOfWeek getDayOfWeek(LocalDateTime localDateTime) {
        return localDateTime.getDayOfWeek();
    }
}
