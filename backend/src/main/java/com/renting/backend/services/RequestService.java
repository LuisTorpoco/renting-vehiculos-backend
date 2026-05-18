package com.renting.backend.services;

import com.renting.backend.dtos.request.CreateLoanRequest;
import com.renting.backend.dtos.request.ResolveLoanRequest;
import com.renting.backend.dtos.response.LoanRequestResponse;
import com.renting.backend.enums.RequestStatus;

import java.util.List;

public interface RequestService {

    LoanRequestResponse createLoanRequest(CreateLoanRequest request);

    LoanRequestResponse getLoanRequestById(Long id);

    List<LoanRequestResponse> getAllLoanRequests(RequestStatus status);

    void deleteLoanRequest(Long id);

    LoanRequestResponse resolveLoanRequest(Long id, ResolveLoanRequest request);


}
