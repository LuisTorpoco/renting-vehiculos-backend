package com.renting.backend.repositories;

import com.renting.backend.entities.Extra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

 // Repositorio para la gestión de datos de la entidad Extra en Oracle.
@Repository
public interface ExtraRepository extends JpaRepository<Extra, Long> {
    //  solo los extras que estén activos evita traer extras que no se pueden alquilar.
    List<Extra> findByIsActive(Integer isActive);
}