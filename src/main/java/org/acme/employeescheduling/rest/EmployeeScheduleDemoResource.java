package org.acme.employeescheduling.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.acme.employeescheduling.domain.EmployeeSchedule;
import org.acme.employeescheduling.service.DataService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Tag(name = "Demo data", description = "Data from resources folder")
@Path("get-data")
public class EmployeeScheduleDemoResource {

    private final DataService dataService;

    @Inject
    public EmployeeScheduleDemoResource(DataService dataService) {
        this.dataService = dataService;
    }

    @Operation(summary = "List demo data.")
    @GET
    public Object[] list() {
        return List.of("LARGE", "SMALL").toArray();
    }

    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Unsolved demo schedule.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = EmployeeSchedule.class)))})
    @Operation(summary = "Find an unsolved schedule")
    @GET
    @Path("{dataType}")
    public Response getData(@PathParam(value = "dataType") final String dataType, @QueryParam("start_date") final String startDate, @QueryParam("end_date") final String endDate) {
        return Response.ok(dataService.getEmployeeSchedule(startDate, endDate)).build();
    }
}
