package com.renting.backend.services;

import com.renting.backend.dtos.request.PriceCalculationRequest;
import com.renting.backend.dtos.response.PriceCalculationResponse;
import com.renting.backend.entities.Extra;
import com.renting.backend.entities.ExtraType;
import com.renting.backend.entities.Vehicle;
import com.renting.backend.repositories.ExtraRepository;
import com.renting.backend.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final VehicleRepository vehicleRepository;
    private final ExtraRepository extraRepository;

    //Realiza el cálculo completo del presupuesto de renting según las reglas de negocio establecidas.

    public PriceCalculationResponse calculateRentingPrice(PriceCalculationRequest request) {

        //Validar y recuperar el vehículo de Oracle
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado con ID: " + request.getVehicleId()));

        //Recuperar los extras seleccionados por el usuario
        List<Extra> selectedExtras = extraRepository.findAllById(request.getExtraIds());

        //Valores base de la base de datos
        BigDecimal currentInvestment = vehicle.getBaseInvestment();
        BigDecimal currentMonthlyFee = vehicle.getBaseMonthlyFee();

        BigDecimal totalFixedIncrement = BigDecimal.ZERO;
        BigDecimal totalPercentageIncrement = BigDecimal.ZERO;

        //Procesar cada extra según su tipo (FIXED o PERCENTAGE)
        for (Extra extra : selectedExtras) {
            if (extra.getExtraType() == ExtraType.FIXED) {
                //Regla del enunciado: "un importe fijo ... value * 12 a inversión y value a cuota"
                BigDecimal investmentImpact = extra.getValue().multiply(BigDecimal.valueOf(12));
                currentInvestment = currentInvestment.add(investmentImpact);

                currentMonthlyFee = currentMonthlyFee.add(extra.getValue());
                totalFixedIncrement = totalFixedIncrement.add(extra.getValue());

            } else if (extra.getExtraType() == ExtraType.PERCENTAGE) {

                BigDecimal percentageImpact = vehicle.getBaseMonthlyFee().multiply(extra.getValue());

                currentMonthlyFee = currentMonthlyFee.add(percentageImpact);
                totalPercentageIncrement = totalPercentageIncrement.add(percentageImpact);
            }
        }

        int monthsDelta = request.getMonths() - 12;
        BigDecimal termAdjustment = BigDecimal.ZERO;

        if (monthsDelta < 0) {
            //Penalización: +10% en la cuota por cada mes que baje de 12
            int missingMonths = Math.abs(monthsDelta);
            BigDecimal penaltyRate = BigDecimal.valueOf(0.10).multiply(BigDecimal.valueOf(missingMonths));
            termAdjustment = vehicle.getBaseMonthlyFee().multiply(penaltyRate);
            currentMonthlyFee = currentMonthlyFee.add(termAdjustment);

        } else if (monthsDelta > 0) {
            //Bonificación: -3% en la cuota por cada mes que suba de 12, con un tope máximo de un 20% total
            int extraMonths = monthsDelta;
            BigDecimal discountRate = BigDecimal.valueOf(0.03).multiply(BigDecimal.valueOf(extraMonths));

            //Aplicar el tope del 20% (0.20)
            BigDecimal maxDiscountRate = BigDecimal.valueOf(0.20);
            if (discountRate.compareTo(maxDiscountRate) > 0) {
                discountRate = maxDiscountRate;
            }

            termAdjustment = vehicle.getBaseMonthlyFee().multiply(discountRate).negate(); // Negativo porque resta
            currentMonthlyFee = currentMonthlyFee.add(termAdjustment);
        }

        // Construir la respuesta con redondeo comercial a 2 decimales
        return PriceCalculationResponse.builder()
                .finalInvestment(currentInvestment.setScale(2, RoundingMode.HALF_UP))
                .finalMonthlyFee(currentMonthlyFee.setScale(2, RoundingMode.HALF_UP))
                .extraFixedIncrement(totalFixedIncrement.setScale(2, RoundingMode.HALF_UP))
                .extraPercentageIncrement(totalPercentageIncrement.setScale(2, RoundingMode.HALF_UP))
                .termAdjustment(termAdjustment.setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}
