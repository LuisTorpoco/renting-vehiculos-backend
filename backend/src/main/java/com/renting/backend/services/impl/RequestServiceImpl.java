package com.renting.backend.services.impl;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.request.ResolveRequestDTO;
import com.renting.backend.dtos.response.RequestResponseDTO;
import com.renting.backend.entities.Request;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository repository;

    @Override
    public RequestResponseDTO createLoanRequest(CreateRequestDTO request) {
        Request entity = Request.builder()
                .customerId(request.getCustomerId())
                .createdAt(LocalDateTime.now())
                .state(RequestStatus.APPROVED)
                .periodInMonths(request.getPeriodInMonths())
                .isActive("true")
                .build();
        
    }

    @Override
    public RequestResponseDTO getLoanRequestById(Long id) {
        return null;
    }

    @Override
    public List<RequestResponseDTO> getAllLoanRequests(RequestStatus status) {
        return List.of();
    }

    @Override
    public void deleteLoanRequest(Long id) {

    }

    @Override
    public RequestResponseDTO resolveLoanRequest(Long id, ResolveRequestDTO request) {
        return null;
    }
}
