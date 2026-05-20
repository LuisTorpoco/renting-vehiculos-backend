package com.renting.backend.mapper;

import com.renting.backend.dtos.request.CustomerCreateRequest;
import com.renting.backend.dtos.response.CustomerResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.enums.EmploymentStatus;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerCreateRequest r) {
        if (r == null) {
            return null;
        }

        Customer c = new Customer();
        c.setBirthdate(r.getBirthdate());
        c.setScoring(r.getScoring());

        if (r.getEmploymentStatus() != null) {
            c.setEmploymentStatus(r.getEmploymentStatus().name());
        }

        c.setPhone(r.getPhone());

        if (r.getNonPayment() != null) {
            c.setNonPayment(r.getNonPayment() ? 1 : 0);
        } else {
            c.setNonPayment(0);
        }

        c.setCareerTime(r.getCareerTime());

        c.setIsActive(1);

        // Mapeo del resto de campos obligatorios
        c.setNif(r.getNif());
        c.setName(r.getName());
        c.setFirstSurname(r.getFirstSurname());
        c.setSecondSurname(r.getSecondSurname());
        c.setNationality(r.getNationality());

        return c;
    }

    public CustomerResponse toResponse(Customer c) {
        if (c == null) {
            return null;
        }

        EmploymentStatus empStatus = null;
        if (c.getEmploymentStatus() != null) {
            try {
                empStatus = EmploymentStatus.valueOf(c.getEmploymentStatus());
            } catch (IllegalArgumentException e) {
                empStatus = null;
            }
        }

        Boolean nonPaymentBoolean = (c.getNonPayment() != null && c.getNonPayment() == 1);
        
        return CustomerResponse.builder()
                .id(c.getId())
                .nif(c.getNif())
                .name(c.getName())
                .firstSurname(c.getFirstSurname())
                .secondSurname(c.getSecondSurname())
                .nationality(c.getNationality())
                .birthdate(c.getBirthdate())
                .scoring(c.getScoring())
                .employmentStatus(empStatus)
                .phone(c.getPhone())
                .nonPayment(nonPaymentBoolean)
                .careerTime(c.getCareerTime())
                .build();
    }
}