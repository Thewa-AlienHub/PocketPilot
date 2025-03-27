package org.example.pocketpilot.service;


import org.bson.types.ObjectId;
import org.example.pocketpilot.dto.requestDTO.FinancialGoalsRequestDTO;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface FinancialGoalService {

    ResponseEntity<Object> addFinancialGoal(FinancialGoalsRequestDTO dto);

    ResponseEntity<Object> getGoalProgress(ObjectId goalId);
    ResponseEntity<Object> autoAllocateSavings(ObjectId userId, BigDecimal incomeAmount);
}
