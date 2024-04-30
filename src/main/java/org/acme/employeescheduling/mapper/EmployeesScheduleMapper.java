package org.acme.employeescheduling.mapper;

import org.acme.employeescheduling.domain.*;
import org.acme.employeescheduling.dto.EmployeesScheduleDTO;
import org.acme.employeescheduling.dto.ShiftDTO;
import org.acme.employeescheduling.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.acme.employeescheduling.service.Main;


public class EmployeesScheduleMapper {

    private static final Logger logger = Logger.getLogger(EmployeesScheduleMapper.class.getName());


    public static EmployeeSchedule toEmployeeSchedule(List<EmployeesScheduleDTO> dtoE, List<Shift> shifts) {


        return EmployeeSchedule
                .builder()
                .availabilities(getAvailabilities(dtoE))
                .employees(getEmployees(dtoE))
                .shifts(shifts)
                .build();
    }

    private static List<Availability> getAvailabilities(List<EmployeesScheduleDTO> dto) {
        return dto.stream().map(e -> {
            logger.log(Level.INFO, "Converting EmployeesScheduleDTO to Employee: " + e.toString());

            return Availability
                    .builder()
                    .id(Availability.generateId())
                    .employee(getEmployee(e))
                    .startTime(getStartTime(e))
                    .endTime(getEndTime(e))
                    .availabilityOnDay(getAvailabilityOnDay(e))
                    .build();
        }).collect(Collectors.toList());
    }

    public static List<Employee> getEmployees(List<EmployeesScheduleDTO> dto) {
        logger.log(Level.INFO, "EmployeesScheduleDTO to Employee: " + dto.stream().map(EmployeesScheduleMapper::getEmployee).collect(Collectors.toList()));

        return dto.stream().map(EmployeesScheduleMapper::getEmployee).collect(Collectors.toList());
    }

   /* private static List<Shift> getShifts(List<ShiftDTO> dto) {
        logger.log(Level.INFO,"shift dto list"+dto);

        logger.log(Level.INFO,"===========>"+dto.stream().map(EmployeesScheduleMapper::getShift).collect(Collectors.toList()));
        return dto.stream().map(EmployeesScheduleMapper::getShift).collect(Collectors.toList());
    }
*/
    private static Employee getEmployee(EmployeesScheduleDTO dto) {


        logger.info("Scheduless------: " + dto.getSchedules().stream()
                .flatMap(scheduleDTO -> scheduleDTO.getSchedule().stream().toList().stream())+"\n");
//        logger.log(Level.INFO, "Employee schedule 1: " + dto.getSchedules().get(0));
//        logger.log(Level.INFO, "Employee schedule 2: " + dto.getSchedules().get(1).toString());

//        logger.log(Level.INFO, "Employee: " + dto.getSchedules().get(0).getSchedule().get(1).toString());

List<Schedule> availabilities = new ArrayList<>();
availabilities.add(new Schedule(DateTimeUtil.toLocalTime(dto.getSchedules().get(0).getSchedule().get(0).getStartTime()),DateTimeUtil.toLocalTime(dto.getSchedules().get(0).getSchedule().get(0).getEndTime())));
if(dto.getSchedules().size()>1){
    availabilities.add(new Schedule(DateTimeUtil.toLocalTime(dto.getSchedules().get(1).getSchedule().get(0).getStartTime()),DateTimeUtil.toLocalTime(dto.getSchedules().get(1).getSchedule().get(0).getEndTime())));
}

        return Employee
                .builder()
                .name(dto.getName())
                .skills(new HashSet<>(dto.getSkills()))
                .domain(StoreName.valueOf(dto.getDomain()))
                .availabilities(availabilities)
                .build();
    }


    /*private static Shift getShift(ShiftDTO dto) {

        logger.log(Level.INFO,"shift dto"+dto);
        logger.log(Level.INFO,"..................."+Shift
                .builder()
                .id(Shift.generateId())
                .start(DateTimeUtil.toLocalTime(dto.getRequiredShifts().get(0).getStartTime()))
                .end(DateTimeUtil.toLocalTime(dto.getRequiredShifts().get(0).getEndTime()))
                .storeName(StoreName.valueOf(dto.getStoreType()))
                .requiredSkill(Skill.valueOf(dto.getRequiredSkills().get(0).getSkillName()))
                .build());

        return Shift
                .builder()
                .id(Shift.generateId())
                .start(DateTimeUtil.toLocalTime(dto.getRequiredShifts().get(0).getStartTime()))
                .end(DateTimeUtil.toLocalTime(dto.getRequiredShifts().get(0).getEndTime()))
                .storeName(StoreName.valueOf(dto.getStoreType()))
                .requiredSkill(Skill.valueOf(dto.getRequiredSkills().get(0).getSkillName()))
                .build();
    }*/

    private static LocalTime getStartTime(EmployeesScheduleDTO dto) {
        return DateTimeUtil.toLocalTime(dto.getSchedules().get(0).getSchedule().get(0).getStartTime());
    }

    private static LocalTime getEndTime(EmployeesScheduleDTO dto) {
        return DateTimeUtil.toLocalTime(dto.getSchedules().get(0).getSchedule().get(0).getEndTime());
    }

    private static Set<AvailabilityOnDay> getAvailabilityOnDay(EmployeesScheduleDTO dto) {
//        logger.log(Level.INFO,"employees schedule dto-------"+dto);

//        return dto.getSchedules().get(0).getSchedule().get(0).getDays().stream().map(AvailabilityOnDay::valueOf).collect(Collectors.toSet());
        return dto.getSchedules().stream()
                .flatMap(scheduleDTO -> scheduleDTO.getSchedule().stream())
                .flatMap(shiftDTO -> shiftDTO.getDays().stream())
                .map(AvailabilityOnDay::valueOf)
                .collect(Collectors.toSet());
    }
}
