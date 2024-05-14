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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setSkills(List<String> skills) {
        skills = skills;
    }

    public void setSchedules(List<ScheduleDTO> schedules) {
        this.schedules = schedules;
    }

    public String getPosition() {
        return position;
    }

    public String getDomain() {
        return domain;
    }

    public Set<String> getSkills() {
        return skills;
    }
    public EmployeesScheduleDTO(String name, String position, String domain, List<String> skills, List<ScheduleDTO> schedules) {
        this.name = name;
        this.position = position;
        this.domain = domain;
        this.skills = new HashSet<>(skills); // Ensure to copy the provided skills to a new Set
        this.schedules = schedules;
    }
    public List<ScheduleDTO> getSchedules() {
        return schedules;
    }

    public EmployeesScheduleDTO(String name, String position, String domain, Set<String> skills, List<ScheduleDTO> schedules) {
        this.name = name;
        this.position = position;
        this.domain = domain;
        this.skills = skills;
        this.schedules = schedules;
    }

    private Set<String> skills;
    private List<ScheduleDTO> schedules;
}
