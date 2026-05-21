package com.renting.backend.dtos.response;

import com.renting.backend.enums.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestWithDetailsResponseDTO {

    //solicitud
    private Long requestId;
    private RequestStatus state;
    private Integer periodInMonths;
    private LocalDateTime createdAt;
    private LocalDateTime resolutionDate;

    //cliente
    private Long customerId;
    private String customerName;
    private String customerFirstSurname;
    private String customerSecondSurname;
    private String customerNif;

    //vehículos
    private List<RequestDetailResponseDTO> vehicles;
}
