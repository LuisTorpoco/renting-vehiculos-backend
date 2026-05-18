package com.renting.backend.controllers;

import com.renting.backend.dtos.request.PriceCalculationRequest;
import com.renting.backend.dtos.response.PriceCalculationResponse;
import com.renting.backend.entities.Vehicle;
import com.renting.backend.repositories.VehicleRepository;
import com.renting.backend.services.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VehicleController {

    private final PriceService priceService;
    private final VehicleRepository vehicleRepository;

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllActiveVehicles() {
        // Buscamos solo los vehículos que tengan isActive = 1 (respetado el borrado logico)
        List<Vehicle> vehicles = vehicleRepository.findByIsActive(1);
        return ResponseEntity.ok(vehicles);
    }

    @PostMapping("/calculate-price")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(@RequestBody PriceCalculationRequest request) {
        PriceCalculationResponse response = priceService.calculateRentingPrice(request);
        return ResponseEntity.ok(response);
    }
}