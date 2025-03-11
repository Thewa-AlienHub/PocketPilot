package org.example.pocketpilot.service;

import org.example.pocketpilot.dto.RequestDTO.FinancialReportRequestDTO;
import org.example.pocketpilot.entities.TransactionEntity;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface FinancialReportService {

    ResponseEntity< Object> getSpendingTrends(FinancialReportRequestDTO dto);

    ResponseEntity<Object> getIncomeVsExpense(FinancialReportRequestDTO dto);

    ResponseEntity<Object> getFilteredTransactions(FinancialReportRequestDTO dto);
}
