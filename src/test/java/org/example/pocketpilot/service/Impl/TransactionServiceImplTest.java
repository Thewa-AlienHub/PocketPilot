package org.example.pocketpilot.service.Impl;

import org.bson.types.ObjectId;
import org.example.pocketpilot.commonlib.Response;
import org.example.pocketpilot.components.NotificationQueue;
import org.example.pocketpilot.dto.requestDTO.TransactionRequestDTO;
import org.example.pocketpilot.entities.TransactionEntity;
import org.example.pocketpilot.repository.TransactionRepository;
import org.example.pocketpilot.repository.UserRepository;
import org.example.pocketpilot.service.BudgetService;
import org.example.pocketpilot.service.FinancialGoalService;
import org.example.pocketpilot.utils.CurencyConversionService;
import org.example.pocketpilot.utils.CustomUserDetails;
import org.example.pocketpilot.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CurencyConversionService curencyConversionService;
    @Mock
    private FinancialGoalService financialGoalService;
    @Mock
    private BudgetService budgetService;
    @Mock
    private NotificationQueue notificationQueue;

    @InjectMocks
    private TransactionServiceImpl transactionServiceImpl;

    @Mock
    private CustomUserDetails customUserDetails;

    private ObjectId userId;
    private ObjectId transactionId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = new ObjectId("67d021d962d01a12d9bd45ff");
        transactionId = new ObjectId();

        // Mock Authentication and SecurityContext
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(customUserDetails.getUserId()).thenReturn(userId);
        System.out.println("userId"+userId);

        // Set the mocked Authentication into the SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void addTransaction_ShouldReturnSuccess_WhenTransactionIsAdded() {
        // Arrange
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setAmount(new BigDecimal("1000"));
        transactionRequestDTO.setType("expense");
        transactionRequestDTO.setCategory(1); // Assuming 1 corresponds to a category
        transactionRequestDTO.setTransactionDateTime(LocalDateTime.now());
        transactionRequestDTO.setRecurring(false);


        when(customUserDetails.getUserId()).thenReturn(userId);
        when(userRepository.getCurrencyCodeById(userId)).thenReturn("LKR");
        when(curencyConversionService.convertCurrency(any(), eq("LKR"), eq("LKR"))).thenReturn(new BigDecimal("1000"));
        when(transactionRepository.save(any())).thenReturn(true);

        // Act
        ResponseEntity<Object> response = transactionServiceImpl.addTransaction(transactionRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Response);  // Assuming response is of type Response
    }

    @Test
    void addTransaction_ShouldReturnUnauthorized_WhenUserNotAuthenticated() {
        // Arrange
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setAmount(new BigDecimal("1000"));

        // Simulate user not authenticated
        when(customUserDetails.getUserId()).thenReturn(null);  // User not authenticated

        // Act
        ResponseEntity<Object> response = transactionServiceImpl.addTransaction(transactionRequestDTO);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void updateTransaction_ShouldReturnSuccess_WhenTransactionIsUpdated() {
        // Arrange
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setAmount(new BigDecimal("2000"));
        transactionRequestDTO.setType("income");
        transactionRequestDTO.setCategory(2);  // Assuming 2 corresponds to a category
        transactionRequestDTO.setTransactionDateTime(LocalDateTime.now());
        transactionRequestDTO.setRecurring(false);


        when(transactionRepository.findById(any())).thenReturn(Optional.of(new TransactionEntity()));
        when(userRepository.getCurrencyCodeById(any())).thenReturn("LKR");
        when(curencyConversionService.convertCurrency(any(), eq("LKR"), eq("LKR"))).thenReturn(new BigDecimal("2000"));

        // Act
        ResponseEntity<Object> response = transactionServiceImpl.updateTransactions(transactionId, transactionRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteTransaction_ShouldReturnSuccess_WhenTransactionIsDeleted() {
        // Arrange

        when(transactionRepository.findById(any())).thenReturn(Optional.of(new TransactionEntity()));

        // Act
        ResponseEntity<Object> response = transactionServiceImpl.deleteTransactions(transactionId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

