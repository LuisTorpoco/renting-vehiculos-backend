
package com.renting.backend.repositories;

import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Extra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ExtraRepository extends JpaRepository<Extra, Long> {

}