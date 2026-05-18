package com.renting.backend.services.impl;

import com.renting.backend.dtos.request.CreateLoanRequest;
import com.renting.backend.dtos.request.ResolveLoanRequest;
import com.renting.backend.dtos.response.LoanRequestResponse;
import com.renting.backend.entities.Request;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository repository;

    @Override
    public LoanRequestResponse createLoanRequest(CreateLoanRequest request) {
        Request entity = Request.builder()
                .customerId(request.getCustomerId())
                .createdAt(LocalDateTime.now())
                .state(RequestStatus.APPROVED)
                .periodInMonths(request.getPeriodInMonths())
                .isActive("true")
                .build();
        
    }

    @Override
    public LoanRequestResponse getLoanRequestById(Long id) {
        return null;
    }

    @Override
    public List<LoanRequestResponse> getAllLoanRequests(RequestStatus status) {
        return List.of();
    }

    @Override
    public void deleteLoanRequest(Long id) {

    }

    @Override
    public LoanRequestResponse resolveLoanRequest(Long id, ResolveLoanRequest request) {
        return null;
    }
}
