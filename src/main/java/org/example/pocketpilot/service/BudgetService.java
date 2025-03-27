package org.example.pocketpilot.service;

import org.bson.types.ObjectId;
import org.example.pocketpilot.dto.BudgetFilterDTO;
import org.example.pocketpilot.dto.requestDTO.BudgetRequestDTO;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.YearMonth;

public interface BudgetService {
    ResponseEntity<Object> setBudgetPlan(BudgetRequestDTO dto);

    ResponseEntity<Object> getBudgetPlanFilter(BudgetFilterDTO dto);

    boolean updateBudgetPlan(int Category, ObjectId userId , BigDecimal transactionAmount,String userEmail);

    BigDecimal calculateRecommendedBudget(ObjectId userId, String category, YearMonth yearMonth);
}
