package org.acme.employeescheduling.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ScheduleState {

    private String tenantId;

    private Integer publishLength; // In number of days

    private Integer draftLength; // In number of days

    private LocalDate firstDraftDate;

    private LocalDate lastHistoricDate;

    @JsonIgnore
    public boolean isHistoric(LocalTime time) {
        return time.isBefore(LocalTime.MIDNIGHT);
    }

    @JsonIgnore
    public boolean isDraft(LocalTime dateTime) {
        return !dateTime.isBefore(LocalTime.MIDNIGHT);
    }

    @JsonIgnore
    public boolean isPublished(LocalTime dateTime) {
        return !isHistoric(dateTime) && !isDraft(dateTime);
    }

    /*@JsonIgnore
    public boolean isHistoric(Shift shift) {
        return isHistoric(shift.getStart());
    }

    @JsonIgnore
    public boolean isDraft(Shift shift) {
        return isDraft(shift.getStart());
    }

    @JsonIgnore
    public boolean isPublished(Shift shift) {
        return isPublished(shift.getStart());
    }*/

    @JsonIgnore
    public LocalDate getFirstPublishedDate() {
        return lastHistoricDate.plusDays(1);
    }

    @JsonIgnore
    public LocalDate getFirstUnplannedDate() {
        return firstDraftDate.plusDays(draftLength);
    }
}
