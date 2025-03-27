package org.example.pocketpilot.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.commonlib.ErrorMessage;
import org.example.pocketpilot.entities.TransactionEntity;
import org.example.pocketpilot.model.TransactionModel;
import org.example.pocketpilot.repository.BudgetRepository;
import org.example.pocketpilot.repository.FinancialGoalRepository;
import org.example.pocketpilot.repository.TransactionRepository;
import org.example.pocketpilot.repository.UserRepository;
import org.example.pocketpilot.service.DashboardService;
import org.example.pocketpilot.utils.CurencyConversionService;
import org.example.pocketpilot.utils.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashBoardServiceImpl extends ResponseController implements DashboardService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final FinancialGoalRepository financialGoalRepository;
    private final UserRepository userRepository;
    private final CurencyConversionService curencyConversionService;

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public ResponseEntity<Object> getUserTransactions() {
        try {
            Authentication authentication = getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (authentication == null || !authentication.isAuthenticated()) {
                return sendResponse(new ErrorMessage(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
            }
            String userRole = authentication.getAuthorities().toString();
            ObjectId userId = userDetails.getUserId();

            List<TransactionModel> transactions = new ArrayList<>();

            transactions= transactionRepository.getUserTransactions(userId)
                    .stream()
                    .map(this::convertToModel)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(transactions);


        }catch (Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during get user Transactions for Dashboard"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    private TransactionModel convertToModel(TransactionEntity entity) {
        //check currency and convert
        String UserCurrency = userRepository.getCurrencyCodeById(entity.getUserId());

        BigDecimal convertedAmount = curencyConversionService.convertCurrency(entity.getAmount(),"LKR",UserCurrency);

        return TransactionModel.builder()
                .id(entity.getId().toHexString())
                .userId(entity.getUserId().toHexString())
                .type(entity.getType())
                .amount(convertedAmount)
                .category(entity.getCategory())
                .tags(entity.getTags())
                .transactionDateTime(entity.getTransactionDateTime())
                .recurring(entity.isRecurring())
                .recurrencePattern(entity.getRecurrencePattern())
                .nextOccurrence(entity.getNextOccurrence())
                .build();
    }
}
