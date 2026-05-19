package com.renting.backend.services;

import com.renting.backend.dtos.request.PriceCalculationRequest;
import com.renting.backend.dtos.response.PriceCalculationResponse;
import com.renting.backend.entities.Extra;
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

    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request) {
        // Buscar el vehículo en la base de datos (Usando tu modelo real)
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + request.getVehicleId()));

        // Inicializar valores base usando los nombres de tu tabla VEHICLE
        BigDecimal finalInvestment = vehicle.getPrice();
        BigDecimal finalMonthlyFee = vehicle.getBaseMonthlyFee();

        BigDecimal extraFixedIncrement = BigDecimal.ZERO;
        BigDecimal extraPercentageIncrement = BigDecimal.ZERO;


        if (request.getExtraIds() != null && !request.getExtraIds().isEmpty()) {
            List<Extra> extras = extraRepository.findAllById(request.getExtraIds());

            for (Extra extra : extras) {

                if (extra.getPercentage() != null && extra.getPercentage().compareTo(BigDecimal.ZERO) > 0) {

                    BigDecimal percentageRate = extra.getPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

                    BigDecimal increment = vehicle.getBaseMonthlyFee().multiply(percentageRate);

                    extraPercentageIncrement = extraPercentageIncrement.add(increment);
                }

                else if (extra.getPrice() != null) {
                    extraFixedIncrement = extraFixedIncrement.add(extra.getPrice());
                    //Un extra fijo incrementa la inversión inicial (por ejemplo multiplicándolo por un año base)
                    finalInvestment = finalInvestment.add(extra.getPrice().multiply(BigDecimal.valueOf(12)));
                }
            }
        }

        //Aplicar los incrementos de los extras a la cuota mensual
        finalMonthlyFee = finalMonthlyFee.add(extraFixedIncrement).add(extraPercentageIncrement);

        // Calcular Regla de negocio por Plazo (Meses de contrato)
        int months = request.getMonths();
        BigDecimal termAdjustment = BigDecimal.ZERO;

        if (months < 12) {
            //Penalización: +10% por cada mes faltante para llegar a 12
            int missingMonths = 12 - months;
            BigDecimal penaltyRate = BigDecimal.valueOf(missingMonths)
                    .multiply(BigDecimal.valueOf(0.10));

            termAdjustment = finalMonthlyFee.multiply(penaltyRate);
            finalMonthlyFee = finalMonthlyFee.add(termAdjustment);
        } else if (months > 12) {
            //Descuento: -3% por cada mes extra después de 12
            int extraMonths = months - 12;
            BigDecimal discountRate = BigDecimal.valueOf(extraMonths)
                    .multiply(BigDecimal.valueOf(0.03));

            // El descuento no puede superar el 20% total
            BigDecimal maxDiscountRate = BigDecimal.valueOf(0.20);
            if (discountRate.compareTo(maxDiscountRate) > 0) {
                discountRate = maxDiscountRate;
            }

            termAdjustment = finalMonthlyFee.multiply(discountRate).negate(); // Negativo porque resta
            finalMonthlyFee = finalMonthlyFee.add(termAdjustment);
        }

        // Construir y devolver la respuesta con redondeo comercial a 2 decimales
        return PriceCalculationResponse.builder()
                .finalInvestment(finalInvestment.setScale(2, RoundingMode.HALF_UP))
                .finalMonthlyFee(finalMonthlyFee.setScale(2, RoundingMode.HALF_UP))
                .extraFixedIncrement(extraFixedIncrement.setScale(2, RoundingMode.HALF_UP))
                .extraPercentageIncrement(extraPercentageIncrement.setScale(2, RoundingMode.HALF_UP))
                .termAdjustment(termAdjustment.setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}