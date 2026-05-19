package com.renting.backend.services;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.request.ResolveRequestDTO;
import com.renting.backend.dtos.response.RequestResponseDTO;
import com.renting.backend.enums.RequestStatus;

import java.util.List;

public interface RequestService {

    RequestResponseDTO createLoanRequest(CreateRequestDTO request);

    RequestResponseDTO getLoanRequestById(Long id);

    List<RequestResponseDTO> getAllLoanRequests(RequestStatus status);

    void deleteLoanRequest(Long id);

    RequestResponseDTO resolveLoanRequest(Long id, ResolveRequestDTO request);


}
