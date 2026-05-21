package com.renting.backend.services;

import com.renting.backend.dtos.request.*;
import com.renting.backend.dtos.response.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerResponse create(CustomerRequest request);

    CustomerResponse update(Long id, CustomerRequest request);

    void delete(Long id);

    CustomerResponse findActiveCustomerById(Long id);

    Page<CustomerResponse> listActiveCustomers(Pageable pageable);

    void addIncome(Long customerId, IncomeRequest request);
}