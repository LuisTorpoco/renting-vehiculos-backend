package com.renting.backend.dtos.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleEvaluationResponse {

    private String ruleName;

    private Boolean passed;
}
