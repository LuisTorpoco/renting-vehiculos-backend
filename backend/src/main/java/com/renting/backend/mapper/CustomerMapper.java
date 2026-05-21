package com.renting.backend.mapper;

import com.renting.backend.dtos.request.CustomerCreateRequest;
import com.renting.backend.dtos.response.CustomerResponse;
import com.renting.backend.entities.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerCreateRequest r) {

        return Customer.builder()
                .nif(r.getNif())
                .name(r.getName())
                .firstSurname(r.getFirstSurname())
                .secondSurname(r.getSecondSurname())
                .nationality(r.getNationality())
                .birthdate(r.getBirthdate())
                .scoring(r.getScoring())
                .employmentStatus(r.getEmploymentStatus())
                .phone(r.getPhone())
                .nonPayment(r.getNonPayment() != null ? r.getNonPayment() : 0) // default 0 si es null
                .careerTime(r.getCareerTime())
                .isActive(1)
                .build();
    }


    public CustomerResponse toResponse(Customer c) {

        return CustomerResponse.builder()
                .id(c.getId())
                .nif(c.getNif())
                .name(c.getName())
                .firstSurname(c.getFirstSurname())
                .secondSurname(c.getSecondSurname())
                .nationality(c.getNationality())
                .birthdate(c.getBirthdate())
                .scoring(c.getScoring())
                .employmentStatus(c.getEmploymentStatus())
                .phone(c.getPhone())
                .nonPayment(c.getNonPayment())
                .careerTime(c.getCareerTime())
                .build();
    }
}