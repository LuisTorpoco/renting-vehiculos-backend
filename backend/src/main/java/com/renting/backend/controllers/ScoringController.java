package com.renting.backend.controllers;

import com.renting.backend.dtos.response.ScoringResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Request;
import com.renting.backend.exception.ResourceNotFoundException;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scoring")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ScoringController {

    private final ScoringService scoringService;
    private final RequestRepository requestRepository;

    @PostMapping("/evaluate/{requestId}")
    public ResponseEntity<ScoringResponse>
    evaluateScoring(
            @PathVariable
            Long requestId
    ) {

        Request request =
                requestRepository
                        .findById(requestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Request not found with id:" +requestId
                                )
                        );

        Customer customer =
                request.getCustomer();

        ScoringResponse response =
                scoringService.evaluate(
                        customer,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}