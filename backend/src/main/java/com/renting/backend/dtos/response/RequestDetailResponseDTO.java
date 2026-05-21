package com.renting.backend.dtos.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDetailResponseDTO {

    private Long vehicleId;
    private String brand;
    private String model;
    private String licensePlate;
    private BigDecimal price;
    private BigDecimal baseMonthlyFee;
    private List<ExtraResponseDTO> extras;
}
