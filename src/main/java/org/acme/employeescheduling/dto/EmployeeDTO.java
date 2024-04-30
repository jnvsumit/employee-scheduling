package org.acme.employeescheduling.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeDTO {
    private String name;
    private String position;
    private String domain;
    private List<String> skills;
    private List<ScheduleDTO> schedules;

    // Getters and setters
}
