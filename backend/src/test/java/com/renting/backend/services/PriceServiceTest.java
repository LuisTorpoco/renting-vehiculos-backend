package com.renting.backend.services;

import com.renting.backend.dtos.request.PriceCalculationRequest;
import com.renting.backend.dtos.response.PriceCalculationResponse;
import com.renting.backend.entities.Extra;
import com.renting.backend.entities.Vehicle;
import com.renting.backend.repositories.ExtraRepository;
import com.renting.backend.repositories.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock private VehicleRepository vehicleRepository;
    @Mock private ExtraRepository extraRepository;

    @InjectMocks
    private PriceService priceService;

    private static BigDecimal bd(String val) {
        return new BigDecimal(val);
    }

    private static void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
        assertEquals(0, expected.compareTo(actual),
                "Expected " + expected + " but got " + actual);
    }

    @Test
    @DisplayName("Should calculate price without extras at 12-month term")
    void shouldCalculatePriceWithNoExtras() {
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setVehicleId(1L);
        request.setMonths(12);

        Vehicle vehicle = new Vehicle();
        vehicle.setPrice(bd("15000"));
        vehicle.setBaseMonthlyFee(bd("250"));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        PriceCalculationResponse response = priceService.calculatePrice(request);

        assertBigDecimalEquals(bd("15000.00"), response.getFinalInvestment());
        assertBigDecimalEquals(bd("250.00"),   response.getFinalMonthlyFee());
        assertBigDecimalEquals(bd("0.00"),     response.getTermAdjustment());
    }

    @Test
    @DisplayName("Should calculate price with fixed and percentage extras at 12-month term")
    void shouldCalculatePriceWithFixedAndPercentageExtras() {
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setVehicleId(1L);
        request.setExtraIds(List.of(2L, 3L));
        request.setMonths(12);

        Vehicle vehicle = new Vehicle();
        vehicle.setPrice(bd("20000"));
        vehicle.setBaseMonthlyFee(bd("300"));

        Extra fixedExtra = new Extra();
        fixedExtra.setId(2L);
        fixedExtra.setPrice(bd("100"));
        fixedExtra.setPercentage(null);

        Extra percentageExtra = new Extra();
        percentageExtra.setId(3L);
        percentageExtra.setPrice(null);
        percentageExtra.setPercentage(bd("10"));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(extraRepository.findAllById(any())).thenReturn(List.of(fixedExtra, percentageExtra));

        PriceCalculationResponse response = priceService.calculatePrice(request);

        assertBigDecimalEquals(bd("23200.00"), response.getFinalInvestment());
        assertBigDecimalEquals(bd("440.00"),   response.getFinalMonthlyFee());
        assertBigDecimalEquals(bd("100.00"),   response.getExtraFixedIncrement());
        assertBigDecimalEquals(bd("40.00"),    response.getExtraPercentageIncrement());
        assertBigDecimalEquals(bd("0.00"),     response.getTermAdjustment());
    }

    @Test
    @DisplayName("Should throw RuntimeException when vehicle does not exist")
    void shouldThrowExceptionWhenVehicleDoesNotExist() {
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setVehicleId(99L);

        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> priceService.calculatePrice(request)
        );

        assertTrue(exception.getMessage().contains("99"));
        verifyNoInteractions(extraRepository);
    }

    @Test
    @DisplayName("Should apply 10% penalty per missing month when term is below 12")
    void shouldApplyPenaltyWhenTermIsBelowTwelveMonths() {
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setVehicleId(1L);
        request.setMonths(6); // 6 missing months → 60% penalty

        Vehicle vehicle = new Vehicle();
        vehicle.setPrice(bd("15000"));
        vehicle.setBaseMonthlyFee(bd("250"));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        PriceCalculationResponse response = priceService.calculatePrice(request);

        assertBigDecimalEquals(bd("150.00"), response.getTermAdjustment());
        assertBigDecimalEquals(bd("400.00"), response.getFinalMonthlyFee());
    }

    @Test
    @DisplayName("Should apply 3% discount per extra month when term is above 12")
    void shouldApplyDiscountWhenTermIsAboveTwelveMonths() {
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setVehicleId(1L);
        request.setMonths(15);

        Vehicle vehicle = new Vehicle();
        vehicle.setPrice(bd("15000"));
        vehicle.setBaseMonthlyFee(bd("250"));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        PriceCalculationResponse response = priceService.calculatePrice(request);

        assertBigDecimalEquals(bd("-22.50"), response.getTermAdjustment());
        assertBigDecimalEquals(bd("227.50"), response.getFinalMonthlyFee());
    }

    @Test
    @DisplayName("Should cap discount at 20% regardless of term length")
    void shouldCapDiscountAtTwentyPercent() {
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setVehicleId(1L);
        request.setMonths(20);

        Vehicle vehicle = new Vehicle();
        vehicle.setPrice(bd("15000"));
        vehicle.setBaseMonthlyFee(bd("250"));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        PriceCalculationResponse response = priceService.calculatePrice(request);

        assertBigDecimalEquals(bd("-50.00"), response.getTermAdjustment());
        assertBigDecimalEquals(bd("200.00"), response.getFinalMonthlyFee());
    }

    @Test
    @DisplayName("Should default to 12-month term when months is null")
    void shouldDefaultToTwelveMonthsWhenMonthsIsNull() {
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setVehicleId(1L);
        request.setMonths(null); // ← tests the NPE fix

        Vehicle vehicle = new Vehicle();
        vehicle.setPrice(bd("15000"));
        vehicle.setBaseMonthlyFee(bd("250"));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        PriceCalculationResponse response = priceService.calculatePrice(request);

        assertBigDecimalEquals(bd("250.00"), response.getFinalMonthlyFee());
        assertBigDecimalEquals(bd("0.00"),   response.getTermAdjustment());
    }
}
