package org.acme.employeescheduling.dto;

import java.util.*;
import lombok.Data;
@Data
public class RequiredShiftDTO {
    private String start_time;
    private String end_time;
    private int employee_count;
    private List<RequiredSkillDTO> required_skills;

    // Getters and setters
}
