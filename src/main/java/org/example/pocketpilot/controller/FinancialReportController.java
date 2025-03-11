package org.example.pocketpilot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pocketpilot.dto.RequestDTO.FinancialReportRequestDTO;
import org.example.pocketpilot.service.FinancialReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/financial-reports")
@PreAuthorize("hasAuthority(UserRole.ADMIN.getRoleName() or UserRole.PREMIUM_USER.getRoleName() or UserRole.REGULAR_USER.getRoleName())")
@RequiredArgsConstructor
@Slf4j
public class FinancialReportController {

    private final FinancialReportService financialReportService;


    @GetMapping("/spending-trends")
    public ResponseEntity< Object> getSpendingTrends(
            @ModelAttribute FinancialReportRequestDTO requestDTO) {
        return financialReportService.getSpendingTrends(requestDTO);
    }

    @GetMapping("/income-vs-expense")
    public ResponseEntity<Object> getIncomeVsExpense(
            @ModelAttribute FinancialReportRequestDTO requestDTO) {
        return financialReportService.getIncomeVsExpense(requestDTO);
    }

    @GetMapping("/filtered-transactions")
    public ResponseEntity<Object> getFilteredTransactions(
            @ModelAttribute FinancialReportRequestDTO requestDTO) {
        return financialReportService.getFilteredTransactions(requestDTO);
    }

}
