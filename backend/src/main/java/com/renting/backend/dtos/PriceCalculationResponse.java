package com.renting.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculationResponse {

    private BigDecimal finalInvestment;   //Inversión final (Precio del vehículo + extras)
    private BigDecimal finalMonthlyFee;   //Cuota mensual final (Con extras y penalizaciones/bonificaciones por plazo)
    private BigDecimal extraFixedIncrement; //Sumatorio total de los incrementos por extras fijos
    private BigDecimal extraPercentageIncrement; //Sumatorio total de los incrementos por extras porcentuales
    private BigDecimal termAdjustment;  //Ajuste por plazo (penalización o bonificación según meses)
}
