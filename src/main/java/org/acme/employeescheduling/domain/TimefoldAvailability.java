package org.acme.employeescheduling.domain;

import java.time.LocalDate;
import java.util.Objects;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;

public class TimefoldAvailability {

//    @PlanningId
//    private String id;

    private Employee employee;

    private LocalDate date;

    private String availabilityType;
    private TimefoldAvailability() {
    }

    public TimefoldAvailability( Employee employee, LocalDate date, String availabilityType) {
//        this.id = id;
        this.employee = employee;
        this.date = date;
        this.availabilityType = availabilityType;
    }

//    public String getId() {
//        return id;
//    }

//    public void setId(String id) {
//        this.id = id;
//    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate localDate) {
        this.date = localDate;
    }

    public String getAvailabilityType() {
        return availabilityType;
    }

    public void setAvailabilityType(String availabilityType) {
        this.availabilityType = availabilityType;
    }

    @Override
    public String toString() {
        return availabilityType + "(" + employee + ", " + date + ")";
    }

    /*@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Availability that)) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }*/
}
