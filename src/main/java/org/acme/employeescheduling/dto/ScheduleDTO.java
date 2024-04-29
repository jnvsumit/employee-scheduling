package org.acme.employeescheduling.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ScheduleDTO {

    private List<String> days;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;
}
