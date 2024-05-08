package org.acme.employeescheduling.solver;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import jakarta.inject.Inject;

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;

import org.acme.employeescheduling.domain.*;
//import org.acme.employeescheduling.domain.AvailabilityType;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class EmployeeSchedulingConstraintProviderTest {
    private static final LocalDate DAY_1 = LocalDate.of(2021, 2, 1);

    private static final LocalDateTime DAY_START_TIME = DAY_1.atTime(LocalTime.of(9, 0));
    private static final LocalDateTime DAY_END_TIME = DAY_1.atTime(LocalTime.of(17, 0));
    private static final LocalDateTime AFTERNOON_START_TIME = DAY_1.atTime(LocalTime.of(13, 0));
    private static final LocalDateTime AFTERNOON_END_TIME = DAY_1.atTime(LocalTime.of(21, 0));

    @Inject
    ConstraintVerifier<EmployeeSchedulingConstraintProvider, EmployeeSchedule> constraintVerifier;

    @Test
    void testRequiredSkill() {
        Employee employee = new Employee("Amy", Set.of(""));
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::requiredSkill)
                .given(employee,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee))
                .penalizes(1);

        employee = new Employee("Beth", Set.of("Skill"));
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::requiredSkill)
                .given(employee,
                        new Shift("2", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee))
                .penalizes(0);
    }

//    @Test
//    void testRequiredDomain() {
//        Employee employee = new Employee("Amy", Set.of(""));
//        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::requiredSkill)
//                .given(employee,
//                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee))
//                .penalizes(1);
//
//        employee = new Employee("Beth", Set.of("Skill"));
//        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::requiredSkill)
//                .given(employee,
//                        new Shift("2", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee))
//                .penalizes(0);
//    }

//    @Test
//    void testMatchShiftStartTimeWithEmployeeAvailability() {
//        // Create employees with different availabilities
//        Employee employee1 = new Employee("John", Set.of(), null, List.of(new Schedule(LocalTime.of(8, 0), LocalTime.of(15, 0))));
//        Employee employee2 = new Employee("Alice", Set.of(), null, List.of(new Schedule(LocalTime.of(12, 0), LocalTime.of(20, 0))));
//
//        // Shifts with start times matching and not matching employees' availabilities
//        LocalDateTime matchingStartTime = LocalDate.now().atTime(LocalTime.of(8, 0));
//        LocalDateTime nonMatchingStartTime = LocalDate.now().atTime(LocalTime.of(10, 0));
//
//        // Verify constraint behavior
//        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::matchShiftStartTimeWithEmployeeAvailability)
//                .given(employee1, new Shift("1", matchingStartTime, matchingStartTime.plusHours(8), "Location", "Skill", employee1))
//                .penalizes(0);
//
//        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::matchShiftStartTimeWithEmployeeAvailability)
//                .given(employee1, new Shift("2", nonMatchingStartTime, nonMatchingStartTime.plusHours(8), "Location", "Skill", employee1))
//                .penalizes(1);
//
//        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::matchShiftStartTimeWithEmployeeAvailability)
//                .given(employee2, new Shift("3", matchingStartTime, matchingStartTime.plusHours(8), "Location", "Skill", employee2))
//                .penalizes(0);
//
//        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::matchShiftStartTimeWithEmployeeAvailability)
//                .given(employee2, new Shift("4", nonMatchingStartTime, nonMatchingStartTime.plusHours(8), "Location", "Skill", employee2))
//                .penalizes(1);
//    }
    @Test
    void testOverlappingShifts() {
        Employee employee1 = new Employee("Amy", Set.of("Skill"));
        Employee employee2 = new Employee("Beth", Set.of("Skill"));
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::noOverlappingShifts)
                .given(employee1,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1),
                        new Shift("2", DAY_START_TIME, DAY_END_TIME, "Location 2", "Skill", employee1))
                .penalizesBy((int) Duration.ofHours(8).toMinutes());

        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::noOverlappingShifts)
                .given(employee1,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1),
                        new Shift("2", DAY_START_TIME, DAY_END_TIME, "Location 2", "Skill", employee2))
                .penalizes(0);

        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::noOverlappingShifts)
                .given(employee1,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1),
                        new Shift("2", AFTERNOON_START_TIME, AFTERNOON_END_TIME, "Location 2", "Skill", employee1))
                .penalizesBy((int) Duration.ofHours(4).toMinutes());
    }

    @Test
    void testOneShiftPerDay() {
        Employee employee1 = new Employee("Amy", Set.of("Skill"));
        Employee employee2 = new Employee("Beth", Set.of("Skill"));
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::noOverlappingShifts)
                .given(employee1,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1),
                        new Shift("2", DAY_START_TIME, DAY_END_TIME, "Location 2", "Skill", employee1))
                .penalizes(1);

        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::noOverlappingShifts)
                .given(employee1,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1),
                        new Shift("2", DAY_START_TIME, DAY_END_TIME, "Location 2", "Skill", employee2))
                .penalizes(0);

        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::noOverlappingShifts)
                .given(employee1,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1),
                        new Shift("2", AFTERNOON_START_TIME, AFTERNOON_END_TIME, "Location 2", "Skill", employee1))
                .penalizes(1);

        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::noOverlappingShifts)
                .given(employee1,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1),
                        new Shift("2", DAY_START_TIME.plusDays(1), DAY_END_TIME.plusDays(1), "Location 2", "Skill", employee1))
                .penalizes(0);
    }

    @Test
    void testAtLeast10HoursBetweenConsecutiveShifts() {
        Employee employee1 = new Employee("Amy", Set.of("Skill"));
        Employee employee2 = new Employee("Beth", Set.of("Skill"));
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::atLeast10HoursBetweenTwoShifts)
                .given(employee1,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1),
                        new Shift("2", AFTERNOON_END_TIME, DAY_START_TIME.plusDays(1), "Location 2", "Skill", employee1))
                .penalizesBy(360);
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::atLeast10HoursBetweenTwoShifts)
                .given(employee1,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1),
                        new Shift("2", DAY_END_TIME, DAY_START_TIME.plusDays(1), "Location 2", "Skill", employee1))
                .penalizesBy(600);
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::atLeast10HoursBetweenTwoShifts)
                .given(employee1,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1),
                        new Shift("2", DAY_END_TIME.plusHours(10), DAY_START_TIME.plusDays(1), "Location 2", "Skill", employee1))
                .penalizes(0);
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::atLeast10HoursBetweenTwoShifts)
                .given(employee1,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1),
                        new Shift("2", AFTERNOON_END_TIME, DAY_START_TIME.plusDays(1), "Location 2", "Skill", employee2))
                .penalizes(0);
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::noOverlappingShifts)
                .given(employee1,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1),
                        new Shift("2", DAY_START_TIME.plusDays(1), DAY_END_TIME.plusDays(1), "Location 2", "Skill", employee1))
                .penalizes(0);
    }

    @Test
    void testUnavailableEmployee() {
        Employee employee1 = new Employee("Amy", Set.of("Skill"));
        Employee employee2 = new Employee("Beth", Set.of("Skill"));
        Availability unavailability = new Availability("1", employee1, DAY_1, AvailabilityType.UNAVAILABLE);
        Availability desired = new Availability("2", employee1, DAY_1, AvailabilityType.DESIRED);
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::unavailableEmployee)
                .given(employee1,
                        unavailability,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1))
                .penalizesBy((int) Duration.ofHours(8).toMinutes());
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::unavailableEmployee)
                .given(employee1,
                        unavailability,
                        new Shift("1", DAY_START_TIME.plusDays(1), DAY_END_TIME.plusDays(1), "Location", "Skill", employee1))
                .penalizes(0);
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::unavailableEmployee)
                .given(employee1,
                        unavailability,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee2))
                .penalizes(0);
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::unavailableEmployee)
                .given(employee1,
                        desired,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1))
                .penalizes(0);
    }

    @Test
    void testDesiredDayForEmployee() {
        Employee employee1 = new Employee("Amy", Set.of("Skill"));
        Employee employee2 = new Employee("Beth", Set.of("Skill"));
        Availability unavailability = new Availability("1", employee1, DAY_1, AvailabilityType.UNAVAILABLE);
        Availability desired = new Availability("2", employee1, DAY_1, AvailabilityType.DESIRED);
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::desiredDayForEmployee)
                .given(employee1,
                        desired,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1))
                .rewardsWith((int) Duration.ofHours(8).toMinutes());
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::desiredDayForEmployee)
                .given(employee1,
                        desired,
                        new Shift("1", DAY_START_TIME.plusDays(1), DAY_END_TIME.plusDays(1), "Location", "Skill", employee1))
                .rewards(0);
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::desiredDayForEmployee)
                .given(employee1,
                        desired,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee2))
                .rewards(0);
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::desiredDayForEmployee)
                .given(employee1,
                        unavailability,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1))
                .rewards(0);
    }

    @Test
    void testUndesiredDayForEmployee() {
        Employee employee1 = new Employee("Amy", Set.of("Skill"));
        Employee employee2 = new Employee("Beth", Set.of("Skill"));
        Availability unavailability = new Availability("1", employee1, DAY_1, AvailabilityType.UNAVAILABLE);
        Availability undesired = new Availability("2", employee1, DAY_1, AvailabilityType.UNDESIRED);
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::undesiredDayForEmployee)
                .given(employee1,
                        undesired,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1))
                .penalizesBy((int) Duration.ofHours(8).toMinutes());
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::undesiredDayForEmployee)
                .given(employee1,
                        undesired,
                        new Shift("1", DAY_START_TIME.plusDays(1), DAY_END_TIME.plusDays(1), "Location", "Skill", employee1))
                .penalizes(0);
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::undesiredDayForEmployee)
                .given(employee1,
                        undesired,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee2))
                .penalizes(0);
        constraintVerifier.verifyThat(EmployeeSchedulingConstraintProvider::undesiredDayForEmployee)
                .given(employee1,
                        unavailability,
                        new Shift("1", DAY_START_TIME, DAY_END_TIME, "Location", "Skill", employee1))
                .penalizes(0);
    }
}
