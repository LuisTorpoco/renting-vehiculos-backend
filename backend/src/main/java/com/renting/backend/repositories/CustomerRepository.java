package com.renting.backend.repositories;

import com.renting.backend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.id = :id AND c.isActive = 1")
    Optional<Customer> findActiveById(@Param("id") Long id);

    @Query("SELECT COUNT(r) > 0 FROM Request r WHERE r.customer.id = :id AND r.isActive = 1")
    boolean hasActiveRequests(@Param("id") Long id);
}