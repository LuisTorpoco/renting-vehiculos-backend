package com.renting.backend.mapper;

import com.renting.backend.dtos.request.*;
import com.renting.backend.dtos.response.*;
import com.renting.backend.entities.*;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerCreateRequest r) {
        Customer c = new Customer();
        c.setNif(r.getNif());
        c.setName(r.getName());
        c.setFirstSurname(r.getFirstSurname());
        c.setSecondSurname(r.getSecondSurname());
        c.setNationality(r.getNationality());
        c.setBirthdate(r.getBirthdate());
        c.setScoring(r.getScoring());
        c.setEmploymentStatus(r.getEmploymentStatus());
        c.setPhone(r.getPhone());
        c.setNonPayment(r.getNonPayment());
        c.setCareerTime(r.getCareerTime());
        c.setActive(true);
        return c;
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