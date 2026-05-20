package com.renting.backend.controllers;

import com.renting.backend.dtos.request.PriceCalculationRequest;
import com.renting.backend.dtos.response.PriceCalculationResponse;
import com.renting.backend.entities.Vehicle;
import com.renting.backend.repositories.VehicleRepository;
import com.renting.backend.services.PriceService;
import jakarta.validation.Valid; 
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VehicleController {

    private final PriceService priceService;
    private final VehicleRepository vehicleRepository;

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllActiveVehicles() {

        List<Vehicle> vehicles = vehicleRepository.findByAvailable(1);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle>
    getVehicleById(
            @PathVariable
            Long id
    ) {

        Vehicle vehicle =
                vehicleRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vehicle not found with id: "
                                                + id
                                )
                        );

        return ResponseEntity.ok(
                vehicle
        );
    }

    @PostMapping("/calculate-price")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(@Valid @RequestBody PriceCalculationRequest request) {
        PriceCalculationResponse response = priceService.calculatePrice(request);
        return ResponseEntity.ok(response);
    }
}