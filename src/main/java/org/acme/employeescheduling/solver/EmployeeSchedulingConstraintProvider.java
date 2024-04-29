package org.acme.employeescheduling.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;
import org.acme.employeescheduling.domain.Shift;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EmployeeSchedulingConstraintProvider implements ConstraintProvider {

    private static int getMinuteOverlap(Shift shift1, Shift shift2) {
        // The overlap of two timeslot occurs in the range common to both timeslots.
        // Both timeslots are active after the higher of their two start times,
        // and before the lower of their two end times.
        LocalDateTime shift1Start = shift1.getStart();
        LocalDateTime shift1End = shift1.getEnd();
        LocalDateTime shift2Start = shift2.getStart();
        LocalDateTime shift2End = shift2.getEnd();
        return (int) Duration.between((shift1Start.compareTo(shift2Start) > 0) ? shift1Start : shift2Start,
                (shift1End.compareTo(shift2End) < 0) ? shift1End : shift2End).toMinutes();
    }


    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                requiredSkill(constraintFactory),
                atLeast10HoursBetweenTwoShifts(constraintFactory),
                oneShiftPerDay(constraintFactory),
                noOverlappingShifts(constraintFactory),
        };
    }

    /*Constraint matchEmployeeStartTime(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .filter(shift -> {
                    // Get the shift's start time
                    LocalTime shiftStartTime = shift.getStart().toLocalTime();
                    // Check if the shift's start time falls within any of the employee's available start time ranges
                    return shift.getEmployee().getAvailabilities().stream()
                            .anyMatch(availability ->
                                    shiftStartTime.compareTo(LocalTime.parse(availability.getStartTime())) >= 0 &&
                                            shiftStartTime.compareTo(LocalTime.parse(availability.getEndTime())) <= 0);
                })
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Mismatched employee start time and shift start time");
    }*/

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

}
