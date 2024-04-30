package org.acme.employeescheduling.service;

import com.aayushatharva.brotli4j.common.annotations.Local;
import org.acme.employeescheduling.domain.*;
import org.acme.employeescheduling.service.DataService;
import org.acme.employeescheduling.dto.DepartmentDTO;
import org.acme.employeescheduling.dto.EmployeeDTO;
import org.acme.employeescheduling.dto.RequiredSkillDTO;
import org.acme.employeescheduling.dto.RequiredShiftDTO;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.acme.employeescheduling.mapper.EmployeesScheduleMapper;
import org.acme.employeescheduling.utils.DataUtil;

import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

     public static List<Shift> getShifts(){
         String jsonData = null;
         try {
             jsonData = DataUtil.getDataFromFile("data/stores.json");
         } catch (Exception e) {

         }
         Gson gson = new Gson();
         Type storeListType = new TypeToken<List<DepartmentDTO>>() {
         }.getType();
         List<DepartmentDTO> storeDTOs = gson.fromJson(jsonData, storeListType);
         // Map DTOs to Store class
         List<Department> stores = mapToStores(storeDTOs);
         LocalDate startDate = LocalDate.now()                 // Get the current date
                 .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

         int initialRosterDays = 21;
         List<Shift> allShifts = new LinkedList<>();
         for(int i=0;i<initialRosterDays;i++){

             LocalDate date = startDate.plusDays(i);
             DayOfWeek dayOfWeek = date.getDayOfWeek();
             String dayOfWeekStr = dayOfWeek.toString();
             if(Objects.equals(dayOfWeekStr, "SUNDAY")){
                 break;
             }
             allShifts.addAll(generateShiftsForDay(stores,date,dayOfWeekStr));
         }
         AtomicInteger countShift = new AtomicInteger();
         allShifts.forEach(s -> s.setId(Integer.toString(countShift.getAndIncrement())));
//         List <Employee> employees = EmployeesScheduleMapper.getEmployees();
//         for (Shift shift : allShifts) {
//             // Find suitable employee for the shift
//             Employee suitableEmployee = findSuitableEmployee(shift, employees);
//
//             // Assign employee to the shift if found
//             if (suitableEmployee != null) {
//                 shift.setEmployee(suitableEmployee);
//                 System.out.println("Assigned " + suitableEmployee.getName() + " to shift on " + shift.getDay());
//             } else {
//                 System.out.println("No suitable employee found for shift on " + shift.getDay());
//             }
//         }

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
//        return null; // No suitable employee found
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
        for(Shift shift: allShifts){
            System.out.println(shift);
        }
        return allShifts;
    }
}

