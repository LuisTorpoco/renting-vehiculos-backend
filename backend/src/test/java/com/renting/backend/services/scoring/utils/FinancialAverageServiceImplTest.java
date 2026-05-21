package com.renting.backend.services.scoring.utils;

import com.renting.backend.entities.Income;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinancialAverageServiceImplTest {
    private final FinancialAverageServiceImpl service = new FinancialAverageServiceImpl();

    @Test
    void promedioPreImpuestosConIngresosValidos() {
        // Prueba: Promedio pre-impuestos con ingresos dentro de los últimos 3 años
        Income i1 = new Income();
        i1.setPreTaxes(BigDecimal.valueOf(1000));
        i1.setCreatedAt(LocalDateTime.now().minusMonths(6));
        Income i2 = new Income();
        i2.setPreTaxes(BigDecimal.valueOf(2000));
        i2.setCreatedAt(LocalDateTime.now().minusMonths(12));
        List<Income> incomes = Arrays.asList(i1, i2);
        BigDecimal promedio = service.calculateAveragePreTaxes(incomes);
        assertEquals(BigDecimal.valueOf(1500.00).setScale(2), promedio);
    }

    @Test
    void promedioPostImpuestosConIngresosValidos() {
        // Prueba: Promedio post-impuestos con ingresos dentro de los últimos 3 años
        Income i1 = new Income();
        i1.setPostTaxes(BigDecimal.valueOf(800));
        i1.setCreatedAt(LocalDateTime.now().minusMonths(6));
        Income i2 = new Income();
        i2.setPostTaxes(BigDecimal.valueOf(1200));
        i2.setCreatedAt(LocalDateTime.now().minusMonths(12));
        List<Income> incomes = Arrays.asList(i1, i2);
        BigDecimal promedio = service.calculateAveragePostTaxes(incomes);
        assertEquals(BigDecimal.valueOf(1000.00).setScale(2), promedio);
    }

    @Test
    void promedioConListaVacia() {
        // Prueba: Lista vacía debe retornar 0
        BigDecimal promedio = service.calculateAveragePreTaxes(Collections.emptyList());
        assertEquals(BigDecimal.ZERO.setScale(2), promedio);
    }

    @Test
    void promedioConIngresosFueraDeRango() {
        // Prueba: Todos los ingresos fuera de los últimos 3 años
        Income i1 = new Income();
        i1.setPreTaxes(BigDecimal.valueOf(1000));
        i1.setCreatedAt(LocalDateTime.now().minusYears(4));
        List<Income> incomes = Collections.singletonList(i1);
        BigDecimal promedio = service.calculateAveragePreTaxes(incomes);
        assertEquals(BigDecimal.ZERO.setScale(2), promedio);
    }

    @Test
    void promedioConUltimoIngresoRecientePeroFueraDe3Anios() {
        // Prueba: Último ingreso dentro de los últimos 2 años pero fuera de los 3 años
        Income i1 = new Income();
        i1.setPreTaxes(BigDecimal.valueOf(500));
        i1.setCreatedAt(LocalDateTime.now().minusYears(1));
        List<Income> incomes = Collections.singletonList(i1);
        BigDecimal promedio = service.calculateAveragePreTaxes(incomes);
        assertEquals(BigDecimal.valueOf(500.00).setScale(2), promedio);
    }
}

