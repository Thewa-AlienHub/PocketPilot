package org.example.pocketpilot.controller;

import org.example.pocketpilot.dto.RequestDTO.FinancialReportRequestDTO;
import org.example.pocketpilot.service.FinancialReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class FinancialReportControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private FinancialReportService financialReportService;

    @InjectMocks
    private FinancialReportController financialReportController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(financialReportController).build();
    }

    @Test
    public void testGetSpendingTrends() throws Exception {
        // Prepare mock response
        FinancialReportRequestDTO requestDTO = new FinancialReportRequestDTO();
        // Set some test data on the requestDTO if needed

        // Mock the service call
        when(financialReportService.getSpendingTrends(requestDTO))
                .thenReturn(ResponseEntity.ok("Mocked Spending Trends Data"));

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/financial-reports/spending-trends")
                        .param("someParam", "value"))  // Pass any parameters you need
                .andExpect(status().isOk());  // Check that it returns HTTP 200 (OK)
    }

    @Test
    public void testGetIncomeVsExpense() throws Exception {
        // Mock the service call
        FinancialReportRequestDTO requestDTO = new FinancialReportRequestDTO();
        when(financialReportService.getIncomeVsExpense(requestDTO))
                .thenReturn(ResponseEntity.ok("Mocked Income vs Expense Data"));

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/financial-reports/income-vs-expense")
                        .param("someParam", "value"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFilteredTransactions() throws Exception {
        // Mock the service call
        FinancialReportRequestDTO requestDTO = new FinancialReportRequestDTO();
        when(financialReportService.getFilteredTransactions(requestDTO))
                .thenReturn(ResponseEntity.ok("Mocked Filtered Transactions"));

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/financial-reports/filtered-transactions")
                        .param("someParam", "value"))
                .andExpect(status().isOk());
    }
}
