package com.renting.backend.services.impl;

import com.renting.backend.dtos.request.CustomerCreateRequest;
import com.renting.backend.dtos.request.CustomerUpdateRequest;
import com.renting.backend.dtos.request.IncomeRequest;
import com.renting.backend.dtos.response.CustomerResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Income;
import com.renting.backend.mapper.CustomerMapper;
import com.renting.backend.repositories.CustomerRepository;
import com.renting.backend.repositories.IncomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private CustomerMapper mapper;

    @InjectMocks
    private CustomerServiceImpl service;

    private Customer customer;

    @BeforeEach
    void setUp() {

        customer = Customer.builder()
                .id(1L)
                .nif("12345678A")
                .name("Aaron")
                .firstSurname("Garcia")
                .nationality("Spanish")
                .birthdate(LocalDate.of(2000, 1, 1))
                .scoring(BigDecimal.valueOf(4.5))
                .employmentStatus("EMPLOYED")
                .phone("666666666")
                .nonPayment(0)
                .isActive(1)
                .careerTime(LocalDate.of(2020, 1, 1))
                .build();
    }

    @Test
    void shouldCreateCustomer() {

        CustomerCreateRequest request =
                new CustomerCreateRequest();

        CustomerResponse response =
                CustomerResponse.builder()
                        .id(1L)
                        .name("Aaron")
                        .build();

        when(mapper.toEntity(request))
                .thenReturn(customer);

        when(repository.save(customer))
                .thenReturn(customer);

        when(mapper.toResponse(customer))
                .thenReturn(response);

        CustomerResponse result =
                service.create(request);

        assertNotNull(result);

        assertEquals(
                "Aaron",
                result.getName()
        );

        verify(repository)
                .save(customer);
    }

    @Test
    void shouldUpdateCustomer() {

        CustomerUpdateRequest request =
                new CustomerUpdateRequest();

        request.setName("Aaron Updated");

        CustomerResponse response =
                CustomerResponse.builder()
                        .id(1L)
                        .name("Aaron Updated")
                        .build();

        when(repository.findActiveById(1L))
                .thenReturn(Optional.of(customer));

        when(repository.save(customer))
                .thenReturn(customer);

        when(mapper.toResponse(customer))
                .thenReturn(response);

        CustomerResponse result =
                service.update(1L, request);

        assertEquals(
                "Aaron Updated",
                result.getName()
        );

        verify(repository)
                .save(customer);
    }

    @Test
    void shouldDeleteCustomerLogically() {

        when(repository.findActiveById(1L))
                .thenReturn(Optional.of(customer));

        when(repository.hasPendingRequests(1L))
                .thenReturn(false);

        service.delete(1L);

        assertEquals(
                0,
                customer.getIsActive()
        );

        verify(repository)
                .save(customer);
    }

    @Test
    void shouldThrowExceptionWhenCustomerHasPendingRequests() {

        when(repository.findActiveById(1L))
                .thenReturn(Optional.of(customer));

        when(repository.hasPendingRequests(1L))
                .thenReturn(true);

        IllegalStateException ex =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.delete(1L)
                );

        assertEquals(
                "Customer has pending requests",
                ex.getMessage()
        );
    }

    @Test
    void shouldFindCustomerById() {

        CustomerResponse response =
                CustomerResponse.builder()
                        .id(1L)
                        .name("Aaron")
                        .build();

        when(repository.findActiveById(1L))
                .thenReturn(Optional.of(customer));

        when(mapper.toResponse(customer))
                .thenReturn(response);

        CustomerResponse result =
                service.findById(1L);

        assertEquals(
                1L,
                result.getId()
        );
    }

    @Test
    void shouldListCustomers() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Customer> page =
                new PageImpl<>(List.of(customer));

        CustomerResponse response =
                CustomerResponse.builder()
                        .id(1L)
                        .name("Aaron")
                        .build();

        when(repository.findAllActive(pageable))
                .thenReturn(page);

        when(mapper.toResponse(customer))
                .thenReturn(response);

        Page<CustomerResponse> result =
                service.list(pageable);

        assertEquals(
                1,
                result.getContent().size()
        );
    }

    @Test
    void shouldAddIncome() {

        IncomeRequest request =
                new IncomeRequest();

        request.setPreTaxes(
                BigDecimal.valueOf(3000)
        );

        request.setPostTaxes(
                BigDecimal.valueOf(2200)
        );

        when(repository.findActiveById(1L))
                .thenReturn(Optional.of(customer));

        service.addIncome(1L, request);

        verify(incomeRepository)
                .save(any(Income.class));
    }
}