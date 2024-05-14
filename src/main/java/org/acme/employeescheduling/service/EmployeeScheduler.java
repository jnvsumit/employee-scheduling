
package org.acme.employeescheduling.service;

import org.acme.employeescheduling.domain.Employee;
import org.acme.employeescheduling.domain.Shift;
import org.acme.employeescheduling.domain.Skill;
import org.acme.employeescheduling.domain.StoreName;
import org.acme.employeescheduling.dto.EmployeesScheduleDTO;
import org.acme.employeescheduling.dto.ScheduleDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeScheduler {

    private static final Logger logger = Logger.getLogger(EmployeeScheduler.class.getName());

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
                logger.log(Level.INFO,"employee34567"+employee);
                ScheduleDTO schedule =  employee.getSchedules().get(rotationIndex);
                String[] days = schedule.getDays().toArray(new String[0]);
                if (Arrays.asList(days).contains(shift.getDay().substring(0, 2))) {
                    String startTime = schedule.getStartTime();
                    String endTime = schedule.getEndTime();
                    if (shift.getStart().equals(startTime) && shift.getEnd().equals(endTime)
                            && employee.getDomain().equals(shift.getStoreType())
                            && employee.getSkills().contains(shift.getRequiredSkill())) {
                        Employee newEmp = convert(employee);
                        shift.setEmployee(newEmp);
                        break;
                    }
                }
            }
            rotationIndex = (rotationIndex + 1) % numSchedules;
        }
        return shifts;
    }

    public static Employee convert(EmployeesScheduleDTO dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setSkills(dto.getSkills());
        employee.setDomain(dto.getDomain());



        return employee;
    }


}

