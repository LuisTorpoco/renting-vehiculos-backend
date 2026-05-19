package com.renting.backend.dtos.request;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRequestDTO {

    @NotNull
    private Long customerId;

    @NotNull
    private Integer periodInMonths;

    @NotEmpty
    private List<RequestVehicleDTO> vehicles;
}
