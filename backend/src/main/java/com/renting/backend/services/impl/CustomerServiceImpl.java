package com.renting.backend.services.impl;

import com.renting.backend.dtos.request.*;
import com.renting.backend.dtos.response.CustomerResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Income;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.exception.ConflictException;
import com.renting.backend.exception.ResourceNotFoundException;
import com.renting.backend.mapper.CustomerMapper;
import com.renting.backend.repositories.CustomerRepository;
import com.renting.backend.repositories.IncomeRepository;
import com.renting.backend.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final IncomeRepository incomeRepository;
    private final CustomerMapper mapper;

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest customerRequest) {
        Customer customer = mapper.toEntity(customerRequest);
        return mapper.toResponse(repository.save(customer));
    }

    @Override
    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {

        var customer = repository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Optional.ofNullable(request.getName()).ifPresent(customer::setName);
        Optional.ofNullable(request.getFirstSurname()).ifPresent(customer::setFirstSurname);
        Optional.ofNullable(request.getSecondSurname()).ifPresent(customer::setSecondSurname);
        Optional.ofNullable(request.getNationality()).ifPresent(customer::setNationality);
        Optional.ofNullable(request.getEmploymentStatus()).ifPresent(customer::setEmploymentStatus);
        Optional.ofNullable(request.getPhone()).ifPresent(customer::setPhone);
        Optional.ofNullable(request.getScoring()).ifPresent(customer::setScoring);
        Optional.ofNullable(request.getNonPayment()).ifPresent(customer::setNonPayment);
        Optional.ofNullable(request.getCareerTime()).ifPresent(customer::setCareerTime);

        return mapper.toResponse(repository.save(customer));
    }

    @Override
    @Transactional
    public void delete(Long id) {

        var customer = repository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (repository.hasPendingRequests(id, RequestStatus.PENDING_ANALYST.name())) {
            throw new ConflictException(
                    "No se puede borrar el cliente: tiene solicitudes pendientes."
            );
        }

        customer.setIsActive(0);
        repository.save(customer);
    }


    @Override
    public CustomerResponse findActiveCustomerById(Long id) {

        Customer customer = repository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return mapper.toResponse(customer);
    }

    @Override
    public Page<CustomerResponse> listActiveCustomers(Pageable pageable) {

        return repository.findAllActive(pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional
    public void addIncome(Long customerId, IncomeRequest request) {

        Customer customer = repository.findActiveById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Income income = new Income();

        income.setCustomer(customer);
        income.setPreTaxes(request.getPreTaxes());
        income.setPostTaxes(request.getPostTaxes());
        income.setCreatedAt(LocalDateTime.now());

        incomeRepository.save(income);
    }
}