package com.renting.backend.services;

import com.renting.backend.dtos.response.ScoringResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Request;

public interface Scoring
{
    ScoringResponse evaluate(Customer customer, Request request);
}
