package com.renting.backend.validations;

import com.renting.backend.entities.Request;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

    public void validateDeletion(Request request) {

        if (request.getState() == RequestStatus.APPROVED
                || request.getState() == RequestStatus.APPROVED_WITH_WARRANTIES) {

            throw new BusinessException(
                    "Approved requests cannot be deleted"
            );
        }
    }

    public void validateAnalystResolution(Request request) {

        if (request.getState() != RequestStatus.PENDING_ANALYST) {

            throw new BusinessException(
                    "Only pending requests can be resolved"
            );
        }
    }
}