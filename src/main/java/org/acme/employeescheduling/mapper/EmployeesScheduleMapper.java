package org.acme.employeescheduling.mapper;

import org.acme.employeescheduling.domain.*;
import org.acme.employeescheduling.domain.enums.Day;
import org.acme.employeescheduling.domain.enums.Department;
import org.acme.employeescheduling.domain.enums.Skill;
import org.acme.employeescheduling.dto.*;
import org.acme.employeescheduling.utils.DateTimeUtil;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class EmployeesScheduleMapper {

    private static final Logger logger = Logger.getLogger(EmployeesScheduleMapper.class.getName());


    public static EmployeeSchedule toEmployeeSchedule(List<EmployeeDTO> a, List<ShiftDTO> b, LocalDateTime start, LocalDateTime end) {
        return EmployeeSchedule
                .builder()
                .availabilities(getAvailabilities(a, start, end))
                .employees(getEmployees(a))
                .shifts(getShifts(b, start, end))
                .build();
    }

    private static List<Availability> getAvailabilities(List<EmployeeDTO> dto, LocalDateTime start, LocalDateTime end) {
        return dto.stream().map(e -> Availability
                .builder()
                .id(Availability.generateId())
                .employee(getEmployee(e))
                .schedules(getAvailabilityOfEmployee(e, start, end))
                .build()).collect(Collectors.toList());
    }

    private static List<Employee> getEmployees(List<EmployeeDTO> dto) {
        return dto.stream().map(EmployeesScheduleMapper::getEmployee).collect(Collectors.toList());
    }

    private static List<Shift> getShifts(List<ShiftDTO> dto, LocalDateTime start, LocalDateTime end) {
        List<Shift> shifts = new ArrayList<>();

        for (ShiftDTO shiftDTO : dto) {
            for (RequiredShift requiredShift : shiftDTO.getRequiredShifts()) {
                for (RequiredSkill requiredSkill : requiredShift.getRequiredSkills()) {
                    for (int i = 0; i < requiredSkill.getMinimumEmployeeCount(); i++) {
                        LocalDateTime current = start;

                        while (!current.isAfter(end)) {
                            LocalDateTime startDateTime = LocalDateTime.of(current.toLocalDate(), DateTimeUtil.toLocalTime(requiredShift.getStartTime()));
                            LocalDateTime endDateTime = LocalDateTime.of(current.toLocalDate(), DateTimeUtil.toLocalTime(requiredShift.getEndTime()));

                            Shift shift = Shift
                                    .builder()
                                    .id(Shift.generateId())
                                    .start(startDateTime)
                                    .end(endDateTime)
                                    .department(Department.valueOf(shiftDTO.getStoreType()))
                                    .requiredSkill(Skill.valueOf(requiredSkill.getSkillName()))
                                    .build();

                            shifts.add(shift);

                            current = current.plusDays(1);
                        }
                    }
                }
            }
        }

        return shifts;
    }

    private static Employee getEmployee(EmployeeDTO dto) {
        return Employee
                .builder()
                .name(dto.getName())
                .skills(dto.getSkills().stream().map(Skill::valueOf).collect(Collectors.toSet()))
                .department(Department.valueOf(dto.getDomain()))
                .build();
    }

    private static List<Schedule>  getAvailabilityOfEmployee(EmployeeDTO dto, LocalDateTime start, LocalDateTime end) {
        List<Schedule> availabilities = new ArrayList<>();

        Map<Day, DayOfWeek> dayDayOfWeekMap = Map.of(
                Day.MO, DayOfWeek.MONDAY,
                Day.TU, DayOfWeek.TUESDAY,
                Day.WE, DayOfWeek.WEDNESDAY,
                Day.TH, DayOfWeek.THURSDAY,
                Day.FR, DayOfWeek.FRIDAY,
                Day.SA, DayOfWeek.SATURDAY,
                Day.SU, DayOfWeek.SUNDAY
        );

        for (ScheduleDTO scheduleDTO : dto.getSchedules()) {
            String startTime = scheduleDTO.getStartTime();
            String endTime = scheduleDTO.getEndTime();

            List<DayOfWeek> employeeAvailableDays = scheduleDTO.getDays().stream().map(Day::valueOf).map(dayDayOfWeekMap::get).toList();

            LocalDateTime current = start;

            while (!current.isAfter(end)) {
                LocalDateTime startDateTime = LocalDateTime.of(current.toLocalDate(), DateTimeUtil.toLocalTime(startTime));
                LocalDateTime endDateTime = LocalDateTime.of(current.toLocalDate(), DateTimeUtil.toLocalTime(endTime));

                if (employeeAvailableDays.contains(startDateTime.getDayOfWeek())) {
                    availabilities.add(
                            Schedule
                                    .builder()
                                    .start(startDateTime)
                                    .endTime(endDateTime)
                                    .build()
                    );
                }

                current = current.plusDays(1);
            }
        }

        return availabilities;
    }
}
