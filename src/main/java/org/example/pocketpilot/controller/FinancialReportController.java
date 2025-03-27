package org.example.pocketpilot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pocketpilot.dto.requestDTO.FinancialReportRequestDTO;
import org.example.pocketpilot.service.FinancialReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/financial-reports")
@RequiredArgsConstructor
@Slf4j
public class FinancialReportController {

    private final FinancialReportService financialReportService;


    @GetMapping("/spending-trends")
    public ResponseEntity< Object> getSpendingTrends(
           @Valid @RequestBody FinancialReportRequestDTO requestDTO) {
        return financialReportService.getSpendingTrends(requestDTO);
    }

    @GetMapping("/income-vs-expense")
    public ResponseEntity<Object> getIncomeVsExpense(
            @Valid @ModelAttribute FinancialReportRequestDTO requestDTO) {
        return financialReportService.getIncomeVsExpense(requestDTO);
    }

    @GetMapping("/filtered-transactions")
    public ResponseEntity<Object> getFilteredTransactions(
            @ModelAttribute FinancialReportRequestDTO requestDTO) {
        return financialReportService.getFilteredTransactions(requestDTO);
    }

}
