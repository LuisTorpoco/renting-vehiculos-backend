package com.renting.backend.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.renting.backend.dtos.request.CustomerCreateRequest;
import com.renting.backend.entities.Customer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

class CustomerMapperTest {
    private final CustomerMapper customerMapper = new CustomerMapper();

    @Test
    void toEntity_DeberiaMapearRequestAEntidadCorrectamente() {

        CustomerCreateRequest request = new CustomerCreateRequest();
        request.setNif("12345678Z");
        request.setName("Abraham");
        request.setFirstSurname("García");
        request.setSecondSurname("Pérez");
        request.setNationality("Española");
        request.setBirthdate(LocalDate.of(1995, 5, 20));
        request.setScoring(BigDecimal.valueOf(4));
        request.setEmploymentStatus("EMPLOYED");
        request.setPhone("600123456");
        request.setNonPayment(0);
        request.setCareerTime(LocalDate.of(2020, 1, 1));


        Customer entity = customerMapper.toEntity(request);


        assertNotNull(entity);
        assertEquals(request.getNif(), entity.getNif());
        assertEquals(request.getName(), entity.getName());
        assertEquals(request.getFirstSurname(), entity.getFirstSurname());
        assertEquals(request.getSecondSurname(), entity.getSecondSurname());
        assertEquals(request.getNationality(), entity.getNationality());
        assertEquals(request.getBirthdate(), entity.getBirthdate());
        assertEquals(request.getScoring(), entity.getScoring());
        assertEquals(request.getEmploymentStatus(), entity.getEmploymentStatus());
        assertEquals(request.getPhone(), entity.getPhone());
        assertEquals(request.getNonPayment(), entity.getNonPayment());
        assertEquals(request.getCareerTime(), entity.getCareerTime());
        assertEquals(1, entity.getIsActive());
    }
}
