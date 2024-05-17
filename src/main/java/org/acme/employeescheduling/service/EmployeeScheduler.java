
package org.acme.employeescheduling.service;

import org.acme.employeescheduling.domain.*;
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
import java.util.stream.Collectors;

public class EmployeeScheduler {

    private static final Logger logger = Logger.getLogger(EmployeeScheduler.class.getName());


    public static Employee convert(EmployeesScheduleDTO dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setSkills(dto.getSkills());
        employee.setDomain(dto.getDomain());
        return employee;
    }


    public static List<Shift> assignShifts(List<EmployeesScheduleDTO> employees, List<Shift> shifts) {
//        logger.log(Level.INFO, "Employees " + employees);
//        logger.log(Level.INFO, "\n\nShifts " + shifts);
        LinkedList<String> employeeIds = new LinkedList<>();
        String oneShiftEmployeeId="";
        int firstIndex = 0;
        int lastIndex = employees.size()-1;

        for(EmployeesScheduleDTO employee : employees){

            if(employee.getSchedules().size()==1){
                oneShiftEmployeeId = employee.getId();
                logger.log(Level.INFO,"------"+oneShiftEmployeeId);
            }
            else{
                employeeIds.add(employee.getId());
            }
        }

        for(EmployeesScheduleDTO employee : employees){

            if(employee.getSchedules().size()==1 && Objects.equals(employee.getSchedules().get(0).getStartTime(), "08:00")){
                employeeIds.addFirst(oneShiftEmployeeId);
                firstIndex = 1;
                lastIndex = employees.size()-1;
                logger.log(Level.INFO,"In morning"+ firstIndex+"firstIndex "+lastIndex+" secondIndex" + employee + "size" + employees.size());

            }
            if (employee.getSchedules().size()==1 && Objects.equals(employee.getSchedules().get(0).getStartTime(), "15:00")) {
                employeeIds.add(oneShiftEmployeeId);
                firstIndex = 0;
                lastIndex = employees.size()-2;

                logger.log(Level.INFO,"In afternoo "+ firstIndex+"firstIndex "+lastIndex+" secondIndex" + "employee" + employee);

            }
        }
        logger.log(Level.INFO,"Employee Ids" + employeeIds);


        Map<Integer, List<Shift>> shiftsByWeek = shifts.stream()
                .collect(Collectors.groupingBy(shift -> shift.getStart().get(WeekFields.of(Locale.getDefault()).weekOfYear())));
        List<List<Shift>> shiftsByWeekList = new ArrayList<>(shiftsByWeek.values());

        for(List<Shift> weekShifts: shiftsByWeekList){

                for(int i =0;i<weekShifts.size();i++){
                    String index = (employeeIds.get(i%employeeIds.size()));
                    Employee empToAssign = null;
                    for (EmployeesScheduleDTO employee : employees) {
                        logger.log(Level.INFO,"Employee with id -->",employee.getId());

                        if(employee.getId().equals(index)){
                            empToAssign = convert(employee);
                            logger.log(Level.INFO,"Employee -->",empToAssign);
                        }
                    }

                    weekShifts.get(i).setEmployee(empToAssign);
                    logger.log(Level.INFO,"Shift---->" + weekShifts.get(i) + "\nIndex"+ index);
                }

                employeeIds = rightShift(employeeIds, firstIndex, lastIndex);

//                employeeIds = rightShift(employeeIds, secondIndex);
                logger.log(Level.INFO,"Shifted Ids" + employeeIds);

        }
//        logger.log(Level.INFO,"shiftsByWeekList"+shiftsByWeekList);
        Map<String, LocalDate> employeeScheduleStartDate = new HashMap<>();


//        for (EmployeesScheduleDTO employee : employees) {
//
//
//            List<ScheduleDTO> sch = employee.getSchedules();
//            Map<String, Integer> indexMap = new HashMap<>();
//            for (int i = 0; i < sch.size(); i++) {
//                indexMap.put(sch.get(i).getStartTime(), i);
//            }
//            logger.log(Level.INFO,"Vaca"+(employee.getVacation()));
//            List<String> vacations = new ArrayList<>();
//
//            if(!(employee.getVacation()==null)){
//                LocalDate vacationStartDate = LocalDate.parse(employee.getVacation().get(0));
//                LocalDate vacationEndDate = LocalDate.parse(employee.getVacation().get(1));
//
//
//                while (!vacationStartDate.isAfter(vacationEndDate)) {
//                    vacations.add(String.valueOf(vacationStartDate));
//                    vacationStartDate = vacationStartDate.plusDays(1); // Move to the next day
//                }
//
//                logger.log(Level.INFO,"Vacations"+vacations);
//
//            }
//
////            logger.log(Level.INFO, employee.toString() + "....employee\n\n");
//            Set<LocalDate> assignedDates = new HashSet<>(); // To keep track of dates on which the employee is already assigned a shift
//
//            // Initialize the current schedule index for the employee
//
//            int currentScheduleIndex = 0;
//            LocalDate scheduleStartDate = null;
//            for (Shift shift : shifts) {
////                logger.log(Level.INFO,"VActions------>"+vacations);
////                logger.log(Level.INFO,shift.getStart().toLocalDate().toString());
////                boolean isInList = vacations.stream()
////                        .map(LocalDate::parse)
////                        .anyMatch(shift.getStart().toLocalDate()::isEqual);
////                if(isInList){
////                    logger.log(Level.INFO,"Is in list true"+employee+"\n"+shift+"\n");
////                    continue;
////                }
//                String shiftStartTime = shift.getStart().toLocalTime().toString();
//                int indexOfStart = indexMap.get(shiftStartTime);
//
//                if (shift.getEmployee() == null) {
//                    LocalDate shiftDate = shift.getStart().toLocalDate();
//                    if (!assignedDates.contains(shiftDate)) {
//
//                        if (scheduleStartDate == null) {
//                            scheduleStartDate = shiftDate;
//                            employeeScheduleStartDate.put(employee.getName(), scheduleStartDate);
//                        } else {
//                            long daysOnCurrentSchedule = ChronoUnit.DAYS.between(scheduleStartDate, shiftDate);
//                            if (daysOnCurrentSchedule >= 7) {
//                                indexOfStart = (indexOfStart + 1) % employee.getSchedules().size();
//                                scheduleStartDate = shiftDate;
//                                employeeScheduleStartDate.put(employee.getName(), scheduleStartDate);
//                            }
//                        }
////                        logger.log(Level.INFO, "Employee" + employee + "\n" + indexOfStart + "\n" + shift);
//
//                        ScheduleDTO schedule = employee.getSchedules().get(indexOfStart);
//                        String[] days = schedule.getDays().toArray(new String[0]);
//                        String shiftDay = shift.getDay().substring(0, 2);
//
//                        if (Arrays.asList(days).contains(shiftDay)) {
//                            String startTime = schedule.getStartTime();
//                            String endTime = schedule.getEndTime();
//                            if (shift.getStart().toLocalTime().toString().equals(startTime) &&
//                                    employee.getDomain().equals(shift.getStoreType()) &&
//                                    employee.getSkills().contains(shift.getRequiredSkill())) {
////                                logger.log(Level.INFO, "-hcvahgdghvc----------------------------");
//
//                                shift.setEmployee(convert(employee));
//
//                                assignedDates.add(shiftDate); // Mark this date as assigned for the employee
////                                logger.log(Level.INFO, "-hcvahgdghvc----------------------------");
//                            }
//
//                            /*logger.log(Level.INFO, "shift.getStart().toString()" + shift.getStart().toLocalTime().toString());
//                            logger.log(Level.INFO, "startTime" + startTime);
//                            logger.log(Level.INFO, "employee.getDomain()" + employee.getDomain());
//                            logger.log(Level.INFO, "shift.getStoreType()" + shift.getStoreType());
//                            logger.log(Level.INFO, "employee.getSkills()" + employee.getSkills());
//                            logger.log(Level.INFO, "shift.getRequiredSkill()" + shift.getRequiredSkill());*/
//                        }
//                    }
//                }
//            }
//        }

        return shifts;
    }

    /*public static <T> LinkedList<T> rightShift(LinkedList<T> list, int firstIndex, int lastIndex) {
        if (list.size() <= 1 || firstIndex >= lastIndex || lastIndex >= list.size() - 1) {
            return list; // No need to shift if the list has 0 or 1 element,
            // or if the firstIndex is greater than or equal to the secondIndex,
            // or if the secondIndex is out of bounds
        }

        T lastElement = list.remove(list.size() - 1); // Remove the last element

        for (int i = lastIndex; i > firstIndex + 1; i--) {
            T element = list.remove(i);
            list.add(i, element);
        }


        list.add(firstIndex + 1, lastElement); // Add the last element after the element at firstIndex

        return list;
    }*/
    public static <T> LinkedList<T> rightShift(LinkedList<T> list, int index1, int index2) {
        // Check if the indices are valid
        if (index1 < 0 || index2 >= list.size() || index1 >= index2) {
            throw new IllegalArgumentException("Invalid indices");
        }

        // Calculate the number of elements to rotate
        int rotateAmount = index2 - index1 + 1;

        // Extract the sublist to be rotated
        LinkedList<T> sublist = new LinkedList<>(list.subList(index1, index2 + 1));

        // Perform the rotation
        Collections.rotate(sublist, 1);

        // Update the original list with the rotated sublist
        ListIterator<T> iterator = list.listIterator(index1);
        for (T element : sublist) {
            iterator.next();
            iterator.set(element);
        }

        return list;
    }



}
