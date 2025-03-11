package org.example.pocketpilot.service;


import org.bson.types.ObjectId;
import org.example.pocketpilot.dto.RequestDTO.FinancialGoalsRequestDTO;
import org.example.pocketpilot.dto.RequestDTO.TransactionRequestDTO;
import org.example.pocketpilot.dto.TransactionFilterDTO;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface FinancialGoalService {

    ResponseEntity<Object> addFinancialGoal(FinancialGoalsRequestDTO dto);

    ResponseEntity<Object> getGoalProgress(ObjectId goalId);
    ResponseEntity<Object> autoAllocateSavings(ObjectId userId, BigDecimal incomeAmount);
}
