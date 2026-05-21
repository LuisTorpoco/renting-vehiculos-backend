package com.renting.backend.repositories;

import com.renting.backend.entities.Customer;
import com.renting.backend.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("""
        SELECT c
        FROM Customer c
        WHERE c.id = :id
        AND c.isActive = 1
    """)
    Optional<Customer> findActiveById(@Param("id") Long id);

    @Query("""
        SELECT c
        FROM Customer c
        WHERE c.isActive = 1
    """)
    Page<Customer> findAllActive(Pageable pageable);

    @Query("""
    SELECT COUNT(r) > 0
    FROM Request r
    WHERE r.customer.id = :id
    AND r.state = :status
""")
    boolean hasPendingRequests(
            @Param("id") Long id,
            @Param("status") RequestStatus status
    );


    long countByIsActive(Integer isActive);
}