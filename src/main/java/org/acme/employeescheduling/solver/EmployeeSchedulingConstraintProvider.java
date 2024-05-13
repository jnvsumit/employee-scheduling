package org.acme.employeescheduling.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;
import org.acme.employeescheduling.domain.Availability;
import org.acme.employeescheduling.domain.Schedule;
import org.acme.employeescheduling.domain.Shift;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class EmployeeSchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                requiredSkill(constraintFactory),
                requiredDepartment(constraintFactory),
                atLeast10HoursBetweenTwoShifts(constraintFactory),
                oneShiftPerDay(constraintFactory),
                noOverlappingShifts(constraintFactory),
                matchShiftStartTimeAndShiftEndTimeWithEmployeeAvailability(constraintFactory),
                rotateEmployeeShifts(constraintFactory)
        };
    }

    Constraint matchShiftStartTimeAndShiftEndTimeWithEmployeeAvailability(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .join(Availability.class, Joiners.equal(Shift::getEmployee, Availability::getEmployee))
                .filter((shift, availability) -> {
                    LocalDateTime shiftStartTime = shift.getStart();
                    LocalDateTime shiftEndTime = shift.getEnd();

                    for (Schedule schedule : availability.getSchedules()) {
                        if ((schedule.getStart().isBefore(shiftStartTime) || schedule.getStart().isEqual(shiftStartTime)) && (schedule.getEndTime().isAfter(shiftEndTime) || schedule.getEndTime().isEqual(shiftEndTime))) {
                            return false;
                        }
                    }

                    return true;
                })
                .penalize(HardSoftScore.ONE_SOFT,
                        (shift, availability) -> getShiftDurationInMinutes(shift))
                .asConstraint("Shift start time doesn't match employee availability");
    }

    Constraint noOverlappingShifts(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(Shift.class, Joiners.equal(Shift::getEmployee),
                        Joiners.overlapping(Shift::getStart, Shift::getEnd))
                .penalize(HardSoftScore.ONE_HARD,
                        EmployeeSchedulingConstraintProvider::getMinuteOverlap)
                .asConstraint("Overlapping shift");
    }

    Constraint requiredSkill(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .filter(shift -> !shift.getEmployee().getSkills().contains(shift.getRequiredSkill()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Missing required skill");
    }

    Constraint requiredDepartment(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .filter(shift -> !shift.getEmployee().getDepartment().equals(shift.getDepartment()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Department does not match");
    }

    Constraint atLeast10HoursBetweenTwoShifts(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(Shift.class,
                        Joiners.equal(Shift::getEmployee),
                        Joiners.lessThanOrEqual(Shift::getEnd, Shift::getStart))
                .filter((firstShift, secondShift) -> Duration.between(firstShift.getEnd(), secondShift.getStart()).toHours() < 10)
                .penalize(HardSoftScore.ONE_HARD,
                        (firstShift, secondShift) -> {
                            int breakLength = (int) Duration.between(firstShift.getEnd(), secondShift.getStart()).toMinutes();
                            return (10 * 60) - breakLength;
                        })
                .asConstraint("At least 10 hours between 2 shifts");
    }

    Constraint oneShiftPerDay(ConstraintFactory constraintFactory) {

        return constraintFactory.forEachUniquePair(Shift.class, Joiners.equal(Shift::getEmployee),
                        Joiners.equal(shift -> shift.getStart().getNano()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Max one shift per day");
    }

    Constraint rotateEmployeeShifts(ConstraintFactory constraintFactory) {
        Map<String, LocalDateTime> lastShiftEndTime = new HashMap<>();
        Map<String, LocalDateTime> lastShiftStartTime = new HashMap<>();

        return constraintFactory.forEach(Shift.class)
                .groupBy(Shift::getEmployee, Shift::getDepartment)
                .join(Shift.class)
                .penalize(HardSoftScore.ONE_SOFT, (employee, department, shift) -> {
                    double penalizeCount = 0.0;
                    String key = employee.getName() + ":" + department.name();

                    if (lastShiftStartTime.containsKey(key)) {
                        if (lastShiftStartTime.get(key).isEqual(shift.getStart())) penalizeCount+=0.5;
                    }

                    if (lastShiftEndTime.containsKey(key)) {
                        if (lastShiftEndTime.get(key).isEqual(shift.getEnd())) penalizeCount+=0.5;
                    }

                    lastShiftStartTime.put(key, shift.getStart());
                    lastShiftEndTime.put(key, shift.getStart());

                    return (int) Math.ceil(penalizeCount);
                })
                .asConstraint("Rotate employee shifts");
    }

    private static int getMinuteOverlap(Shift shift1, Shift shift2) {
        LocalDateTime shift1Start = shift1.getStart();
        LocalDateTime shift1End = shift1.getEnd();
        LocalDateTime shift2Start = shift2.getStart();
        LocalDateTime shift2End = shift2.getEnd();
        return (int) Duration.between((shift1Start.isAfter(shift2Start)) ? shift1Start : shift2Start,
                (shift1End.isBefore(shift2End)) ? shift1End : shift2End).toMinutes();
    }

    private static int getShiftDurationInMinutes(Shift shift) {
        return (int) Duration.between(shift.getStart(), shift.getEnd()).toMinutes();
    }
}
