package org.acme.employeescheduling.domain;
import lombok.Data;

import java.util.*;
@Data
public class RequiredShift {
    private String startTime;
    private String endTime;
    private int employeeCount;
    private List<RequiredSkill> requiredSkills;

    // Getters and setters
}


