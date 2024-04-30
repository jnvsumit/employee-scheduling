package org.acme.employeescheduling.domain;
import lombok.Data;
@Data
public class RequiredSkill {
    private String skillName;
    private int minimumEmployeeCount;

}