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

    public static List<Shift> getShifts(LocalDate startDate, LocalDate endDate, String data){
        Gson gson = new Gson();
        Type storeListType = new TypeToken<List<DepartmentDTO>>() {}.getType();
        List<DepartmentDTO> storeDTOs = gson.fromJson(data, storeListType);

        List<Department> stores = mapToStores(storeDTOs);

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        List<Shift> allShifts = new LinkedList<>();
        for(int i=0;i<=daysBetween;i++){

            LocalDate date = startDate.plusDays(i);
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            String dayOfWeekStr = dayOfWeek.toString();
            allShifts.addAll(generateShiftsForDay(stores,date,dayOfWeekStr));
        }
        AtomicInteger countShift = new AtomicInteger();
        allShifts.forEach(s -> s.setId(Integer.toString(countShift.getAndIncrement())));

        return allShifts;
    }

     public static List<Shift> getShifts(LocalDate startDate, LocalDate endDate ){
         String jsonData = null;
         try {
             jsonData = DataUtil.getDataFromFile("data/store2.json");
         } catch (Exception e) {

         }
         Gson gson = new Gson();
         Type storeListType = new TypeToken<List<DepartmentDTO>>() {
         }.getType();
         List<DepartmentDTO> storeDTOs = gson.fromJson(jsonData, storeListType);
//         logger.log(Level.INFO,"Store dto in main .java"+storeDTOs);
         // Map DTOs to Store class
         List<Department> stores = mapToStores(storeDTOs);

         long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

         List<Shift> allShifts = new LinkedList<>();
         for(int i=0;i<=daysBetween;i++){

             LocalDate date = startDate.plusDays(i);
             DayOfWeek dayOfWeek = date.getDayOfWeek();
             String dayOfWeekStr = dayOfWeek.toString();
//             if(Objects.equals(dayOfWeekStr, "SUNDAY")){
//                 continue;
//             }
             allShifts.addAll(generateShiftsForDay(stores,date,dayOfWeekStr));
         }
         AtomicInteger countShift = new AtomicInteger();
         allShifts.forEach(s -> s.setId(Integer.toString(countShift.getAndIncrement())));

         return allShifts;
     }


    public static List<Availability> generateAvailabilities(List<EmployeesScheduleDTO> employees, LocalDate startDate, LocalDate endDate) {
        List<Availability> availabilities = new ArrayList<>();

        LocalDate dateIterator = startDate;
        int weekCount = 0;

        while (!dateIterator.isAfter(endDate)) {
            for (EmployeesScheduleDTO employee : employees) {
                List<ScheduleDTO> schedules = employee.getSchedules();
//                logger.log(Level.INFO,"schedulessgjsdgyug45678" +schedules.toString());
//                EmployeeScheduleDTO schedule = schedules.get(weekCount % schedules.size());// Rotate through schedules
//                List<String> scheduleDays = schedule.getDays();

                /*if (scheduleDays.contains(dateIterator.getDayOfWeek().toString())) {

                   // availabilities.add(createAvailability(employee, dateIterator, schedule.getStartTime(), schedule.getEndTime()));
                } else {


                   // availabilities.add(createUnavailableAvailability(employee, dateIterator,schedule.getStartTime(), schedule.getEndTime()));
                }*/
            }


            if (!(dateIterator.getDayOfWeek() == DayOfWeek.SATURDAY)){
                dateIterator = dateIterator.plusDays(1);
            }
            else{
                dateIterator = dateIterator.plusDays(2);
            }

            if (dateIterator.getDayOfWeek() == DayOfWeek.SATURDAY) {
                weekCount++;
            }
        }
        AtomicInteger countShift = new AtomicInteger();
        availabilities.forEach(s -> s.setId(Integer.toString(countShift.getAndIncrement())));
        return availabilities;
    }

//    private static Availability createAvailability(EmployeesScheduleDTO employeesScheduleDTO, LocalDate date, String startTime, String endTime) {
//        Availability availability = new Availability();
//        availability.setEmployee(new Employee(employeesScheduleDTO.getName() ,employeesScheduleDTO.getSkills(),employeesScheduleDTO.getDomain(),null));
//        availability.setDate(date);
//        availability.setStartTime(LocalTime.parse(startTime));
//        availability.setEndTime(LocalTime.parse(endTime));
//        availability.setAvailabilityType(AvailabilityType.DESIRED);
////        availability.setId(Availability.generateId());
//        return availability;
//    }

//    private static Availability createUnavailableAvailability(EmployeesScheduleDTO employeesScheduleDTO, LocalDate date, String startTime, String endTime) {
//        Availability availability = new Availability();
//        availability.setEmployee(new Employee(employeesScheduleDTO.getName() ,employeesScheduleDTO.getSkills(),employeesScheduleDTO.getDomain(),null));
//        availability.setDate(date);
//        availability.setStartTime(LocalTime.parse(startTime));
//        availability.setEndTime(LocalTime.parse(endTime));
//        availability.setAvailabilityType(AvailabilityType.UNAVAILABLE);
////        availability.setId(Availability.generateId());
//        return availability;
//    }


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
//        logger.log(Level.INFO,departments.toString());
        return departments;
    }


    public static List<Shift> generateShiftsForDay(List<Department> departments, LocalDate date ,String day) {

        List<Shift> allShifts = new LinkedList<>();

//        System.out.println("\n"+"Day of week ---"+day+"\n");

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

