package com.renting.backend.services.impl;

import com.renting.backend.dtos.request.*;
import com.renting.backend.dtos.response.*;
import com.renting.backend.entities.Customer;
import com.renting.backend.mapper.CustomerMapper;
import com.renting.backend.repositories.CustomerRepository;
import com.renting.backend.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @Override
    @Transactional
    public CustomerResponse create(CustomerCreateRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Override
    @Transactional
    public CustomerResponse update(Long id, CustomerUpdateRequest request) {

        Customer c = repository.findActiveById(id).orElseThrow();

        if (request.getName() != null) c.setName(request.getName());
        if (request.getFirstSurname() != null) c.setFirstSurname(request.getFirstSurname());
        if (request.getSecondSurname() != null) c.setSecondSurname(request.getSecondSurname());
        if (request.getNationality() != null) c.setNationality(request.getNationality());

        // Corregido: Se asigna el String directamente (ya que EmploymentStatus se cambió a String en la entidad)
        if (request.getEmploymentStatus() != null) {
            c.setEmploymentStatus(request.getEmploymentStatus().name());
        }

        if (request.getPhone() != null) c.setPhone(request.getPhone());
        if (request.getScoring() != null) c.setScoring(request.getScoring());

        // Corregido: Convierte el Boolean del Request a Integer (1 = true, 0 = false) para Oracle
        if (request.getNonPayment() != null) {
            c.setNonPayment(request.getNonPayment() ? 1 : 0);
        }

        if (request.getCareerTime() != null) c.setCareerTime(request.getCareerTime());

        return mapper.toResponse(repository.save(c));
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Customer c = repository.findActiveById(id).orElseThrow();

        if (repository.hasActiveRequests(id)) {
            throw new RuntimeException("Customer has active requests");
        }

        // Corregido: Cambiado c.setActive(false) por c.setIsActive(0) para cumplir con el NUMBER(1) de Oracle
        c.setIsActive(0);
        repository.save(c);
    }

    @Override
    public CustomerResponse findById(Long id) {
        return mapper.toResponse(repository.findActiveById(id).orElseThrow());
    }

    @Override
    public Page<CustomerResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }
}