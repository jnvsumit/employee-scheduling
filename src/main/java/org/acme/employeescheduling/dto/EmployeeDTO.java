package org.acme.employeescheduling.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeDTO {
    @SerializedName("name")
    private String name;

    @SerializedName("position")
    private String position;

    @SerializedName("domain")
    private String domain;

    @SerializedName("skills")
    private List<String> skills;

    @SerializedName("schedules")
    private List<ScheduleDTO> schedules;
}
