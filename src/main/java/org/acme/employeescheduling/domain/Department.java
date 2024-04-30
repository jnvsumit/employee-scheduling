package org.acme.employeescheduling.domain;

import java.time.LocalTime;
import java.util.List;
import lombok.Data;
@Data
public class Department {


        private String name;
        private String storeType;
        private List<RequiredShift> requiredShifts;


}
