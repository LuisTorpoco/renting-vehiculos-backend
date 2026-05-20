package com.renting.backend.services;

import com.renting.backend.dtos.request.*;
import com.renting.backend.dtos.response.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerResponse create(CustomerCreateRequest request);

    CustomerResponse update(Long id, CustomerUpdateRequest request);

    void delete(Long id);

    CustomerResponse findById(Long id);

    Page<CustomerResponse> list(Pageable pageable);

    void addIncome(Long customerId, IncomeRequest request);
}