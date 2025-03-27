package org.example.pocketpilot.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.commonlib.ErrorMessage;
import org.example.pocketpilot.commonlib.Response;
import org.example.pocketpilot.components.NotificationQueue;
import org.example.pocketpilot.dto.requestDTO.BudgetRequestDTO;
import org.example.pocketpilot.dto.requestDTO.TransactionRequestDTO;
import org.example.pocketpilot.dto.TransactionFilterDTO;
import org.example.pocketpilot.entities.TransactionEntity;
import org.example.pocketpilot.enums.NotificationType;
import org.example.pocketpilot.enums.TransactionCategory;
import org.example.pocketpilot.enums.common.ResponseMessage;
import org.example.pocketpilot.enums.common.Status;
import org.example.pocketpilot.model.NotificationModel;
import org.example.pocketpilot.model.TransactionModel;
import org.example.pocketpilot.repository.TransactionRepository;
import org.example.pocketpilot.repository.UserRepository;
import org.example.pocketpilot.service.BudgetService;
import org.example.pocketpilot.service.FinancialGoalService;
import org.example.pocketpilot.service.TransactionService;
import org.example.pocketpilot.utils.CurencyConversionService;
import org.example.pocketpilot.utils.CustomUserDetails;
import org.example.pocketpilot.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl extends ResponseController implements TransactionService {


    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final CurencyConversionService curencyConversionService;
    private final FinancialGoalService financialGoalService;
    private final BudgetService budgetService;
    private final NotificationQueue notificationQueue;

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    @Override
    @Transactional
    public ResponseEntity<Object> addTransaction(TransactionRequestDTO dto) {

        try {
            Authentication authentication = getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (authentication == null || !authentication.isAuthenticated()) {
                return sendResponse(new ErrorMessage(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
            }
            String userRole = authentication.getAuthorities().toString();
            ObjectId userId = userDetails.getUserId();
            String userEmail = userDetails.getUserEmail();



            //check currency and convert
            String UserCurrency = userRepository.getCurrencyCodeById(userId);

            BigDecimal convertedAmount = curencyConversionService.convertCurrency(dto.getAmount(),UserCurrency,"LKR");

            //check the unusual spending pattern
            if("expense".equals(dto.getType())) {
                detectUnusualSpending(userId,TransactionCategory.fromId(dto.getCategory()).get().getValue(),convertedAmount,userEmail);
            }


            //check the invest and send money for goals
            if("income".equalsIgnoreCase(dto.getType())){
                financialGoalService.autoAllocateSavings(userId,convertedAmount) ;
            }


            TransactionEntity transaction = TransactionEntity.builder()
                    .userId(userId)
                    .type(dto.getType())
                    .amount(convertedAmount)
                    .category(TransactionCategory.fromId(dto.getCategory()).get().getValue())
                    .tags(dto.getTags())
                    .transactionDateTime(dto.getTransactionDateTime())
                    .recurring(dto.isRecurring())
                    .recurrencePattern(dto.isRecurring()?dto.getRecurrencePattern():null)
                    .nextOccurrence(dto.isRecurring()? calculateNextOccurrence(dto.getRecurrencePattern()): null)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .status(dto.isRecurring()? Status.INITIALIZED:null)
                    .build();

            boolean transactionDone = transactionRepository.save(transaction);

            if (transactionDone) {
                boolean updated = budgetService.updateBudgetPlan(dto.getCategory(),userId,convertedAmount,userEmail);


            }


            return sendResponse(new Response(ResponseMessage.SUCCESS, HttpStatus.OK));
                    

        }catch (Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during add Transaction"),HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }

    @Override
    public ResponseEntity<Object> getFilteredTransactions(TransactionFilterDTO dto) {
        try {

            Authentication authentication = getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (authentication == null || !authentication.isAuthenticated()) {
                return sendResponse(new ErrorMessage(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
            }
            ObjectId userId = userDetails.getUserId();


            List<TransactionModel> transactions = new ArrayList<>();
            transactions = transactionRepository.findTransactionByFilter(dto,userId)
                    .stream()
                    .map(this::convertToModel)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(transactions);
        }catch (Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while filtering the transaction"));
        }
    }

    @Override
    public ResponseEntity<Object> updateTransactions(ObjectId id, TransactionRequestDTO dto) {
        try{

            Optional<TransactionEntity> existingTransactionOpt = transactionRepository.findById(id);

            if (existingTransactionOpt.isEmpty()) {
                return sendResponse(new ErrorMessage(HttpStatus.NOT_FOUND, "Transaction not found"));
            }

            TransactionEntity existingTransaction = existingTransactionOpt.get();

            //check currency and convert
            String UserCurrency = userRepository.getCurrencyCodeById(existingTransaction.getUserId());

            BigDecimal convertedAmount = curencyConversionService.convertCurrency(dto.getAmount(),UserCurrency,"LKR");

            TransactionEntity updatedTransaction = TransactionEntity.builder()
                    .id(existingTransaction.getId())
                    .userId(existingTransaction.getUserId())
                    .type(dto.getType())
                    .amount(convertedAmount)
                    .category(TransactionCategory.fromId(dto.getCategory()).get().getValue())
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

    public boolean detectUnusualSpending( ObjectId userId , String Category, BigDecimal transactionAmount,String UserEmail) {


            List<TransactionEntity> transactions = transactionRepository.findByUserIdAndDateAfter(
                    userId, 5,Category,"expense"
            );

            // Map to store max transaction amount per month
            Map<Integer, BigDecimal> monthlyMax = new TreeMap<>();

            for (TransactionEntity transaction : transactions) {
                int month = transaction.getTransactionDateTime().getMonthValue(); // Get month number (1-12)
                monthlyMax.put(month, monthlyMax.getOrDefault(month, BigDecimal.ZERO)
                        .max(transaction.getAmount())); // Keep track of max transaction per month
            }


            // Calculate usual amount based on max transaction trends
            BigDecimal usualAmount = BigDecimal.ZERO;
            BigDecimal prevMonthMax = null;

            for (Map.Entry<Integer, BigDecimal> entry : monthlyMax.entrySet()) {
                BigDecimal currentMax = entry.getValue();

                if (prevMonthMax != null) {
                    if (currentMax.compareTo(prevMonthMax) > 0) {
                        usualAmount = prevMonthMax.multiply(new BigDecimal("1.2")); // Increase by 20%
                    } else {
                        usualAmount = prevMonthMax.multiply(new BigDecimal("0.95")); // Decrease by 5%
                    }
                } else {
                    usualAmount = currentMax; // Initialize with first month's max
                }

                prevMonthMax = usualAmount;
            }

            // Check if the current month's max spending exceeds the usual amount
            if (transactionAmount.compareTo(usualAmount) > 0) {
                String NotificationMessage = "Unusual spending detected! Your spending is higher than usual this month.";

                NotificationModel notificationModel = NotificationModel.builder()
                        .userId(userId)
                        .userEmail(UserEmail)
                        .enableEmailNotification(true)
                        .subject("Unusual spending")
                        .msgBody(NotificationMessage)
                        .type(NotificationType.IMMEDIATE)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .status(Status.INITIALIZED)
                        .build();

                notificationQueue.enqueue(notificationModel);

                return false;
            }

        return true;

    }

    @Scheduled(cron = "0 0 0 * * ?")// Runs every day at 12:00 AM
    @Override
    public void processRecurringTransactions() {
        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime now = LocalDateTime.of(2025, 4, 21, 19, 21, 21);

        List<TransactionEntity> transactions = transactionRepository.findRecurringTransactions(now);
        List<TransactionEntity> missedTransactions = transactionRepository.findMissedRecurringTransactions(now);
        List<TransactionEntity> upcomingTransactions = transactionRepository.findRecurringUpcomingTransactions(now);

        for (TransactionEntity transaction : transactions) {
            try {

                // Calculate next occurrence based on recurrencePattern
                LocalDateTime nextOccurrence = calculateNextOccurrence(transaction.getRecurrencePattern());

                // Update next occurrence in DB
                transactionRepository.updateSuccessStatus(transaction.getId(), Status.SUCCESS, LocalDateTime.now());

                processPayment(transaction ,nextOccurrence);



                log.info("Processed recurring transaction: {} - Next occurrence: {}", transaction.getId(), nextOccurrence);
            } catch (Exception e) {
                log.error("Error processing transaction: {}", transaction.getId(), e);
            }
        }

        for (TransactionEntity missedtransaction : missedTransactions) {

            String NotificationMessage = "You Missed the Recurring Payment In " +
                    missedtransaction.getCategory()+" Category , \n Amount :" + missedtransaction.getAmount() + "\n The Occurrence Date is " + missedtransaction.getNextOccurrence();

            NotificationModel notificationModel = NotificationModel.builder()
                    .userId(missedtransaction.getUserId())
                    .enableEmailNotification(true)
                    .subject("Missed Recuring Payment")
                    .msgBody(NotificationMessage)
                    .type(NotificationType.SCHEDULED)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .status(Status.INITIALIZED)
                    .build();

            notificationQueue.enqueue(notificationModel);
        }

        for (TransactionEntity upcomingtransaction : upcomingTransactions) {

            String NotificationMessage = "There is Recurring Payment In " +
                    upcomingtransaction.getCategory()+" Category ,\n  Amount :" + upcomingtransaction.getAmount() +","+
                    "\n The Recuring will be process in " + upcomingtransaction.getNextOccurrence();

            NotificationModel notificationModel = NotificationModel.builder()
                    .userId(upcomingtransaction.getUserId())
                    .enableEmailNotification(true)
                    .subject("Upcoming Recuring Payment")
                    .msgBody(NotificationMessage)
                    .type(NotificationType.SCHEDULED)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .status(Status.INITIALIZED)
                    .build();

            notificationQueue.enqueue(notificationModel);

        }
    }

    private void processPayment(TransactionEntity transaction , LocalDateTime nextOccurrence) {

        //create New Transaction with updated next occurence date
        transaction.setId(null);
        transaction.setNextOccurrence(nextOccurrence);
        transaction.setStatus(Status.INITIALIZED);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setTransactionDateTime(LocalDateTime.now());

        transactionRepository.save(transaction);

        String NotificationMessage = "SuccessFully Processd the Recurring Payment In " +
                transaction.getCategory()+" Category , Amount :" + transaction.getAmount();

        NotificationModel notificationModel = NotificationModel.builder()
                .userId(transaction.getUserId())
                .enableEmailNotification(true)
                .subject("Recuuring Payment")
                .msgBody(NotificationMessage)
                .type(NotificationType.SCHEDULED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Status.INITIALIZED)
                .build();

        notificationQueue.enqueue(notificationModel);

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
