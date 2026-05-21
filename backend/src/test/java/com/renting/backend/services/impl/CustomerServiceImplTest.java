package com.renting.backend.services.impl;

import com.renting.backend.dtos.request.CustomerRequest;
import com.renting.backend.dtos.response.CustomerResponse;
import com.renting.backend.entities.Customer;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Should create a customer with valid values")
    void shouldCreateCustomerWithValidValues(){
        CustomerRequest customerRequest = new CustomerRequest();
        var customer = Customer.builder()
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

        when(customerMapper.toEntity(customerRequest)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        var result = customerService.create(customerRequest);

        verify(customerMapper).toEntity(customerRequest);
        verify(customerRepository).save(customer);
        verify(customerMapper).toResponse(customer);
        assertEquals("12345678A", result.getNif());
        assertTrue(result.isActive());
    }

    @Test
    void shouldThrowNotFoundWhenCustomerDoesNotExists(){
        when(customerRepository.findActiveById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.findActiveCustomerById(9999L));
    }
}