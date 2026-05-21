package com.renting.backend.services.business;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.request.PriceCalculationRequest;
import com.renting.backend.dtos.request.RequestVehicleDTO;
import com.renting.backend.dtos.response.PriceCalculationResponse;
import com.renting.backend.dtos.response.RuleEvaluationResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Income;
import com.renting.backend.entities.Request;
import com.renting.backend.entities.RequestDetail;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.exception.ResourceNotFoundException;
import com.renting.backend.repositories.CustomerRepository;
import com.renting.backend.repositories.IncomeRepository;
import com.renting.backend.repositories.RequestDetailRepository;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.PriceService;
import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.engine.ApprovalRulesEngine;
import com.renting.backend.services.scoring.engine.DenialRulesEngine;
import com.renting.backend.services.scoring.utils.FinancialAverageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestBusinessService {

    private final RequestRepository requestRepository;
    private final RequestDetailRepository detailRepository;
    private final CustomerRepository customerRepository;
    private final IncomeRepository incomeRepository;

    private final PriceService priceService;
    private final FinancialAverageService financialAverageService;

    private final DenialRulesEngine denialRulesEngine;
    private final ApprovalRulesEngine approvalRulesEngine;

    @Transactional
    public Request create(CreateRequestDTO dto) {

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + dto.getCustomerId()
                ));

        LocalDateTime now = LocalDateTime.now();

        Request requestToEvaluate = Request.builder()
                .customer(customer)
                .createdAt(now)
                .periodInMonths(dto.getPeriodInMonths())
                .isActive(1)
                .state(RequestStatus.PENDING_ANALYST)
                .build();

        List<Income> incomes = incomeRepository
                .findByCustomerIdOrderByCreatedAtDesc(customer.getId());

        boolean deniedLastTwoYears = requestRepository
                .countByCustomerIdAndStateAndCreatedAtGreaterThanEqual(
                        customer.getId(),
                        RequestStatus.DENIED,
                        now.minusYears(2)
                ) > 0;

        boolean approvedWithWarranties = requestRepository
                .countByCustomerIdAndStateAndCreatedAtGreaterThanEqual(
                        customer.getId(),
                        RequestStatus.APPROVED_WITH_WARRANTIES,
                        now.minusYears(2)
                ) > 0;

        BigDecimal monthlyFee = BigDecimal.ZERO;
        BigDecimal vehicleInvestment = BigDecimal.ZERO;

        for (RequestVehicleDTO vehicle : dto.getVehicles()) {
            PriceCalculationResponse price = priceService.calculatePrice(
                    PriceCalculationRequest.builder()
                            .vehicleId(vehicle.getVehicleId())
                            .extraIds(vehicle.getExtraIds())
                            .months(dto.getPeriodInMonths())
                            .build()
            );

            monthlyFee = monthlyFee.add(price.getFinalMonthlyFee());
            vehicleInvestment = vehicleInvestment.add(price.getFinalInvestment());
        }

        ScoringContext context = ScoringContext.builder()
                .customer(customer)
                .request(requestToEvaluate)
                .incomes(incomes)
                .averagePreTaxes(financialAverageService.calculateAveragePreTaxes(incomes))
                .averagePostTaxes(financialAverageService.calculateAveragePostTaxes(incomes))
                .deniedLastTwoYears(deniedLastTwoYears)
                .approvedWithWarranties(approvedWithWarranties)
                .monthlyFee(monthlyFee)
                .vehicleInvestment(vehicleInvestment)
                .build();

        RequestStatus finalStatus = RequestStatus.PENDING_ANALYST;

        List<RuleEvaluationResponse> denialEvaluations = denialRulesEngine.evaluate(context);

        boolean shouldBeDenied = denialEvaluations.stream()
                .anyMatch(RuleEvaluationResponse::getPassed);

        if (shouldBeDenied) {
            finalStatus = RequestStatus.DENIED;
        } else {
            List<RuleEvaluationResponse> approvalEvaluations = approvalRulesEngine.evaluate(context);

            boolean meetsAllApprovals = !approvalEvaluations.isEmpty()
                    && approvalEvaluations.stream()
                    .allMatch(RuleEvaluationResponse::getPassed);

            if (meetsAllApprovals) {
                finalStatus = RequestStatus.APPROVED;
            }
        }

        Request request = Request.builder()
                .customer(customer)
                .createdAt(now)
                .state(finalStatus)
                .resolutionDate(finalStatus == RequestStatus.PENDING_ANALYST ? null : now)
                .periodInMonths(dto.getPeriodInMonths())
                .isActive(1)
                .build();

        Request savedRequest = requestRepository.save(request);

        saveRequestDetails(savedRequest, dto);

        return savedRequest;
    }

    private void saveRequestDetails(Request request, CreateRequestDTO dto) {
        for (RequestVehicleDTO vehicle : dto.getVehicles()) {

            List<Long> extraIds = vehicle.getExtraIds();

            if (extraIds != null && !extraIds.isEmpty()) {
                for (Long extraId : extraIds) {
                    RequestDetail detail = RequestDetail.builder()
                            .requestId(request.getId())
                            .vehicleId(vehicle.getVehicleId())
                            .extraId(extraId)
                            .build();

                    detailRepository.save(detail);
                }
            } else {
                RequestDetail detail = RequestDetail.builder()
                        .requestId(request.getId())
                        .vehicleId(vehicle.getVehicleId())
                        .extraId(null)
                        .build();

                detailRepository.save(detail);
            }
        }
    }
}