
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
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeScheduler {

    private static final Logger logger = Logger.getLogger(EmployeeScheduler.class.getName());



    /*public static List<Shift> scheduleShifts(List<EmployeesScheduleDTO> employees, List<Shift> shifts) {
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
                    logger.log(Level.INFO,"shift.getStart().toString()"+shift.getStart().toLocalTime().toString());
                    logger.log(Level.INFO,"startTime"+startTime);
                    logger.log(Level.INFO,"employee.getDomain()"+employee.getDomain());
                    logger.log(Level.INFO,"shift.getStoreType()"+shift.getStoreType());
                    logger.log(Level.INFO,"employee.getSkills()"+employee.getSkills());
                    logger.log(Level.INFO,"shift.getRequiredSkill()"+shift.getRequiredSkill()+"\n");
                    if (shift.getStart().toLocalTime().equals(startTime)
                            ) {

                        Employee newEmp = convert(employee);
                        shift.setEmployee(newEmp);
                        break;
                    }
                }
            }
            rotationIndex = (rotationIndex + 1) % numSchedules;
        }
        return shifts;
    }*/

    public static Employee convert(EmployeesScheduleDTO dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setSkills(dto.getSkills());
        employee.setDomain(dto.getDomain());


        return employee;
    }

    /*public static List<Shift> assignShifts(List<EmployeesScheduleDTO> employees, List<Shift> shifts) {
        List<Shift> list = new ArrayList<>();
        for (EmployeesScheduleDTO employee : employees) {
            logger.log(Level.INFO, employee.toString()+"....employee");
            int rotationIndex = 0; // Initialize rotationIndex here
            for (Shift shift : shifts) {
                if (shift.getEmployee() == null) {
                    ScheduleDTO schedule = employee.getSchedules().get(rotationIndex);
                    String[] days = schedule.getDays().toArray(new String[0]);
                    if (Arrays.asList(days).contains(shift.getDay().substring(0, 2))) {
                        String startTime = schedule.getStartTime();
                        String endTime = schedule.getEndTime();
                        if (shift.getStart().toLocalTime().equals(startTime) && shift.getEnd().equals(endTime)
                                && employee.getDomain().equals(shift.getStoreType())
                                && employee.getSkills().contains(shift.getRequiredSkill())) {
                            logger.log(Level.INFO,"-hcvahgdghvc----------------------------");
                            shift.setEmployee(convert(employee));
                        }
                        logger.log(Level.INFO,"shift.getStart().toString()"+shift.getStart().toLocalTime().toString());
                        logger.log(Level.INFO,"startTime"+startTime);
                        logger.log(Level.INFO,"employee.getDomain()"+employee.getDomain());
                        logger.log(Level.INFO,"shift.getStoreType()"+shift.getStoreType());
                        logger.log(Level.INFO,"employee.getSkills()"+employee.getSkills());
                        logger.log(Level.INFO,"shift.getRequiredSkill()"+shift.getRequiredSkill());
                        logger.log(Level.INFO,"-hcvahgdghvc----------------------------");

                        shift.setEmployee(convert(employee));

                    }
                }
                rotationIndex = (rotationIndex + 1) % employee.getSchedules().size();
                list.add(shift);
            }
            // Move rotationIndex update here
        }

        return shifts;
    }*/

    /*public static List<Shift> assignShifts(List<EmployeesScheduleDTO> employees, List<Shift> shifts) {
        List<Shift> list = new ArrayList<>();
        for (EmployeesScheduleDTO employee : employees) {
            logger.log(Level.INFO, employee.toString() + "....employee");
            Set<LocalDate> assignedDates = new HashSet<>(); // To keep track of dates on which the employee is already assigned a shift

            int rotationIndex = 0;
            for (Shift shift : shifts) {
                if (shift.getEmployee() == null) {
                    LocalDate shiftDate = shift.getStart().toLocalDate();
                    if (!assignedDates.contains(shiftDate)) {
                        ScheduleDTO schedule = employee.getSchedules().get(rotationIndex);
                        String[] days = schedule.getDays().toArray(new String[0]);
                        String shiftDay = shift.getDay().substring(0, 2);

                        if (Arrays.asList(days).contains(shiftDay)) {
                            String startTime = schedule.getStartTime();
                            String endTime = schedule.getEndTime();
                            if (shift.getStart().toLocalTime().toString().equals(startTime) &&
                                    shift.getEnd().toLocalTime().toString().equals(endTime) &&
                                    employee.getDomain().equals(shift.getStoreType()) &&
                                    employee.getSkills().contains(shift.getRequiredSkill())) {
                                logger.log(Level.INFO, "-hcvahgdghvc----------------------------");
                                shift.setEmployee(convert(employee));
                                assignedDates.add(shiftDate); // Mark this date as assigned for the employee
                                logger.log(Level.INFO, "-hcvahgdghvc----------------------------");
                            }

                            logger.log(Level.INFO, "shift.getStart().toString()" + shift.getStart().toLocalTime().toString());
                            logger.log(Level.INFO, "startTime" + startTime);
                            logger.log(Level.INFO, "employee.getDomain()" + employee.getDomain());
                            logger.log(Level.INFO, "shift.getStoreType()" + shift.getStoreType());
                            logger.log(Level.INFO, "employee.getSkills()" + employee.getSkills());
                            logger.log(Level.INFO, "shift.getRequiredSkill()" + shift.getRequiredSkill());
                        }
                    }
                }
                rotationIndex = (rotationIndex + 1) % employee.getSchedules().size();
                list.add(shift);
            }
        }

        return shifts;
    }*/
    public static List<Shift> assignShifts(List<EmployeesScheduleDTO> employees, List<Shift> shifts) {
        logger.log(Level.INFO,"Employees "+employees);
        logger.log(Level.INFO,"\n\nShifts "+shifts);

        List<Shift> list = new ArrayList<>();
        Map<String, LocalDate> employeeScheduleStartDate = new HashMap<>(); // Track the start date of the current schedule for each employee


        for (EmployeesScheduleDTO employee : employees) {
            List <ScheduleDTO> sch = employee.getSchedules();
            Map<String, Integer> indexMap = new HashMap<>();
            for(int i=0;i<sch.size();i++){
                indexMap.put(sch.get(i).getStartTime(),i);
            }
//            logger.log(Level.INFO, employee.toString() + "....employee");
            Set<LocalDate> assignedDates = new HashSet<>(); // To keep track of dates on which the employee is already assigned a shift

            // Initialize the current schedule index for the employee

            int currentScheduleIndex = 0;
            LocalDate scheduleStartDate = null;

            for (Shift shift : shifts) {
                String shiftStartTime = shift.getStart().toLocalTime().toString();
                int indexOfStart = indexMap.get(shiftStartTime);
//                logger.log(Level.INFO,"indexOfStart ------------>"+indexOfStart);
                if (shift.getEmployee() == null) {
                    LocalDate shiftDate = shift.getStart().toLocalDate();
                    if (!assignedDates.contains(shiftDate)) {
                        // Check if the employee needs to switch to the next schedule
                        if (scheduleStartDate == null) {
                            scheduleStartDate = shiftDate;
                            employeeScheduleStartDate.put(employee.getName(), scheduleStartDate);
                        } else {
                            long daysOnCurrentSchedule = ChronoUnit.DAYS.between(scheduleStartDate, shiftDate);
                            if (daysOnCurrentSchedule >= 7) {
                                indexOfStart = (indexOfStart + 1) % employee.getSchedules().size();
                                scheduleStartDate = shiftDate;
                                employeeScheduleStartDate.put(employee.getName(), scheduleStartDate);
                            }
                        }
                        logger.log(Level.INFO,"Employee" + employee + "\n" + indexOfStart + "\n" + shift);

                        ScheduleDTO schedule = employee.getSchedules().get(indexOfStart);
                        String[] days = schedule.getDays().toArray(new String[0]);
                        String shiftDay = shift.getDay().substring(0, 2);

                        if (Arrays.asList(days).contains(shiftDay)) {
                            String startTime = schedule.getStartTime();
                            String endTime = schedule.getEndTime();
                            if (shift.getStart().toLocalTime().toString().equals(startTime) &&
                                    employee.getDomain().equals(shift.getStoreType()) &&
                                    employee.getSkills().contains(shift.getRequiredSkill())) {
//                                logger.log(Level.INFO, "-hcvahgdghvc----------------------------");

                                shift.setEmployee(convert(employee));

                                assignedDates.add(shiftDate); // Mark this date as assigned for the employee
//                                logger.log(Level.INFO, "-hcvahgdghvc----------------------------");
                            }
                            else{
//                                logger.log(Level.INFO,"Unassigned Shift in else"+shift);
//                                logger.log(Level.INFO,"Unassigned Employee in else"+employee);
                            }

                            /*logger.log(Level.INFO, "shift.getStart().toString()" + shift.getStart().toLocalTime().toString());
                            logger.log(Level.INFO, "startTime" + startTime);
                            logger.log(Level.INFO, "employee.getDomain()" + employee.getDomain());
                            logger.log(Level.INFO, "shift.getStoreType()" + shift.getStoreType());
                            logger.log(Level.INFO, "employee.getSkills()" + employee.getSkills());
                            logger.log(Level.INFO, "shift.getRequiredSkill()" + shift.getRequiredSkill());*/
                        }
                    }
                }
                list.add(shift);
            }
        }

        return shifts;
    }



}

