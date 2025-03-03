package org.example.pocketpilot.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.commonlib.ErrorMessage;
import org.example.pocketpilot.commonlib.Response;
import org.example.pocketpilot.dto.RequestDTO.TransactionRequestDTO;
import org.example.pocketpilot.dto.TransactionFilterDTO;
import org.example.pocketpilot.entities.TransactionEntity;
import org.example.pocketpilot.enums.TransactionCategory;
import org.example.pocketpilot.enums.common.ResponseMessage;
import org.example.pocketpilot.model.TransactionModel;
import org.example.pocketpilot.repository.TransactionRepository;
import org.example.pocketpilot.service.TransactionService;
import org.example.pocketpilot.utils.CustomUserDetails;
import org.example.pocketpilot.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl extends ResponseController implements TransactionService {


    private final TransactionRepository transactionRepository;
    private final JwtUtil jwtUtil;

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    @Override
    public ResponseEntity<Object> addTransaction(TransactionRequestDTO dto) {

        try {
            Authentication authentication = getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (authentication == null || !authentication.isAuthenticated()) {
                return sendResponse(new ErrorMessage(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
            }
            String userRole = authentication.getAuthorities().toString();
            ObjectId userId = userDetails.getUserId();
            System.out.println(userId);


            TransactionModel transactionModel = TransactionModel.builder()
                    .userId(userId)
                    .type(dto.getType())
                    .amount(dto.getAmount())
                    .category(String.valueOf(TransactionCategory.valueOf(dto.getCategory())))
                    .tags(dto.getTags())
                    .transactionDateTime(dto.getTransactionDateTime())
                    .recurring(dto.isRecurring())
                    .recurrencePattern(dto.getRecurrencePattern())
                    .nextOccurrence(dto.isRecurring()? calculateNextOccurrence(dto.getRecurrencePattern()): null)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            transactionRepository.save(transactionModel);

            return sendResponse(new Response(ResponseMessage.SUCCESS, HttpStatus.OK));
                    

        }catch (Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during add Transaction"));
        }



    }

    @Override
    public ResponseEntity<Object> getFilteredTransactions(TransactionFilterDTO dto) {
        List<TransactionModel>transactions = new ArrayList<>();
        transactions= transactionRepository.findTransactionByFilter(dto)
                .stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(transactions);
    }

    @Override
    public ResponseEntity<Object> updateTransactions(ObjectId id, TransactionRequestDTO dto) {
        try{

            Optional<TransactionEntity> existingTransactionOpt = transactionRepository.findById(id);

            if (existingTransactionOpt.isEmpty()) {
                return sendResponse(new ErrorMessage(HttpStatus.NOT_FOUND, "Transaction not found"));
            }

            TransactionEntity existingTransaction = existingTransactionOpt.get();
            TransactionEntity updatedTransaction = TransactionEntity.builder()
                    .id(existingTransaction.getId())
                    .userId(existingTransaction.getUserId())
                    .type(dto.getType())
                    .amount(dto.getAmount())
                    .category(String.valueOf(TransactionCategory.valueOf(dto.getCategory())))
                    .tags(dto.getTags())
                    .transactionDateTime(dto.getTransactionDateTime())
                    .recurring(dto.isRecurring())
                    .recurrencePattern(dto.getRecurrencePattern())
                    .nextOccurrence(dto.isRecurring()? calculateNextOccurrence(dto.getRecurrencePattern()): null)
                    .updatedAt(LocalDateTime.now())
                    .build();

                transactionRepository.updateTransaction(id,updatedTransaction);

                return sendResponse(new Response(ResponseMessage.SUCCESS, HttpStatus.OK));


        }catch(Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while updating the transaction"));
        }
    }

    @Override
    public ResponseEntity<Object> deleteTransactions(ObjectId id) {
        try {
            Optional<TransactionEntity> existingTransactionOpt = transactionRepository.findById(id);

            if (existingTransactionOpt.isEmpty()) {
                return sendResponse(new ErrorMessage(HttpStatus.NOT_FOUND, "Transaction not found"));
            }

            transactionRepository.deleteTransaction(id);
            return sendResponse(new Response(ResponseMessage.SUCCESS, HttpStatus.OK));

        }catch ( Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while deleting the transaction"));
        }
    }

    @Override
    public ResponseEntity<Object> getTransactionById(ObjectId id) {
        try {
            Optional<TransactionEntity> TransactionById = transactionRepository.findById(id);

            if (TransactionById.isEmpty()) {
                return sendResponse(new ErrorMessage(HttpStatus.NOT_FOUND, "Transaction not found"));
            }

            TransactionModel transaction = convertToModel(TransactionById.get());
            return ResponseEntity.ok(transaction);

        }catch (Exception e ){
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while getting the transaction By ID"));
        }
    }

    private TransactionModel convertToModel(TransactionEntity entity) {
        return TransactionModel.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getType())
                .amount(entity.getAmount())
                .category(entity.getCategory())
                .tags(entity.getTags())
                .transactionDateTime(entity.getTransactionDateTime())
                .recurring(entity.isRecurring())
                .recurrencePattern(entity.getRecurrencePattern())
                .nextOccurrence(entity.getNextOccurrence())
                .build();
    }

    private LocalDateTime calculateNextOccurrence(String pattern) {
        LocalDateTime now = LocalDateTime.now();
        switch (pattern.toLowerCase()) {
            case "daily":
                return now.plusDays(1);
            case "weekly":
                return now.plusWeeks(1);
            case "monthly":
                return now.plusMonths(1);
            default:
                return null;
        }
    }
}
