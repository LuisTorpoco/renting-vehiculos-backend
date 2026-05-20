package com.renting.backend.repositories;

import com.renting.backend.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.id = :id AND c.isActive = 1")
    Optional<Customer> findActiveById(@Param("id") Long id);

    Page<Customer> findByIsActive(Integer isActive, Pageable pageable);

    @Query("SELECT COUNT(r) > 0 FROM Request r WHERE r.customer.id = :id AND r.isActive = 1")
    boolean hasActiveRequests(@Param("id") Long id);
}