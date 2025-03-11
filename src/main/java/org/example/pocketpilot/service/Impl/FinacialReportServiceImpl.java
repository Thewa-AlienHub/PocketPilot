package org.example.pocketpilot.service.Impl;

import org.bson.types.ObjectId;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.commonlib.ErrorMessage;
import org.example.pocketpilot.dto.RequestDTO.FinancialReportRequestDTO;
import org.example.pocketpilot.entities.TransactionEntity;
import org.example.pocketpilot.repository.TransactionRepository;
import org.example.pocketpilot.service.FinancialReportService;
import org.example.pocketpilot.utils.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class FinacialReportServiceImpl extends ResponseController implements FinancialReportService {

    private TransactionRepository transactionRepository;


    public FinacialReportServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    @Override
    public ResponseEntity<Object> getSpendingTrends(FinancialReportRequestDTO dto) {
        try{
            Authentication authentication = getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return sendResponse(new ErrorMessage(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
            }
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            ObjectId userId = userDetails.getUserId();

            List<Map<String, Object>> trends = transactionRepository.getSpendingTrends(userId, dto.getStartDate(), dto.getEndDate());
            return trends.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(trends);

        }catch (Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during Finance report get SpendTrends"),HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @Override
    public ResponseEntity<Object> getIncomeVsExpense(FinancialReportRequestDTO dto) {
       try {
           Authentication authentication = getAuthentication();

           if (authentication == null || !authentication.isAuthenticated()) {
               return sendResponse(new ErrorMessage(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
           }
           CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

           ObjectId userId = userDetails.getUserId();

           Map<String, BigDecimal> summary = transactionRepository.getIncomeVsExpense(userId, dto.getStartDate(), dto.getEndDate());
           return summary != null && !summary.isEmpty() ? ResponseEntity.ok(summary) : ResponseEntity.noContent().build();


       }catch (Exception e){
           e.printStackTrace();
           return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during getIncomeVsExpense"),HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @Override
    public ResponseEntity<Object> getFilteredTransactions(FinancialReportRequestDTO dto) {
        try {
            Authentication authentication = getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return sendResponse(new ErrorMessage(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
            }
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            ObjectId userId = userDetails.getUserId();

            List<TransactionEntity> transactions = transactionRepository.getFilteredTransactions(userId, dto.getStartDate(),dto.getEndDate(),dto.getCategories(),dto.getTags());
            return transactions.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(transactions);


        }catch (Exception e){
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during getFilteredTransactions"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
