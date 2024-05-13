package org.acme.employeescheduling.dto;

import lombok.Data;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class EmployeesScheduleDTO {

    private String name;
    private String position;
    private String domain;
    private Set<String> skills;
    private List<EmployeeScheduleDTO> schedules;
}
