package com.renting.backend.dtos.request;

import com.renting.backend.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResolveRequestDTO {

    @NotNull
    private RequestStatus status;
}
