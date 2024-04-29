package org.acme.employeescheduling.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ShiftDTO {

    @SerializedName("day")
    private String day;
    @SerializedName("name")
    private String name;

    @SerializedName("store_type")
    private String storeType;

    @SerializedName("required_skills")
    private List<RequiredSkill> requiredSkills;

    @SerializedName("required_shifts")
    private List<RequiredShift> requiredShifts;
}
