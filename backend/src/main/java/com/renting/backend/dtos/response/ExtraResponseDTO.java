package com.renting.backend.dtos.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtraResponseDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal percentage;
    private String category;
}
