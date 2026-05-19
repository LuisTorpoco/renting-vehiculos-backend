package com.renting.backend.entities;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RequestDetailId implements Serializable {

    private Long requestId;

    private Long vehicleId;
}
