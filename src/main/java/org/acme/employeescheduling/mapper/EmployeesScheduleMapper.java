package org.acme.employeescheduling.mapper;

import org.acme.employeescheduling.domain.*;
import org.acme.employeescheduling.dto.EmployeesScheduleDTO;
import org.acme.employeescheduling.dto.ShiftDTO;
import org.acme.employeescheduling.utils.DateTimeUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



public class EmployeesScheduleMapper {
    public static EmployeeSchedule toEmployeeSchedule(List<EmployeesScheduleDTO> dtoE, List<ShiftDTO> dtoS) {
        return EmployeeSchedule
                .builder()
                .availabilities(getAvailabilities(dtoE))
                .employees(getEmployees(dtoE))
                .shifts(getShifts(dtoS))
                .build();
    }

    private static List<Availability> getAvailabilities(List<EmployeesScheduleDTO> dto) {
        return dto.stream().map(e -> Availability
                .builder()
                .employee(getEmployee(e))
                .startTime(getStartTime(e))
                .endTime(getEndTime(e))
                .availabilityOnDay(getAvailabilityOnDay(e))
                .build()).collect(Collectors.toList());
    }

    private static List<Employee> getEmployees(List<EmployeesScheduleDTO> dto) {
        return dto.stream().map(EmployeesScheduleMapper::getEmployee).collect(Collectors.toList());
    }

    private static List<Shift> getShifts(List<ShiftDTO> dto) {
        return dto.stream().map(EmployeesScheduleMapper::getShift).collect(Collectors.toList());
    }

    private static Employee getEmployee(EmployeesScheduleDTO dto) {
        return Employee
                .builder()
                .name(dto.getName())
                .skills(dto.getSkills().stream().map(Skill::valueOf).collect(Collectors.toSet()))
                .domain(StoreName.valueOf(dto.getDomain()))
                .build();
    }

    private static Shift getShift(ShiftDTO dto) {
        return Shift
                .builder()
                .start(DateTimeUtil.toLocalTime(dto.getRequiredShifts().get(0).getStartTime()))
                .end(DateTimeUtil.toLocalTime(dto.getRequiredShifts().get(0).getEndTime()))
                .storeName(StoreName.valueOf(dto.getStoreType()))
                .requiredSkill(Skill.valueOf(dto.getRequiredSkills().get(0).getSkillName()))
                .build();
    }

    private static LocalTime getStartTime(EmployeesScheduleDTO dto) {
        return DateTimeUtil.toLocalTime(dto.getSchedules().get(0).getSchedule().get(0).getStartTime());
    }

    private static LocalTime getEndTime(EmployeesScheduleDTO dto) {
        return DateTimeUtil.toLocalTime(dto.getSchedules().get(0).getSchedule().get(0).getEndTime());
    }

    private static Set<AvailabilityOnDay> getAvailabilityOnDay(EmployeesScheduleDTO dto) {
        return dto.getSchedules().get(0).getSchedule().get(0).getDays().stream().map(AvailabilityOnDay::valueOf).collect(Collectors.toSet());
    }
}
