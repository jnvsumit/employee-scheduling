package org.acme.employeescheduling.filters;

import ai.timefold.solver.core.api.domain.entity.PinningFilter;
import org.acme.employeescheduling.domain.EmployeeSchedule;
import org.acme.employeescheduling.domain.ScheduleState;
import org.acme.employeescheduling.domain.Shift;

import java.time.DayOfWeek;
import java.util.Objects;

public class ShiftPinningFilter implements PinningFilter<EmployeeSchedule, Shift> {

    @Override
    public boolean accept(EmployeeSchedule employeeSchedule, Shift shift) {
        return Objects.isNull(shift) || Objects.isNull(shift.getStart()) || DayOfWeek.SUNDAY.equals(shift.getStart().getDayOfWeek());
    }
}
