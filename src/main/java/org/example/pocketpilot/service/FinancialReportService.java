package org.example.pocketpilot.service;

import org.example.pocketpilot.dto.requestDTO.FinancialReportRequestDTO;
import org.springframework.http.ResponseEntity;

public interface FinancialReportService {

    ResponseEntity< Object> getSpendingTrends(FinancialReportRequestDTO dto);

    ResponseEntity<Object> getIncomeVsExpense(FinancialReportRequestDTO dto);

    ResponseEntity<Object> getFilteredTransactions(FinancialReportRequestDTO dto);
}
