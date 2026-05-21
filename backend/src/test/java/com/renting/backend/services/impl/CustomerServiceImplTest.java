package com.renting.backend.services.impl;

import com.renting.backend.dtos.request.CustomerRequest;
import com.renting.backend.dtos.request.IncomeRequest;
import com.renting.backend.dtos.response.CustomerResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Income;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.exception.BusinessException;
import com.renting.backend.exception.ConflictException;
import com.renting.backend.exception.ResourceNotFoundException;
import com.renting.backend.mapper.CustomerMapper;
import com.renting.backend.repositories.CustomerRepository;
import com.renting.backend.repositories.IncomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServicesImpl flow tests")
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {

        customer = Customer.builder()
                .id(1L)
                .nif("12345678A")
                .name("Juan García")
                .firstSurname("López")
                .secondSurname("Martínez")
                .nationality("ES")
                .birthdate(LocalDate.of(1990, 5, 15))
                .scoring(new BigDecimal("5.50"))
                .employmentStatus("EMPLOYED")
                .phone("912345678")
                .nonPayment(0)
                .isActive(1)
                .careerTime(LocalDate.of(2018, 3, 10))
                .build();
    }

    @Test
    @DisplayName("Should create a customer with valid values")
    void shouldCreateCustomerWithValidValues() {

        CustomerRequest customerRequest = new CustomerRequest();

        CustomerResponse customerResponse = CustomerResponse
                .builder()
                .id(1L)
                .nif("12345678A")
                .name("Juan García")
                .firstSurname("López")
                .secondSurname("Martínez")
                .nationality("ES")
                .birthdate(LocalDate.of(1990, 5, 15))
                .scoring(new BigDecimal("5.50"))
                .employmentStatus("EMPLOYED")
                .phone("912345678")
                .nonPayment(0)
                .isActive(true)
                .careerTime(LocalDate.of(2018, 3, 10))
                .build();

        when(customerMapper.toEntity(customerRequest))
                .thenReturn(customer);

        when(customerRepository.save(customer))
                .thenReturn(customer);

        when(customerMapper.toResponse(customer))
                .thenReturn(customerResponse);

        var result = customerService.create(customerRequest);

        verify(customerMapper).toEntity(customerRequest);
        verify(customerRepository).save(customer);
        verify(customerMapper).toResponse(customer);

        assertEquals("12345678A", result.getNif());
        assertTrue(result.isActive());
    }

    @Test
    @DisplayName("Should throw not found when customer does not exist")
    void shouldThrowNotFoundWhenCustomerDoesNotExists() {

        when(customerRepository.findActiveById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.findActiveCustomerById(9999L)
        );
    }

    @Test
    @DisplayName("Should find customer by id")
    void shouldFindCustomerById() {

        CustomerResponse response = CustomerResponse.builder()
                .id(1L)
                .name("Juan García")
                .build();

        when(customerRepository.findActiveById(1L))
                .thenReturn(Optional.of(customer));

        when(customerMapper.toResponse(customer))
                .thenReturn(response);

        CustomerResponse result =
                customerService.findActiveCustomerById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Juan García", result.getName());
    }

    @Test
    @DisplayName("Should update customer")
    void shouldUpdateCustomer() {

        CustomerRequest request = new CustomerRequest();
        request.setName("Updated Name");

        CustomerResponse response = CustomerResponse.builder()
                .id(1L)
                .name("Updated Name")
                .build();

        when(customerRepository.findActiveById(1L))
                .thenReturn(Optional.of(customer));

        when(customerRepository.save(customer))
                .thenReturn(customer);

        when(customerMapper.toResponse(customer))
                .thenReturn(response);

        CustomerResponse result =
                customerService.update(1L, request);

        assertEquals("Updated Name", result.getName());

        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("Should throw exception when updating non existing customer")
    void shouldThrowExceptionWhenUpdatingCustomer() {

        when(customerRepository.findActiveById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.update(1L, new CustomerRequest())
        );
    }

    @Test
    @DisplayName("Should delete customer logically")
    void shouldDeleteCustomerLogically() {

        when(customerRepository.findActiveById(1L))
                .thenReturn(Optional.of(customer));

        when(customerRepository.hasPendingRequests(
                1L,
                RequestStatus.PENDING_ANALYST
        )).thenReturn(false);

        customerService.delete(1L);

        assertEquals(
                0,
                customer.getIsActive()
        );

        verify(customerRepository)
                .save(customer);
    }

    @Test
    @DisplayName("Should throw conflict when customer has pending requests")
    void shouldThrowConflictWhenCustomerHasPendingRequests() {

        when(customerRepository.findActiveById(1L))
                .thenReturn(Optional.of(customer));

        when(customerRepository.hasPendingRequests(
                1L,
                RequestStatus.PENDING_ANALYST
        )).thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> customerService.delete(1L)
        );
    }

    @Test
    @DisplayName("Should list active customers")
    void shouldListActiveCustomers() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customerPage =
                new PageImpl<>(List.of(customer));

        CustomerResponse response =
                CustomerResponse.builder()
                        .id(1L)
                        .name("Juan García")
                        .build();

        when(customerRepository.findAllActive(pageable))
                .thenReturn(customerPage);

        when(customerMapper.toResponse(customer))
                .thenReturn(response);

        Page<CustomerResponse> result =
                customerService.listActiveCustomers(pageable);

        assertEquals(
                1,
                result.getContent().size()
        );
    }

    @Test
    @DisplayName("Should add income to customer")
    void shouldAddIncome() {

        IncomeRequest request = new IncomeRequest();

        request.setPreTaxes(new BigDecimal("3000"));
        request.setPostTaxes(new BigDecimal("2200"));

        when(customerRepository.findActiveById(1L))
                .thenReturn(Optional.of(customer));

        customerService.addIncome(1L, request);

        verify(incomeRepository)
                .save(any(Income.class));
    }

    @Test
    @DisplayName("Should throw exception when adding income to inactive customer")
    void shouldThrowExceptionWhenAddingIncomeToInactiveCustomer() {

        customer.setIsActive(0);

        IncomeRequest request = new IncomeRequest();

        when(customerRepository.findActiveById(1L))
                .thenReturn(Optional.of(customer));

        assertThrows(
                BusinessException.class,
                () -> customerService.addIncome(1L, request)
        );
    }
}