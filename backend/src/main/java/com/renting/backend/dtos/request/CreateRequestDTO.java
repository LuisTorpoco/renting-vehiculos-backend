package com.renting.backend.dtos.request;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Getter
@Setter
public class CreateRequestDTO {

    @NotNull
    private Long customerId;

    @NotNull
    private Integer periodInMonths;

    //private List<RequestVehicleDTO> vehicles;
}
