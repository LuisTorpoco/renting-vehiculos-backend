package com.renting.backend.dtos.response;

import com.renting.backend.enums.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class RequestResponseDTO {

    private Long id;

    private Long customerId;

    private RequestStatus status;

    private Integer periodInMonths;

    private LocalDateTime createdAt;

    private LocalDateTime resolutionDate;

    // Lista de ids de vehículos incluidos en la solicitud
    private List<Long> vehicleIds;
}
