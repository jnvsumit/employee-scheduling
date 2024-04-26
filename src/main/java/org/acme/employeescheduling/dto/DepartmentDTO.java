package org.acme.employeescheduling.dto;

import java.util.List;
import lombok.Data;
@Data
public class DepartmentDTO {
    private String name;
    private String store_type;
    private List<RequiredShiftDTO> required_shifts;

    // Getters and setters
}

