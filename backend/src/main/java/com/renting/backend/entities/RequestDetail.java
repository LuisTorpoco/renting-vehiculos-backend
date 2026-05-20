package com.renting.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "REQUEST_DETAIL")
@IdClass(RequestDetailId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDetail {

    @Id
    @Column(name = "ID_REQUEST")
    private Long requestId;

    @Id
    @Column(name = "ID_VEHICLE")
    private Long vehicleId;

    @Id
    @Column(name = "ID_EXTRA")
    private Long extraId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REQUEST", insertable = false, updatable = false)
    private Request request;
}