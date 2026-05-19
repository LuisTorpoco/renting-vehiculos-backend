package com.renting.backend.services;

import com.renting.backend.dtos.request.*;
import com.renting.backend.dtos.response.*;
import org.springframework.data.domain.*;

public interface CustomerService {

    CustomerResponse create(CustomerCreateRequest request);

    CustomerResponse update(Long id, CustomerUpdateRequest request);

    void delete(Long id);

    CustomerResponse findById(Long id);

    Page<CustomerResponse> list(Pageable pageable);
}