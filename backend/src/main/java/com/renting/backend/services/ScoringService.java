package com.renting.backend.services;

import com.renting.backend.dtos.response.ScoringResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Request;

public interface ScoringService
{
    ScoringResponse evaluate(Customer customer, Request request);
}
