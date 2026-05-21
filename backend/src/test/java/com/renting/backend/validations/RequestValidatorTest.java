package com.renting.backend.validations;

import com.renting.backend.entities.Request;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestValidatorTest {
    private final RequestValidator validator = new RequestValidator();

    @Test
    void noPermitirEliminarSiAprobado() {
        // Prueba: No permite eliminar si el estado es APPROVED
        Request req = new Request();
        req.setState(RequestStatus.APPROVED);
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validateDeletion(req));
        assertEquals("Approved requests cannot be deleted", ex.getMessage());
    }

    @Test
    void noPermitirEliminarSiAprobadoConGarantias() {
        // Prueba: No permite eliminar si el estado es APPROVED_WITH_WARRANTIES
        Request req = new Request();
        req.setState(RequestStatus.APPROVED_WITH_WARRANTIES);
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validateDeletion(req));
        assertEquals("Approved requests cannot be deleted", ex.getMessage());
    }

    @Test
    void permitirEliminarSiNoAprobado() {
        // Prueba: Permite eliminar si el estado es DENIED
        Request req = new Request();
        req.setState(RequestStatus.DENIED);
        assertDoesNotThrow(() -> validator.validateDeletion(req));
    }

    @Test
    void noPermitirResolverSiNoPendiente() {
        // Prueba: No permite resolver si el estado no es PENDING_ANALYST
        Request req = new Request();
        req.setState(RequestStatus.APPROVED);
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validateAnalystResolution(req));
        assertEquals("Only pending requests can be resolved", ex.getMessage());
    }

    @Test
    void permitirResolverSiPendiente() {
        // Prueba: Permite resolver si el estado es PENDING_ANALYST
        Request req = new Request();
        req.setState(RequestStatus.PENDING_ANALYST);
        assertDoesNotThrow(() -> validator.validateAnalystResolution(req));
    }
}

