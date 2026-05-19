package com.renting.backend.repositories;


import com.renting.backend.entities.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long>
{
    List<Income> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
