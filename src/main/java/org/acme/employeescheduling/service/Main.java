package org.acme.employeescheduling.service;

import java.util.stream.Collectors;
import com.aayushatharva.brotli4j.common.annotations.Local;
import org.acme.employeescheduling.domain.*;
import org.acme.employeescheduling.domain.RequiredShift;
import org.acme.employeescheduling.domain.RequiredSkill;
import org.acme.employeescheduling.dto.*;
import org.acme.employeescheduling.service.DataService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.acme.employeescheduling.mapper.EmployeesScheduleMapper;
import org.acme.employeescheduling.utils.DataUtil;

import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.acme.employeescheduling.utils.DataUtil;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {




    }

     public static List<Shift> getShifts(LocalDate startDate, LocalDate endDate ){
         String jsonData = null;
         try {
             jsonData = DataUtil.getDataFromFile("data/stores.json");
         } catch (Exception e) {

         }
         Gson gson = new Gson();
         Type storeListType = new TypeToken<List<DepartmentDTO>>() {
         }.getType();
         List<DepartmentDTO> storeDTOs = gson.fromJson(jsonData, storeListType);
         logger.log(Level.INFO,"Store dto in main .java"+storeDTOs);
         // Map DTOs to Store class
         List<Department> stores = mapToStores(storeDTOs);

         long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

         List<Shift> allShifts = new LinkedList<>();
         for(int i=0;i<=daysBetween;i++){

             LocalDate date = startDate.plusDays(i);
             DayOfWeek dayOfWeek = date.getDayOfWeek();
             String dayOfWeekStr = dayOfWeek.toString();
             if(Objects.equals(dayOfWeekStr, "SUNDAY")){
                 continue;
             }
             allShifts.addAll(generateShiftsForDay(stores,date,dayOfWeekStr));
         }
         AtomicInteger countShift = new AtomicInteger();
         allShifts.forEach(s -> s.setId(Integer.toString(countShift.getAndIncrement())));

         return allShifts;
     }

//    private static Employee findSuitableEmployee(Shift shift, List<Employee> employees) {
//        for (Employee employee : employees) {
//            // Check if employee has required skill and availability on the shift day
//            if (employee.getDomain().equals(shift.getStoreName()) &&
//                    employee.hasSkill(shift.getRequiredSkill()) &&
//                    employee.isAvailableOn(shift.getDay(), shift.getStart(), shift.getEnd())) {
//                return employee;
//            }
//        }
//        return null; // No suitable employee found'
//    }
/*private static List<Availability> getAvailabilities(LocalDate startDate, LocalDate endDate, List<Employee> employeeList) {
    List<Availability> availabilities = new LinkedList<>();

    // Iterate over each day between startDate and endDate
    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
        final LocalDate currentDate = date;

        // Iterate over each employee
        for (Employee employee : employeeList) {
            // Find schedules for the current employee matching the current day
            List<Schedule> schedulesForDay = employee.getSchedules().stream()
                    .filter(schedule -> schedule.getSchedule().stream()
                            .anyMatch(s -> s.getDays().contains(currentDate.getDayOfWeek().toString())))
                    .collect(Collectors.toList());

            // Iterate over the schedules found for the current employee and day
            for (Schedule schedule : schedulesForDay) {
                // Create Availability objects based on the start and end times in the schedule
                schedule.getSchedule().forEach(slot -> {
                    Availability availability = Availability.builder()
                            .id(Availability.generateId())
                            .employee(employee)
                            .startTime(LocalTime.parse(slot.getStart_time()))
                            .endTime(LocalTime.parse(slot.getEnd_time()))
                            .date(currentDate)
                            .build();
                    availabilities.add(availability);
                });
            }
        }
    }

    return availabilities;
}*/

//    public static List<Availability> getAvailabilities(LocalDate startDate, LocalDate endDate, List<EmployeesScheduleDTO> employeesScheduleDTOList) {
//    List<Availability> availabilities = new LinkedList<>();
//
//    // Iterate over each day between startDate and endDate
//    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
//        final LocalDate currentDate = date;
//
//        // Iterate over each employee schedule DTO
//        for (EmployeesScheduleDTO employeesScheduleDTO : employeesScheduleDTOList) {
//            // Check if employee schedule DTO is for the current domain
//            if (!employeesScheduleDTO.getDomain().equals("FRUIT")) {
//                continue;
//            }
//
//            // Find schedules for the current employee schedule DTO matching the current day
//            List<EmployeeScheduleDTO> schedulesForDay = employeesScheduleDTO.getSchedules().stream()
//                    .filter(schedule -> schedule.getSchedule().stream()
//                            .anyMatch(s -> s.getDays().contains(currentDate.getDayOfWeek().toString())))
//                    .collect(Collectors.toList());
//
//            // Iterate over the schedules found for the current employee and day
//            for (EmployeeScheduleDTO schedule : schedulesForDay) {
//                // Create Availability objects based on the start and end times in the schedule
//                schedule.getSchedule().forEach(slot -> {
//                    Availability availability = Availability.builder()
//                            .employee(new Employee(employeesScheduleDTO.getName(), StoreName.valueOf(employeesScheduleDTO.getDomain())))
//                            .startTime(LocalTime.parse(slot.getStartTime()))
//                            .endTime(LocalTime.parse(slot.getEndTime()))
//                            .date(currentDate)
//                            .build();
//                    logger.log(Level.INFO,"Availability"+availability);
//                    availabilities.add(availability);
//                });
//            }
//        }
//    }
//        AtomicInteger countShift = new AtomicInteger();
//        availabilities.forEach(a -> a.setId(Integer.toString(countShift.getAndIncrement())));
//    return availabilities;
//}

    public static List<Availability> getAvailabilities(LocalDate startDate, LocalDate endDate, List<EmployeesScheduleDTO> employeesScheduleDTOList) {
        List<Availability> availabilities = new LinkedList<>();

        // Map to keep track of the current schedule index for each employee
        int[] scheduleIndices = new int[employeesScheduleDTOList.size()];

        // Iterate over each date between startDate and endDate
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Iterate over each employee schedule DTO
            for (int i = 0; i < employeesScheduleDTOList.size(); i++) {
                EmployeesScheduleDTO employeesScheduleDTO = employeesScheduleDTOList.get(i);
                logger.log(Level.INFO,"=================>"+employeesScheduleDTO);
                // Check if the employee schedule DTO has any schedules
                if (employeesScheduleDTO.getSchedules().isEmpty()) {
                    continue;
                }
                // Get the current schedule index for the employee
                int scheduleIndex = scheduleIndices[i] % employeesScheduleDTO.getSchedules().size();
                // Get the schedule for the current day of the week
                ScheduleDTO scheduleForDay = getScheduleForDay(employeesScheduleDTO, date.getDayOfWeek(), scheduleIndex);
                if (scheduleForDay != null) {
                    // Create an availability for the employee on the current date
                    Availability availability = Availability.builder()
//                            .id(Availability.generateId())
                            .employee(new Employee(employeesScheduleDTO.getName(), employeesScheduleDTO.getDomain()))
                            .startTime(LocalTime.parse(scheduleForDay.getStartTime()))
                            .endTime(LocalTime.parse(scheduleForDay.getEndTime()))
                            .date(date)
                            .build();
                    availabilities.add(availability);
                }
                // Increment the current schedule index for the employee
                scheduleIndices[i]++;
            }
        }
        AtomicInteger countShift = new AtomicInteger();
        availabilities.forEach(a -> a.setId(Integer.toString(countShift.getAndIncrement())));
        return availabilities;
    }

    // Get the schedule for the specified day of the week from the employee schedule DTO
    private static ScheduleDTO getScheduleForDay(EmployeesScheduleDTO employeesScheduleDTO, DayOfWeek dayOfWeek, int scheduleIndex) {
        for (EmployeeScheduleDTO scheduleDTO : employeesScheduleDTO.getSchedules()) {
            for (ScheduleDTO schedule : scheduleDTO.getSchedule()) {
                if (schedule.getDays().contains(dayOfWeek.toString())) {
                    // Check if the schedule index is within bounds
                    if (scheduleIndex >= 0 && scheduleIndex < scheduleDTO.getSchedule().size()) {
                        return scheduleDTO.getSchedule().get(scheduleIndex);
                    } else {
                        // Handle the case where the index is out of bounds
                        return null; // Or throw an exception or handle it according to your requirement
                    }
                }
            }
        }
        return null;
    }


    // Check if the employee schedule DTO has a schedule for the specified day of the week
    private static boolean hasScheduleForDay(EmployeesScheduleDTO employeesScheduleDTO, DayOfWeek dayOfWeek) {
        for (EmployeeScheduleDTO scheduleDTO : employeesScheduleDTO.getSchedules()) {
            for (ScheduleDTO schedule : scheduleDTO.getSchedule()) {
                if (schedule.getDays().contains(dayOfWeek.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Get the schedule for the specified day of the week from the employee schedule DTO
    private static ScheduleDTO getScheduleForDay(EmployeesScheduleDTO employeesScheduleDTO, DayOfWeek dayOfWeek) {
        for (EmployeeScheduleDTO scheduleDTO : employeesScheduleDTO.getSchedules()) {
            for (ScheduleDTO schedule : scheduleDTO.getSchedule()) {
                if (schedule.getDays().contains(dayOfWeek.toString())) {
                    return schedule;
                }
            }
        }
        return null;
    }


    private static List<Department> mapToStores(List<DepartmentDTO> departmentDTOs) {
        List<Department> departments = new ArrayList<>();
        for (DepartmentDTO departmentDTO : departmentDTOs) {
            Department department = new Department();
            department.setName(departmentDTO.getName());
            department.setStoreType(departmentDTO.getStore_type());
            // Map required shifts
            List<RequiredShift> requiredShifts = new ArrayList<>();
            for (RequiredShiftDTO requiredShiftDTO : departmentDTO.getRequired_shifts()) {
                RequiredShift requiredShift = new RequiredShift();
                requiredShift.setStartTime(requiredShiftDTO.getStart_time());
                requiredShift.setEndTime(requiredShiftDTO.getEnd_time());
                requiredShift.setEmployeeCount(requiredShiftDTO.getEmployee_count());
                // Map required skills
                List<RequiredSkill> requiredSkills = new ArrayList<>();
                for (RequiredSkillDTO requiredSkillDTO : requiredShiftDTO.getRequired_skills()) {
                    RequiredSkill requiredSkill = new RequiredSkill();
                    requiredSkill.setSkillName(requiredSkillDTO.getSkill_name());
                    requiredSkill.setMinimumEmployeeCount(requiredSkillDTO.getMinimum_employee_count());
                    requiredSkills.add(requiredSkill);
                }
                requiredShift.setRequiredSkills(requiredSkills);
                requiredShifts.add(requiredShift);
            }
            department.setRequiredShifts(requiredShifts);
            departments.add(department);
        }
        logger.log(Level.INFO,departments.toString());
        return departments;
    }


    public static List<Shift> generateShiftsForDay(List<Department> departments, LocalDate date ,String day) {

        List<Shift> allShifts = new LinkedList<>();

        System.out.println("\n"+"Day of week ---"+day+"\n");

//        LocalDate currentDate = LocalDate.now();

        for(Department department: departments){
            List<RequiredShift> requiredShifts = department.getRequiredShifts();
            for (RequiredShift requiredShift : requiredShifts){
                LocalTime startTime = LocalTime.parse(requiredShift.getStartTime());
                LocalTime endTime = LocalTime.parse(requiredShift.getEndTime());

                LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
                LocalDateTime endDateTime = LocalDateTime.of(date, endTime);
                for(RequiredSkill requiredSkill:requiredShift.getRequiredSkills()){
                    int employeeCount = requiredSkill.getMinimumEmployeeCount();
                    for(int i=0;i<employeeCount;i++){
                        allShifts.add(new Shift(day, startDateTime,endDateTime,department.getStoreType(),requiredSkill.getSkillName()));

                    }

                }
            }
        }
        return allShifts;
    }
}

