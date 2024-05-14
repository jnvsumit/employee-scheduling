package org.acme.employeescheduling.dto;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
public class EmployeeScheduleDTO {

    private List<String> days;
    private LocalTime startTime;
    private LocalTime endTime;


   // private List<ScheduleDTO> schedule;

 /*   public static String generateId(){

        UUID uuid= UUID.randomUUID();

        return String.valueOf(uuid);
    }*/

}
