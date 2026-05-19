package com.renting.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "request_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDetail {
    @Id
    @Column(name = "id_request")
    private Long requestId;

    @Id
    @Column(name = "id_vehicle")
    private Long vehicleId;

    @Column(name = "id_extra")
    private Long extraId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_request",
            insertable = false,
            updatable = false
    )
    private Request request;
}
