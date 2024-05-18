package org.acme.employeescheduling.dto;

import lombok.Data;
import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import java.io.InputStream;

@Data
public class SolveScheduleBodyDTO {
    @FormParam("employeeFile")
    @PartType("application/octet-stream")
    private InputStream employeeFile;

    @FormParam("employeeFileName")
    @PartType("text/plain")
    private String employeeFileName;

    @FormParam("storeFile")
    @PartType("application/octet-stream")
    private InputStream storeFile;

    @FormParam("storeFileName")
    @PartType("text/plain")
    private String storeFileName;
}
