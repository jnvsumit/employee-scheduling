package org.acme.employeescheduling.domain;
import java.util.*;
import java.time.*;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class Schedule {
    private List<String> days;
    private LocalTime startTime;
    private LocalTime endTime;

    // Constructor, getters, and setters
}
