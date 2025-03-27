package org.example.pocketpilot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.pocketpilot.dto.requestDTO.FinancialReportRequestDTO;
import org.example.pocketpilot.service.FinancialReportService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FinancialReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FinancialReportService financialReportService;

    @InjectMocks
    private FinancialReportController financialReportController;

    private final ObjectMapper objectMapper = new ObjectMapper(); // Converts Java objects to JSON

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(financialReportController).build();

        // Register the Java Time module for LocalDateTime support
        objectMapper.registerModule(new JavaTimeModule());
    }
    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("✅ Test Passed: " + testInfo.getDisplayName());
    }



    @Test
    @DisplayName("✅ Get SpendingTrends_Valid request")
    void testGetSpendingTrends_ValidRequest() throws Exception {
        // Create a sample request DTO
        FinancialReportRequestDTO requestDTO = new FinancialReportRequestDTO();
        requestDTO.setStartDate(LocalDateTime.of(2024, 3, 1, 0, 0));
        requestDTO.setEndDate(LocalDateTime.of(2024, 3, 20, 0, 0));

        // Sample JSON response (simulating the service response)
        List<Map<String, Object>> mockResponse = List.of(
                Map.of("_id", Map.of("month", 3, "day", 11),
                        "totalSpent", 3303.000,
                        "month", 3,
                        "day", 11)
        );

        // Mock the service method call
        when(financialReportService.getSpendingTrends(any()))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // Perform the GET request with JSON body (even though it's not ideal)
        mockMvc.perform(get("/api/financial-reports/spending-trends")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())  // Expect HTTP 200 OK
                .andExpect(jsonPath("$[0]._id.month").value(3))  // Validate JSON response
                .andExpect(jsonPath("$[0]._id.day").value(11))
                .andExpect(jsonPath("$[0].totalSpent").value(3303.000));

        // Verify the service method was called exactly once
        verify(financialReportService, times(1)).getSpendingTrends(any());
    }

    @Test
    @DisplayName(" Get SpendingTrends_InValid request")
    void testGetSpendingTrends_InvalidRequest() throws Exception {
        // Create an invalid request DTO (startDate in the future)
        FinancialReportRequestDTO requestDTO = new FinancialReportRequestDTO();
        requestDTO.setStartDate(LocalDateTime.of(2050, 3, 1, 0, 0)); // Future date
        requestDTO.setEndDate(LocalDateTime.of(2050, 3, 20, 0, 0));

        // Perform the request and expect validation error
        mockMvc.perform(get("/api/financial-reports/spending-trends")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Get SpendingTrends_Missing Start date ")
    void testGetSpendingTrends_MissingStartDate() throws Exception {
        String requestBody = """
            {
                "endDate": "2024-02-28T23:59:59",
                "categories": ["Food"]
            }
        """;

        mockMvc.perform(get("/api/financial-reports/spending-trends")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(financialReportService, times(0)).getSpendingTrends(any());
    }


    @Test
    @DisplayName("Get Income Vs Expenses_ Valid Request ")
    void testGetIncomeVsExpense_ValidRequest() throws Exception {
        String requestParams = "?startDate=2024-02-01T00:00:00&endDate=2024-02-28T23:59:59";

        when(financialReportService.getIncomeVsExpense(any()))
                .thenReturn(ResponseEntity.ok("{ \"income\": 5502.250, \"expense\": 3303.000 }"));

        mockMvc.perform(get("/api/financial-reports/income-vs-expense" + requestParams)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(financialReportService, times(1)).getIncomeVsExpense(any());
    }


    @Test
    @DisplayName("Get Income Vs Expenses_ Missing Start Date ")
    void testGetIncomeVsExpense_MissingStartDate() throws Exception {
        String requestParams = "?endDate=2024-02-28T23:59:59";

        mockMvc.perform(get("/api/financial-reports/income-vs-expense" + requestParams)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(financialReportService, times(0)).getIncomeVsExpense(any());
    }

    @Test
    @DisplayName("Get Income Vs Expenses_StartDate In Future  ")
    void testGetIncomeVsExpense_FutureStartDate() throws Exception {
        String requestParams = "?startDate=2050-01-01T00:00:00&endDate=2024-02-28T23:59:59";

        mockMvc.perform(get("/api/financial-reports/income-vs-expense" + requestParams)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(financialReportService, times(0)).getIncomeVsExpense(any());
    }

    @Test
    @DisplayName("Get Income Vs Expenses_Missing End Date ")
    void testGetIncomeVsExpense_MissingEndDate() throws Exception {
        String requestParams = "?startDate=2024-02-01T00:00:00";

        mockMvc.perform(get("/api/financial-reports/income-vs-expense" + requestParams)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(financialReportService, times(0)).getIncomeVsExpense(any());
    }

}
