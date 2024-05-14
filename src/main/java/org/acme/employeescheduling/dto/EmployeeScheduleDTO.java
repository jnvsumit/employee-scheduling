package org.acme.employeescheduling.dto;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class EmployeeScheduleDTO {

    private List<String> days; // Correctly mapped to the JSON field "days"
    private LocalTime start_time; // Correctly mapped to the JSON field "start_time"
    private LocalTime end_time; // Correctly mapped to the JSON field "end_time"

}
