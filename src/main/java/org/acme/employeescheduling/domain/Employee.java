package org.acme.employeescheduling.domain;

import java.util.Objects;
import java.util.*;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.employeescheduling.domain.enums.Department;
import org.acme.employeescheduling.domain.enums.Skill;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private UUID id;

    @PlanningId
    private String name;

    private Set<Skill> skills;

    private Department department;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee employee)) {
            return false;
        }
        return Objects.equals(getName(), employee.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
