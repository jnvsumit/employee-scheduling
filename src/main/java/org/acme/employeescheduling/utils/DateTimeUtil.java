package org.acme.employeescheduling.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public static LocalTime toLocalTime(String dateStr) {
        return LocalTime.parse(dateStr, formatter);
    }

    public static LocalDateTime toLocalDateTime(String dateStr) {
        return LocalDate.parse(dateStr).atStartOfDay();
    }
}
