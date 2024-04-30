package org.acme.employeescheduling.domain;
import java.util.*;
import java.time.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
//@AllArgsConstructor
public class Schedule {
    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    //    private List<String> days;
    private LocalTime startTime;
    private LocalTime endTime;

    public Schedule(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Constructor, getters, and setters
}
