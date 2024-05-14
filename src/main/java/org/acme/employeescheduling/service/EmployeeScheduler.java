package org.acme.employeescheduling.service;

import org.acme.employeescheduling.domain.Employee;
import org.acme.employeescheduling.domain.Shift;
import org.acme.employeescheduling.domain.Skill;
import org.acme.employeescheduling.domain.StoreName;
import org.acme.employeescheduling.dto.EmployeesScheduleDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class EmployeeScheduler {

//    private List<Shift> generateShiftsForTimeSlot(LocalTime slotStartTime, LocalTime slotEndTime, int numOfEmployees, StoreName storeName, Skill requiredSkill){
//
//        List<Shift> shifts = new LinkedList<>();
//        for(int i=0;i<numOfEmployees;i++){
//            shifts.add(new Shift(slotStartTime,slotEndTime,storeName,requiredSkill));
//        }
//        return shifts;
//    }

    public static List<Shift> scheduleShifts(List<EmployeesScheduleDTO> employees, List<Shift> shifts) {
        int rotationIndex = 0;
        int numSchedules = employees.get(0).getSchedules().size(); // Get the number of schedules from any employee
        for (Shift shift : shifts) {
            for (EmployeesScheduleDTO employee : employees) {
                Map<String, String> schedule = (Map<String, String>) employee.getSchedules().get(rotationIndex);
                String[] days = schedule.get("days").split(",");
                if (Arrays.asList(days).contains(shift.getDay().substring(0, 2))) {
                    String startTime = schedule.get("start_time");
                    String endTime = schedule.get("end_time");
                    if (shift.getStart().equals(startTime) && shift.getEnd().equals(endTime)
                            && employee.getDomain().equals(shift.getStoreType())
                            && employee.getSkills().contains(shift.getRequiredSkill())) {
//                        shift.setEmployee();
                        break;
                    }
                }
            }
            rotationIndex = (rotationIndex + 1) % numSchedules;
        }
        return shifts;
    }




}
