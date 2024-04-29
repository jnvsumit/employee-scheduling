package org.acme.employeescheduling.domain;

import java.util.Objects;
import java.util.*;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @PlanningId
    private String name;

    private Set<String> skills;

    private StoreName domain;
    private List<Schedule> availabilities;


    public Employee(String name, Set<String> skills) {
        this.name = name;
        this.skills = skills;
    }

    @Override
    public String toString() {
        return name;
    }

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
