package org.example.pocketpilot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.example.pocketpilot.config.SecurityConfig;
import org.example.pocketpilot.dto.RequestDTO.TransactionRequestDTO;
import org.example.pocketpilot.dto.TransactionFilterDTO;
import org.example.pocketpilot.service.TransactionService;
import org.example.pocketpilot.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionRequestDTO transactionRequestDTO;

    @BeforeEach
    void setUp() {
        transactionRequestDTO = TransactionRequestDTO.builder()
                .type("EXPENSE")
                .amount(BigDecimal.valueOf(100))
                .category(2)
                .tags(Collections.singletonList("food"))
                .recurring(false)
                .transactionDateTime(LocalDateTime.now())
                .recurrencePattern(null)
                .build();
    }

    @Test
    void addTransaction_ShouldReturnOk() throws Exception {
        Mockito.when(transactionService.addTransaction(any(TransactionRequestDTO.class)))
                .thenReturn(ResponseEntity.ok("Transaction added successfully"));

        mockMvc.perform(post("/api/transactions/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("Transaction added successfully"));
    }

    @Test
    void getTransactionById_ShouldReturnOk() throws Exception {
        ObjectId id = new ObjectId();

        Mockito.when(transactionService.getTransactionById(eq(id)))
                .thenReturn(ResponseEntity.ok("Transaction fetched successfully"));

        mockMvc.perform(get("/api/transactions/get/{id}", id.toHexString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("Transaction fetched successfully"));
    }

    @Test
    void updateTransaction_ShouldReturnOk() throws Exception {
        ObjectId id = new ObjectId();

        Mockito.when(transactionService.updateTransactions(eq(id), any(TransactionRequestDTO.class)))
                .thenReturn(ResponseEntity.ok("Transaction updated successfully"));

        mockMvc.perform(put("/api/transactions/update/{id}", id.toHexString())
                        .param("id", id.toHexString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("Transaction updated successfully"));
    }

    @Test
    void deleteTransaction_ShouldReturnOk() throws Exception {
        ObjectId id = new ObjectId();

        Mockito.when(transactionService.deleteTransactions(eq(id)))
                .thenReturn(ResponseEntity.ok("Transaction deleted successfully"));

        mockMvc.perform(delete("/api/transactions/delete/{id}", id.toHexString())
                        .param("id", id.toHexString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("Transaction deleted successfully"));
    }

    @Test
    void getFilteredTransactions_ShouldReturnOk() throws Exception {
        Mockito.when(transactionService.getFilteredTransactions(any(TransactionFilterDTO.class)))
                .thenReturn(ResponseEntity.ok("Filtered transactions fetched successfully"));

        mockMvc.perform(get("/api/transactions")
                        .param("category", "1") // Example param from TransactionFilterDTO (if you have others, add them)
                        .param("startDate", "2024-01-01T00:00:00") // Modify according to your DTO fields
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("Filtered transactions fetched successfully"));
    }

}
