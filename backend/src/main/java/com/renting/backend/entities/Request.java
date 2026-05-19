package com.renting.backend.entities;

import com.renting.backend.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private RequestStatus state;

    @Column(name = "resolution_date")
    private LocalDateTime resolutionDate;

    @Column(name = "period_in_months", nullable = false)
    private Integer periodInMonths;

    @Column(name = "is_active", nullable = false)
    private Integer isActive;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private List<RequestDetail> details;
}
