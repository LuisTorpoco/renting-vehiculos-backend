package com.renting.backend.controllers;

import com.renting.backend.dtos.request.CreateLoanRequest;
import com.renting.backend.dtos.request.ResolveLoanRequest;
import com.renting.backend.dtos.response.LoanRequestResponse;
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
    public ResponseEntity<LoanRequestResponse> createLoanRequest(
            @RequestBody CreateLoanRequest request
    ) {
        LoanRequestResponse response =
                requestService.createLoanRequest(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanRequestResponse> getLoanRequestById(
            @PathVariable Long id
    ) {
        LoanRequestResponse response =
                requestService.getLoanRequestById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LoanRequestResponse>> getAllLoanRequests(
            @RequestParam(required = false) RequestStatus status
    ) {
        List<LoanRequestResponse> responses =
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
    public ResponseEntity<LoanRequestResponse> resolveLoanRequest(
            @PathVariable Long id,
            @RequestBody ResolveLoanRequest request
    ) {
        LoanRequestResponse response =
                requestService.resolveLoanRequest(id, request);
        return ResponseEntity.ok(response);
    }
}