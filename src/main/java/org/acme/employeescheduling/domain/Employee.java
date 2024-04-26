package org.acme.employeescheduling.domain;

import java.util.Objects;
import java.util.*;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Employee {
    @PlanningId
    private String name;

    private Set<Skill> skills;

    private StoreName domain;

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
