package org.acme.employeescheduling.domain;

import java.time.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Schedule {

    private LocalDateTime start;
    private LocalDateTime endTime;
}
