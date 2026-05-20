package com.renting.backend.repositories;

import com.renting.backend.entities.Customer;
<<<<<<< HEAD
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    Optional<Customer> findByNif(
            String nif
    );
=======
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.id = :id AND c.active = true")
    Optional<Customer> findActiveById(@Param("id") Long id);

    @Query("""
        SELECT COUNT(r) > 0
        FROM Request r
        WHERE r.customer.id = :id AND r.active = true
    """)
    boolean hasActiveRequests(@Param("id") Long id);
>>>>>>> 7c38f41 (Clases)
}