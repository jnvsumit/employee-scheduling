package org.acme.employeescheduling.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;
import org.acme.employeescheduling.domain.Availability;
import org.acme.employeescheduling.domain.AvailabilityType;
import org.acme.employeescheduling.domain.Employee;
import org.acme.employeescheduling.domain.Shift;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

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
                domainMappedStoreName(constraintFactory),
                unavailableEmployee(constraintFactory),
                desiredDayForEmployee(constraintFactory)
//                matchShiftStartTimeWithEmployeeAvailability(constraintFactory)
        };
    }

    /*Constraint domainMappedStoreName(ConstraintFactory constraintFactory){
        return constraintFactory.forEach(Shift.class)
                .filter(shift -> !shift.getStoreType().equals(shift.getEmployee().getDomain()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Unmatched domain and StoreName");
    }*/
    Constraint domainMappedStoreName(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .filter(shift -> {
                    Employee employee = shift.getEmployee();
                    if (employee == null) {
                        return true; // No employee assigned; constraint satisfied
                    }
                    return !shift.getStoreType().equals(employee.getDomain()) || !employee.getSkills().contains(shift.getRequiredSkill());
                })
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Unmatched domain and StoreName or Missing required skill");
    }
//    Constraint matchShiftStartTimeWithEmployeeAvailability(ConstraintFactory constraintFactory) {
//        return constraintFactory.forEach(Shift.class)
//                .filter(shift -> {
//                    // Retrieve the start time of the shift
//                    LocalTime shiftStartTime = shift.getStart().toLocalTime();
//                    // Check if any availability of the employee matches the shift start time
//                    return !(shift.getEmployee().getAvailabilities().stream()
//                            .anyMatch(availability ->
//                                    availability.getStartTime().equals(shiftStartTime)));
//                })
//                .penalize(HardSoftScore.ONE_HARD)
//                .asConstraint("Shift start time doesn't match employee availability");
//    }
    Constraint noOverlappingShifts(ConstraintFactory constraintFactory) {
        return constraintFactory.forEachUniquePair(Shift.class, Joiners.equal(Shift::getEmployee),
                        Joiners.overlapping(Shift::getStart, Shift::getEnd))
                .penalize(HardSoftScore.ONE_HARD,
                        EmployeeSchedulingConstraintProvider::getMinuteOverlap)
                .asConstraint("Overlapping shift");
    }

    /*Constraint requiredSkill(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .filter(shift -> !shift.getEmployee().getSkills().contains(shift.getRequiredSkill()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Missing required skill");
    }*/

    Constraint requiredSkill(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .filter(shift -> shift.getEmployee() != null && !shift.getEmployee().getSkills().contains(shift.getRequiredSkill()))
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
                            return (12 * 60) - breakLength;
                        })
                .asConstraint("At least 10 hours between 2 shifts");
    }

    Constraint oneShiftPerDay(ConstraintFactory constraintFactory) {

        return constraintFactory.forEachUniquePair(Shift.class, Joiners.equal(Shift::getEmployee),
                        Joiners.equal(shift -> shift.getStart().getNano()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Max one shift per day");
    }

    private static int getShiftDurationInMinutes(Shift shift) {
        return (int) Duration.between(shift.getStart(), shift.getEnd()).toMinutes();
    }


    Constraint unavailableEmployee(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .join(Availability.class, Joiners.equal((Shift shift) -> shift.getStart().toLocalDate(), Availability::getDate),
                        Joiners.equal(Shift::getEmployee, Availability::getEmployee))
                .filter((shift, availability) -> availability.getAvailabilityType() == AvailabilityType.UNAVAILABLE)
                .penalize(HardSoftScore.ONE_HARD,
                        (shift, availability) -> getShiftDurationInMinutes(shift))
                .asConstraint("Unavailable employee");
    }


    Constraint desiredDayForEmployee(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .join(Availability.class, Joiners.equal((Shift shift) -> shift.getStart().toLocalDate(), Availability::getDate),
                        Joiners.equal(Shift::getEmployee, Availability::getEmployee))
                .filter((shift, availability) -> availability.getAvailabilityType() == AvailabilityType.DESIRED)
                .reward(HardSoftScore.ONE_SOFT,
                        (shift, availability) -> getShiftDurationInMinutes(shift))
                .asConstraint("Desired day for employee");
    }

    Constraint undesiredDayForEmployee(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Shift.class)
                .join(Availability.class, Joiners.equal((Shift shift) -> shift.getStart().toLocalDate(), Availability::getDate),
                        Joiners.equal(Shift::getEmployee, Availability::getEmployee))
                .filter((shift, availability) -> availability.getAvailabilityType() == AvailabilityType.UNDESIRED)
                .penalize(HardSoftScore.ONE_SOFT,
                        (shift, availability) -> getShiftDurationInMinutes(shift))
                .asConstraint("Undesired day for employee");
    }

}
