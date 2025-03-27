package org.example.pocketpilot.controller;

import org.example.pocketpilot.dto.requestDTO.TransactionRequestDTO;
import org.example.pocketpilot.entities.TransactionEntity;
import org.example.pocketpilot.repository.TransactionRepository;
import org.example.pocketpilot.repository.UserRepository;
import org.example.pocketpilot.utils.JwtUtil;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WebMvcTest(TransactionController.class)
@ComponentScan(basePackages = "org.example.pocketpilot")
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser
//@ActiveProfiles("test")
public class TransactionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
   private JwtUtil jwtUtil;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
    }

    @Test
    public void contextLoads() {
        assertNotNull(mockMvc, "MockMvc should not be null");
        assertNotNull(transactionRepository, "TransactionRepository should not be null");
    }

    @Test
    public void checkBeans() {
        String[] beans = applicationContext.getBeanDefinitionNames();
        System.out.println("Registered Beans:");
        for (String bean : beans) {
            System.out.println(bean);
        }
    }



    @Test
    public void testAddTransaction_Success() throws Exception {
        // Prepare request DTO
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setType("expense");
        request.setAmount(new BigDecimal("1000"));
        request.setCategory(2);
        request.setTags(List.of("lunch", "fast food"));
        request.setTransactionDateTime(LocalDateTime.now());
        request.setRecurring(false);

        // Convert request to JSON
        String requestJson = new ObjectMapper().writeValueAsString(request);

        // Perform the request
        mockMvc.perform(post("/api/transactions/add")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(requestJson)
                        .with(csrf())) // Enable CSRF for security
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        // Verify if transaction is saved
        List<TransactionEntity> transactions = transactionRepository.findAll();
        System.out.println("Saved transaction: " + transactions.get(0));

        assertEquals(1, transactions.size());
        assertEquals(2, transactions.get(0).getCategory());
    }
}
