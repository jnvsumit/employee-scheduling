package org.acme.employeescheduling.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class ShiftDTO {

    @SerializedName("name")
    private String name;

    @SerializedName("store_type")
    private String storeType;

    @SerializedName("required_shifts")
    private List<RequiredShift> requiredShifts;
}
