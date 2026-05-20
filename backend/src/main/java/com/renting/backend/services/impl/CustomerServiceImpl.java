package com.renting.backend.services.impl;

import com.renting.backend.dtos.request.*;
import com.renting.backend.dtos.response.CustomerResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Income;
import com.renting.backend.exception.BusinessException;
import com.renting.backend.mapper.CustomerMapper;
import com.renting.backend.repositories.CustomerRepository;
import com.renting.backend.repositories.IncomeRepository;
import com.renting.backend.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final IncomeRepository incomeRepository;
    private final CustomerMapper mapper;

    @Override
    @Transactional
    public CustomerResponse create(CustomerCreateRequest request) {

        Customer customer = mapper.toEntity(request);

        return mapper.toResponse(repository.save(customer));
    }

    @Override
    @Transactional
    public CustomerResponse update(Long id, CustomerUpdateRequest request) {

        Customer c = repository.findActiveById(id)
                .orElseThrow();

        if (request.getName() != null) c.setName(request.getName());
        if (request.getFirstSurname() != null) c.setFirstSurname(request.getFirstSurname());
        if (request.getSecondSurname() != null) c.setSecondSurname(request.getSecondSurname());
        if (request.getNationality() != null) c.setNationality(request.getNationality());
        if (request.getEmploymentStatus() != null) c.setEmploymentStatus(request.getEmploymentStatus());
        if (request.getPhone() != null) c.setPhone(request.getPhone());
        if (request.getScoring() != null) c.setScoring(request.getScoring());
        if (request.getNonPayment() != null) c.setNonPayment(request.getNonPayment());
        if (request.getCareerTime() != null) c.setCareerTime(request.getCareerTime());

        return mapper.toResponse(repository.save(c));
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Customer c = repository.findActiveById(id)
                .orElseThrow();

        if (repository.hasActiveRequests(id)) {
            throw new BusinessException("No se puede eliminar el cliente porque tiene solicitudes activas.");
        }

        c.setIsActive(0);

        repository.save(c);
    }

    @Override
    public CustomerResponse findById(Long id) {

        Customer customer = repository.findActiveById(id)
                .orElseThrow();

        return mapper.toResponse(customer);
    }

    @Override
    public Page<CustomerResponse> list(Pageable pageable) {

        return repository.findByIsActive(1,pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional
    public void addIncome(Long customerId, IncomeRequest request) {

        Customer customer = repository.findActiveById(customerId)
                .orElseThrow();

        Income income = new Income();

        income.setCustomer(customer);
        income.setPreTaxes(request.getPreTaxes());
        income.setPostTaxes(request.getPostTaxes());
        income.setCreatedAt(LocalDateTime.now());

        incomeRepository.save(income);
    }
}