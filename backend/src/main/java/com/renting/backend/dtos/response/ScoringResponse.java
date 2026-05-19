package com.renting.backend.dtos.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoringResponse {

    private Boolean automaticallyApproved;

    private Boolean automaticallyDenied;

    private String reason;

    private List<RuleEvaluationResponse> rules;
}