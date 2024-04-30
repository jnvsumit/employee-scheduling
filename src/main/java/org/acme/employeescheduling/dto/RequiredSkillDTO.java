package org.acme.employeescheduling.dto;

import lombok.Data;
@Data
public class RequiredSkillDTO {
    private String skill_name;
    private int minimum_employee_count;

    // Getters and setters
}
