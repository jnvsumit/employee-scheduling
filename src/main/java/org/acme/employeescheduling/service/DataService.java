package org.acme.employeescheduling.service;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.flogger.Flogger;
import org.acme.employeescheduling.domain.Availability;
import org.acme.employeescheduling.domain.EmployeeSchedule;
import org.acme.employeescheduling.domain.Shift;
import org.acme.employeescheduling.dto.EmployeesScheduleDTO;
import org.acme.employeescheduling.dto.ShiftDTO;
import org.acme.employeescheduling.mapper.EmployeesScheduleMapper;
import org.acme.employeescheduling.utils.DataUtil;
import org.acme.employeescheduling.utils.JsonUtil;
import org.acme.employeescheduling.service.Main;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.quarkus.arc.impl.UncaughtExceptions.LOGGER;

@ApplicationScoped
public class DataService {
    private static final Logger logger = Logger.getLogger(DataService.class.getName());

    public EmployeeSchedule getEmployeeSchedule(LocalDate startDate, LocalDate endDate) {

        List <Shift> shifts = Main.getShifts(startDate,endDate);
        try {

            List<EmployeesScheduleDTO> employeesScheduleDTOS = getEmployeeSchedules();
            List < Availability> availabilities = Main.generateAvailabilities(employeesScheduleDTOS,startDate,endDate);


            List<ShiftDTO> shiftDTOS = getShifts();
            List<Shift> assignedShifts = EmployeeScheduler.scheduleShifts(employeesScheduleDTOS,shifts);
            logger.log(Level.INFO,"assgined shifts" + assignedShifts);
            return EmployeesScheduleMapper.toEmployeeSchedule(employeesScheduleDTOS, assignedShifts,availabilities);
//            return null;
        } catch (Exception e) {
            LOGGER.error("Something went wrong", e);
            return null;
        }
    }

    private List<EmployeesScheduleDTO> getEmployeeSchedules() throws Exception {
        try {
            String data = DataUtil.getDataFromFile("data/employee2.json");

            EmployeesScheduleDTO[] scheduleDTOS = JsonUtil.deserialize(data, EmployeesScheduleDTO[].class);

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
}
