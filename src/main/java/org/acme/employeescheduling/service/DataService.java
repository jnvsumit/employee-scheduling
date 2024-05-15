package org.acme.employeescheduling.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.employeescheduling.domain.EmployeeSchedule;
import org.acme.employeescheduling.domain.ScheduleState;
import org.acme.employeescheduling.domain.Shift;
import org.acme.employeescheduling.dto.EmployeeDTO;
import org.acme.employeescheduling.dto.EmployeesScheduleDTO;
import org.acme.employeescheduling.dto.ShiftDTO;
import org.acme.employeescheduling.mapper.EmployeesScheduleMapper;
import org.acme.employeescheduling.rest.EmployeeScheduleResource;
import org.acme.employeescheduling.utils.DataUtil;
import org.acme.employeescheduling.utils.DateTimeUtil;
import org.acme.employeescheduling.utils.JsonUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import static io.quarkus.arc.impl.UncaughtExceptions.LOGGER;

@ApplicationScoped
public class DataService {

    public EmployeeSchedule getEmployeeSchedule(String startDate, String endDate) {
        try {
            List<EmployeeDTO> employees = getEmployees();
            List<ShiftDTO> shifts = getShifts();
            EmployeeSchedule employeeSchedule = EmployeesScheduleMapper.toEmployeeSchedule(employees, shifts, DateTimeUtil.toLocalDateTime(startDate), DateTimeUtil.toLocalDateTime(endDate));

            int initialRosterLengthInDays = 7;
            LocalDate startD = DateTimeUtil.toLocalDateTime(startDate).toLocalDate();

            ScheduleState scheduleState = new ScheduleState();
            scheduleState.setFirstDraftDate(startD);
            scheduleState.setDraftLength(initialRosterLengthInDays);
            scheduleState.setPublishLength(7);
            scheduleState.setLastHistoricDate(startD.minusDays(7));
            scheduleState.setTenantId(EmployeeScheduleResource.SINGLETON_SCHEDULE_ID);

            employeeSchedule.setScheduleState(scheduleState);
            return employeeSchedule;
        } catch (Exception e) {
            LOGGER.error("Something went wrong", e);
            return null;
        }
    }

    private List<EmployeeDTO> getEmployees() throws Exception {
        try {
            String data = DataUtil.getDataFromFile("data/employee.json");
            EmployeeDTO[] scheduleDTOS = JsonUtil.deserialize(data, EmployeeDTO[].class);
            return Arrays.stream(scheduleDTOS).toList();
        } catch (Exception e) {
            LOGGER.error("Failed parsing employees data", e);
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
