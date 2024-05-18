package org.acme.employeescheduling.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.employeescheduling.domain.Availability;
import org.acme.employeescheduling.domain.EmployeeSchedule;
import org.acme.employeescheduling.domain.Shift;
import org.acme.employeescheduling.dto.EmployeesScheduleDTO;
import org.acme.employeescheduling.dto.ShiftDTO;
import org.acme.employeescheduling.mapper.EmployeesScheduleMapper;
import org.acme.employeescheduling.utils.DataUtil;
import org.acme.employeescheduling.utils.JsonUtil;
import org.acme.employeescheduling.service.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static io.quarkus.arc.impl.UncaughtExceptions.LOGGER;

@ApplicationScoped
public class DataService {

    private static final Logger logger = Logger.getLogger(DataService.class.getName());

    public EmployeeSchedule getEmployeeSchedule(LocalDate startDate, LocalDate endDate, String employeeContent, String storeContent) {

        List <Shift> shifts = Main.getShifts(startDate,endDate ,storeContent);


        try {
            List<EmployeesScheduleDTO> employeesScheduleDTOS = getEmployeeSchedules(employeeContent);

            List < Availability> availabilities = Main.generateAvailabilities(employeesScheduleDTOS,startDate,endDate);
            Set<String> domains = new HashSet<>();

            for(Shift shift: shifts){
                domains.add(shift.getStoreType());
            }

            List<Shift> finalShifts = new LinkedList<>();
            for(String domain: domains){
                List<Shift> domainWiseshift = filterShiftsByDomain(shifts,domain);

                AtomicInteger countShift = new AtomicInteger();
                domainWiseshift.forEach(s -> s.setId(Integer.toString(countShift.getAndIncrement())));

                List<EmployeesScheduleDTO> domainBasedEmployees = filterEmployeeByDomain(employeesScheduleDTOS,domain);
                AtomicInteger countShift2 = new AtomicInteger();
                domainBasedEmployees.forEach(s -> s.setId(Integer.toString(countShift2.getAndIncrement())));

                finalShifts.addAll(EmployeeScheduler.assignShifts(domainBasedEmployees,
                        domainWiseshift));
            }

            List<Shift> filteredShifts = new ArrayList<>();
            for (Shift shift : shifts) {
                if (!shift.getDay().equals("SUNDAY")) {
                    filteredShifts.add(shift);
                }
            }
            AtomicInteger countShift = new AtomicInteger();
            filteredShifts.forEach(s -> s.setId(Integer.toString(countShift.getAndIncrement())));


            return EmployeesScheduleMapper.toEmployeeSchedule(employeesScheduleDTOS, filteredShifts,availabilities);
        } catch (Exception e) {
            LOGGER.error("Something went wrong", e);
            return null;
        }
    }

    public EmployeeSchedule getEmployeeSchedule(LocalDate startDate, LocalDate endDate) {

        List <Shift> shifts = Main.getShifts(startDate,endDate);


        try {
            List<EmployeesScheduleDTO> employeesScheduleDTOS = getEmployeeSchedules();

            List < Availability> availabilities = Main.generateAvailabilities(employeesScheduleDTOS,startDate,endDate);
            Set<String> domains = new HashSet<>();

            for(Shift shift: shifts){
                domains.add(shift.getStoreType());
            }

            List<Shift> finalShifts = new LinkedList<>();
            for(String domain: domains){
                List<Shift> domainWiseshift = filterShiftsByDomain(shifts,domain);

                AtomicInteger countShift = new AtomicInteger();
                domainWiseshift.forEach(s -> s.setId(Integer.toString(countShift.getAndIncrement())));

                List<EmployeesScheduleDTO> domainBasedEmployees = filterEmployeeByDomain(employeesScheduleDTOS,domain);
                AtomicInteger countShift2 = new AtomicInteger();
                domainBasedEmployees.forEach(s -> s.setId(Integer.toString(countShift2.getAndIncrement())));

                finalShifts.addAll(EmployeeScheduler.assignShifts(domainBasedEmployees,
                        domainWiseshift));
            }

            List<Shift> filteredShifts = new ArrayList<>();
            for (Shift shift : shifts) {
                if (!shift.getDay().equals("SUNDAY")) {
                    filteredShifts.add(shift);
                }
            }
            AtomicInteger countShift = new AtomicInteger();
            filteredShifts.forEach(s -> s.setId(Integer.toString(countShift.getAndIncrement())));


            return EmployeesScheduleMapper.toEmployeeSchedule(employeesScheduleDTOS, filteredShifts,availabilities);
        } catch (Exception e) {
            LOGGER.error("Something went wrong", e);
            return null;
        }
    }

    public String getFileContent(InputStream fileInputStream) {
        StringBuilder fileContent = new StringBuilder();

        try (InputStream inputStream = fileInputStream;
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

            return fileContent.toString();
        } catch (IOException e) {
            LOGGER.error("Failed fetching data.", e);
            throw new RuntimeException("Failed fetching data.", e);
        }
    }

    private List<EmployeesScheduleDTO> getEmployeeSchedules(String data) {
        try {
            EmployeesScheduleDTO[] scheduleDTOS = JsonUtil.deserialize(data, EmployeesScheduleDTO[].class);
            return Arrays.stream(scheduleDTOS).toList();
        } catch (Exception e) {
            LOGGER.error("Failed parsing and fetching data", e);
            throw e;
        }
    }

    private List<EmployeesScheduleDTO> getEmployeeSchedules() throws Exception {
        try {
            String data = DataUtil.getDataFromFile("data/employee2.json");

            EmployeesScheduleDTO[] scheduleDTOS = JsonUtil.deserialize(data, EmployeesScheduleDTO[].class);
//            logger.log(Level.INFO, Arrays.stream(scheduleDTOS).toList().toString()+"....scheduleDTO");

            return Arrays.stream(scheduleDTOS).toList();

        } catch (Exception e) {
            LOGGER.error("Failed parsing and fetching data", e);
            throw e;
        }
    }

    private List<ShiftDTO> getShifts() throws Exception {
        try {
            String data = DataUtil.getDataFromFile("data/stores.json");
            ShiftDTO[] shiftDTOS = JsonUtil.deserialize(data, ShiftDTO[].class);
            return Arrays.stream(shiftDTOS).toList();
        } catch (Exception e) {
            LOGGER.error("Failed parsing and fetching data", e);
            throw e;
        }
    }


    private List<EmployeesScheduleDTO> filterEmployeeByDomain(List<EmployeesScheduleDTO> employees, String domain){
        List<EmployeesScheduleDTO> filteredEmployees = new ArrayList<>();
        for(EmployeesScheduleDTO employee: employees){
            if(employee.getDomain().equals(domain)){
                filteredEmployees.add(employee);
            }
        }
//        logger.log(Level.INFO,"Employees of domain "+domain + " "+ filteredEmployees);
        return filteredEmployees;
    }

    private List<Shift> filterShiftsByDomain(List<Shift> shifts, String domain){
        List<Shift> filteredShifts = new ArrayList<>();
        for(Shift shift: shifts){
            if(shift.getStoreType().equals(domain)){
                filteredShifts.add(shift);
            }
        }
//        logger.log(Level.INFO,"Shifts of domain "+domain + " "+ filteredShifts);
        return filteredShifts;
    }

}
