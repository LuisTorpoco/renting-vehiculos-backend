package com.renting.backend.dtos.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//DTO para calcular precio total de un alquiler
public class PriceCalculationRequest {
    private Long vehicleId;
    private List<Long> extraIds;
    private Integer months;
}
