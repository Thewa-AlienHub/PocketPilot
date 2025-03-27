package org.example.pocketpilot.service.Impl;

import org.bson.types.ObjectId;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.commonlib.ErrorMessage;
import org.example.pocketpilot.dto.requestDTO.FinancialReportRequestDTO;
import org.example.pocketpilot.repository.TransactionRepository;

import org.example.pocketpilot.utils.CustomUserDetails;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FinancialReportServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails customUserDetails;

    @InjectMocks
    private FinacialReportServiceImpl financialReportService;  // Assuming your service class name

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock the SecurityContext to simulate an authenticated user
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        when(context.getAuthentication()).thenReturn(authentication);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("✅ Test Passed: " + testInfo.getDisplayName());
    }



    // 1️⃣ Test: Unauthenticated User → Expect Unauthorized (401)
    @Test
    @DisplayName("Unauthenticated User → Expect Unauthorized (401)")
    void testGetIncomeVsExpense_UnauthenticatedUser() {
        when(authentication.isAuthenticated()).thenReturn(false);  // Mock unauthenticated user

        FinancialReportRequestDTO requestDTO = new FinancialReportRequestDTO();
        ResponseEntity<Object> response = financialReportService.getIncomeVsExpense(requestDTO);

        ErrorMessage errorMessage = (ErrorMessage) response.getBody();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        // Validate the message in the error response
        assertNotNull(errorMessage);
        assertEquals("User is not authenticated", errorMessage.getMessage());  // Ensure ErrorMessage has a 'getMessage' method
    }

    // 2️⃣ Test: Empty Data from getIncomeVsExpense → Expect Not Found (404)
    @Test
    @DisplayName("Empty Data from getIncomeVsExpense → Expect Not Found (404)")
    void testGetIncomeVsExpense_EmptyResult() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserId()).thenReturn(new ObjectId("67d021d962d01a12d9bd45ff"));

        Map<String, BigDecimal> emptySummary = new HashMap<>();
        when(transactionRepository.getIncomeVsExpense(any(), any(), any())).thenReturn(emptySummary);

        FinancialReportRequestDTO requestDTO = new FinancialReportRequestDTO();
        ResponseEntity<Object> response = financialReportService.getIncomeVsExpense(requestDTO);

        ErrorMessage errorMessage = (ErrorMessage) response.getBody();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Validate the message in the error response
        assertNotNull(errorMessage);
        assertEquals("There is No any Transactions done in that period", errorMessage.getMessage());

    }

    // 3️⃣ Test: Valid Data from getIncomeVsExpense → Expect OK (200)
    @Test
    @DisplayName("Valid Data from getIncomeVsExpense → Expect OK (200)")
    void testGetIncomeVsExpense_ValidData() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserId()).thenReturn(new ObjectId("67d021d962d01a12d9bd45ff"));

        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("income", new BigDecimal("5502.250"));
        summary.put("expense", new BigDecimal("3303.000"));

        when(transactionRepository.getIncomeVsExpense(any(), any(), any())).thenReturn(summary);

        FinancialReportRequestDTO requestDTO = new FinancialReportRequestDTO();
        ResponseEntity<Object> response = financialReportService.getIncomeVsExpense(requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("income"));
        assertTrue(response.getBody().toString().contains("expense"));
    }

    // 4️⃣ Test: Exception Handling → Expect Internal Server Error (500)
    @Test
    @DisplayName("Exception Handling → Expect Internal Server Error (500)")
    void testGetIncomeVsExpense_Exception() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserId()).thenReturn(new ObjectId("67d021d962d01a12d9bd45ff"));

        // Simulate an exception being thrown by the repository
        when(transactionRepository.getIncomeVsExpense(any(), any(), any())).thenThrow(new RuntimeException("Database error"));

        FinancialReportRequestDTO requestDTO = new FinancialReportRequestDTO();
        ResponseEntity<Object> response = financialReportService.getIncomeVsExpense(requestDTO);

        ErrorMessage errorMessage = (ErrorMessage) response.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        // Validate the message in the error response
        assertNotNull(errorMessage);
        assertEquals("An error occurred during getIncomeVsExpense", errorMessage.getMessage());
    }
}
