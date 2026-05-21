package com.renting.backend.dtos.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class RequestVehicleDTO {

    private Long vehicleId;

    private List<Long> extraIds; 
}