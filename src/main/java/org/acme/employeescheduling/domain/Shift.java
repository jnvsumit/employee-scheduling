package org.acme.employeescheduling.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PlanningEntity(pinningFilter = ShiftPinningFilter.class)
public class Shift {

    @PlanningId
    private String id;

    private String day;
    private LocalDateTime start;
    private LocalDateTime end;

    private String storeType;
    private String requiredSkill;

    @PlanningVariable
    private Employee employee;

    public Shift(String day,LocalDateTime start, LocalDateTime end, String storeType, String requiredSkill) {
        this.day = day;
        this.start = start;
        this.end = end;
        this.storeType = storeType;
        this.requiredSkill = requiredSkill;
    }

    public Shift(String id, LocalDateTime start, LocalDateTime end, String storeType, String requiredSkill, Employee employee) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.storeType = storeType;
        this.requiredSkill = requiredSkill;
        this.employee = employee;
    }



//    @Override
//    public String toString() {
//        return start + "-" + end;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Shift shift)) {
            return false;
        }
        return Objects.equals(getId(), shift.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
    public static String generateId(){

        UUID uuid= UUID.randomUUID();

        return String.valueOf(uuid);
    }
}
