package org.acme.employeescheduling.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.*;
import org.acme.employeescheduling.domain.Availability;
import org.acme.employeescheduling.domain.Schedule;
import org.acme.employeescheduling.domain.Shift;
import org.acme.employeescheduling.utils.DateTimeUtil;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeSchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                requiredSkill(constraintFactory),
                requiredDepartment(constraintFactory),
                atLeast10HoursBetweenTwoShifts(constraintFactory),
//                oneShiftPerDay(constraintFactory),
                noOverlappingShifts(constraintFactory),
//                matchShiftStartTimeAndShiftEndTimeWithEmployeeAvailability(constraintFactory),
                rotateEmployeeShiftWeekly(constraintFactory),
//                unavailableEmployee(constraintFactory)
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
        return constraintFactory.forEach(Shift.class)
                .filter(shift -> Objects.nonNull(shift) && Objects.nonNull(shift.getEmployee()))
                .join(constraintFactory.forEach(Shift.class)
                        .filter(shift -> Objects.nonNull(shift) && Objects.nonNull(shift.getEmployee()))
                )
                .filter((firstShift, secondShift) -> {
                    if (Objects.isNull(firstShift) || Objects.isNull(secondShift)) {
                        System.out.println("Encountered null shift(s) in constraint processing: firstShift=" + firstShift + ", secondShift=" + secondShift);
                        return false;
                    }

                    return !Objects.equals(firstShift.getId(), secondShift.getId()) &&
                            Objects.equals(firstShift.getEmployee().getName(), secondShift.getEmployee().getName()) &&
                            Math.abs(Duration.between(firstShift.getEnd(), secondShift.getStart()).toHours()) < 10;
                })
                .penalize(HardSoftScore.ONE_HARD,
                        (firstShift, secondShift) -> {
                            int breakLength = (int) Math.abs(Duration.between(firstShift.getEnd(), secondShift.getStart()).toMinutes());
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

    Constraint rotateEmployeeShiftWeekly(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .filter(shift -> Objects.nonNull(shift) && Objects.nonNull(shift.getEmployee()))
                .groupBy(shift -> shift.getEmployee().getName(), ConstraintCollectors.toList())
                .penalize(HardSoftScore.ONE_HARD, (employeeName, shiftList) -> {
                    Map<Integer, Integer> shiftMap = new HashMap<>();

                    for (Shift shift : shiftList) {
                        int week = shift.getStart().getDayOfYear() / 7;
                        shiftMap.put(week, shiftMap.getOrDefault(week, 0) + 1);
                    }

                    int penalty = 0;

                    int prevWeek = Integer.MIN_VALUE / 2;

                    for (int week : shiftMap.keySet().stream().sorted().toList()) {
                        if (week - prevWeek == 1) {
                            penalty += shiftMap.get(week);
                        }

                        prevWeek = week;
                    }

                    return penalty;
                })
                .asConstraint("Rotate employee shifts weekly");
    }

    Constraint unavailableEmployee(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .filter(shift -> Objects.nonNull(shift) && Objects.nonNull(shift.getEmployee()))
                .join(constraintFactory.forEach(Availability.class)
                        .filter(availability -> Objects.nonNull(availability) && Objects.nonNull(availability.getEmployee()))
                )
                .filter((shift, availability) -> shift.getEmployee().getName().equals(availability.getEmployee().getName()))
                .filter((shift, availability) -> {
                    LocalDateTime shiftStartTime = shift.getStart();
                    LocalDateTime shiftEndTime = shift.getEnd();

                    for (Schedule schedule : availability.getSchedules()) {
                        LocalDateTime scheduleStartTime = schedule.getStart();
                        LocalDateTime scheduleEndTime = schedule.getEndTime();

                        // Check if the shift overlaps with the schedule
                        if (shiftStartTime.isBefore(scheduleEndTime) && shiftEndTime.isAfter(scheduleStartTime)) {
                            return true; // Shift overlaps with unavailable time
                        }
                    }

                    return false;
                })
                .penalize(HardSoftScore.ONE_HARD, ((shift, availability) -> 1000))
                .indictWith((shift, availability) -> null)
                .asConstraint("Unavailable employee", "Can be assigned a null value");
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
