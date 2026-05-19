package com.renting.backend.controllers;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.request.ResolveRequestDTO;
import com.renting.backend.dtos.response.RequestResponseDTO;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.services.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loan-requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestResponseDTO> createLoanRequest(
            @RequestBody CreateRequestDTO request
    ) {
        RequestResponseDTO response =
                requestService.createLoanRequest(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestResponseDTO> getLoanRequestById(
            @PathVariable Long id
    ) {
        RequestResponseDTO response =
                requestService.getLoanRequestById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RequestResponseDTO>> getAllLoanRequests(
            @RequestParam(required = false) RequestStatus status
    ) {
        List<RequestResponseDTO> responses =
                requestService.getAllLoanRequests(status);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoanRequest(
            @PathVariable Long id
    ) {
        requestService.deleteLoanRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<RequestResponseDTO> resolveLoanRequest(
            @PathVariable Long id,
            @RequestBody ResolveRequestDTO request
    ) {
        RequestResponseDTO response =
                requestService.resolveLoanRequest(id, request);
        return ResponseEntity.ok(response);
    }
}