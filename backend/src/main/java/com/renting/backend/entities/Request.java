package com.renting.backend.entities;

<<<<<<< HEAD
public class Request {
}
=======
import com.renting.backend.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private String isActive;
}
>>>>>>> b36f17a (terminado request y requestcontroller)
