package org.example.pocketpilot.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.commonlib.ErrorMessage;
import org.example.pocketpilot.commonlib.Response;
import org.example.pocketpilot.components.NotificationQueue;
import org.example.pocketpilot.dto.requestDTO.FinancialGoalsRequestDTO;
import org.example.pocketpilot.dto.responseDTO.FinancialGoalResponseDTO;
import org.example.pocketpilot.entities.TransactionEntity;
import org.example.pocketpilot.enums.NotificationType;
import org.example.pocketpilot.enums.TransactionCategory;
import org.example.pocketpilot.enums.common.ResponseMessage;
import org.example.pocketpilot.enums.common.Status;
import org.example.pocketpilot.model.FinancialGoalModel;
import org.example.pocketpilot.model.NotificationModel;
import org.example.pocketpilot.repository.FinancialGoalRepository;
import org.example.pocketpilot.repository.TransactionRepository;
import org.example.pocketpilot.service.FinancialGoalService;
import org.example.pocketpilot.utils.CustomUserDetails;
import org.example.pocketpilot.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialGoalServiceImpl extends ResponseController implements FinancialGoalService {


    private final FinancialGoalRepository financialGoalRepository;
    private final TransactionRepository transactionRepository;
    private final JwtUtil jwtUtil;
    private final NotificationQueue notificationQueue;

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    @Override
    public ResponseEntity<Object> addFinancialGoal(FinancialGoalsRequestDTO dto) {

        try {
            Authentication authentication = getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (authentication == null || !authentication.isAuthenticated()) {
                return sendResponse(new ErrorMessage(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
            }
            String userRole = authentication.getAuthorities().toString();
            ObjectId userId = userDetails.getUserId();



            FinancialGoalModel financialGoalModel = FinancialGoalModel.builder()
                    .userId(userId)
                    .goalName(dto.getGoalName())
                    .targetAmount(dto.getTargetAmount())
                    .currentAmount(BigDecimal.ZERO)
                    .deadLine(dto.getDeadline())
                    .autoAllocate(dto.isAutoAllocate())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            financialGoalRepository.save(financialGoalModel);

            return sendResponse(new Response(ResponseMessage.SUCCESS, HttpStatus.OK));
                    

        }catch (Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during add Financial Goal"),HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }

    @Override
    public ResponseEntity<Object> getGoalProgress(ObjectId goalId) {
        try {
            Authentication authentication = getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            if (authentication == null || !authentication.isAuthenticated()) {
                return sendResponse(new ErrorMessage(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
            }
            String userRole = authentication.getAuthorities().toString();
            ObjectId userId = userDetails.getUserId();

            Optional<FinancialGoalModel> goal = financialGoalRepository.findById(goalId);

            if (goal.isEmpty()) {
                return sendResponse(new ErrorMessage(HttpStatus.NOT_FOUND, "Financial goal not found"));
            }

            FinancialGoalModel financialGoalModel = goal.get();
            BigDecimal progressPrecentage = getProgressPercentage(financialGoalModel.getTargetAmount(),financialGoalModel.getCurrentAmount());

            FinancialGoalResponseDTO goalresponse = mapToResponse(financialGoalModel,progressPrecentage);

            return sendResponse(goalresponse,HttpStatus.OK);


        }catch (Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during get Financial goal progress"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<Object> autoAllocateSavings(ObjectId userId, BigDecimal incomeAmount) {
        try {

            List<FinancialGoalModel> goal = financialGoalRepository.getAutoAllocatedGoals(userId);

            if (goal.isEmpty()) {
                return sendResponse(new ErrorMessage(HttpStatus.NOT_FOUND, "Financial goal not found"));
            }

            // Calculate 10% of income to allocate
            BigDecimal allocationAmount = incomeAmount.multiply(BigDecimal.valueOf(0.1));
            BigDecimal amountPerGoal = allocationAmount.divide(BigDecimal.valueOf(goal.size()), 2, RoundingMode.HALF_UP);

            List<FinancialGoalModel> updatedGoals = new ArrayList<>();
            List<TransactionEntity> transactions = new ArrayList<>();

            StringBuilder notificationMessage = new StringBuilder("Your financial goals have been updated:\n");

            for (FinancialGoalModel goals : goal) {

                // Update the goal's current amount
                BigDecimal newCurrentAmount = goals.getCurrentAmount().add(amountPerGoal);
                goals.setCurrentAmount(newCurrentAmount);
                goals.setUpdatedAt(LocalDateTime.now());
                updatedGoals.add(goals);

                // Append goal name and amount to message
                notificationMessage.append(String.format("\n %s: %,.2f", goals.getGoalName(), goals.getCurrentAmount()));

                // Create a transaction record
                TransactionEntity transaction = TransactionEntity.builder()
                        .userId(userId)
                        .type("expense")
                        .amount(amountPerGoal)
                        .category(TransactionCategory.GOALS.getValue())
                        .tags(List.of("Auto-Savings"))
                        .transactionDateTime(LocalDateTime.now())
                        .recurring(false)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                transactions.add(transaction);
            }

            // Update goals in the database
            boolean updated = financialGoalRepository.updateGoals(updatedGoals);

            if(updated){

                NotificationModel notificationModel = NotificationModel.builder()
                        .userId(userId)
                        .enableEmailNotification(true)
                        .subject("Financial Goals Updated")
                        .msgBody(notificationMessage.toString())
                        .type(NotificationType.IMMEDIATE)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .status(Status.INITIALIZED)
                        .build();

                notificationQueue.enqueue(notificationModel);
            }

            // Save transactions in the database
            transactionRepository.saveAll(transactions);


            return sendResponse(new Response(ResponseMessage.SUCCESS, HttpStatus.OK));


        }catch (Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during autoAllocateSavings in Goals"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private FinancialGoalResponseDTO mapToResponse(FinancialGoalModel financialGoalModel, BigDecimal progressPercentage) {
        return FinancialGoalResponseDTO.builder()
                .id(financialGoalModel.getId())
                .userId(financialGoalModel.getUserId())
                .goalName(financialGoalModel.getGoalName())
                .targetAmount(financialGoalModel.getTargetAmount())
                .currentAmount(financialGoalModel.getCurrentAmount())
                .progressPrecentage(progressPercentage)
                .build();
    }

    public BigDecimal getProgressPercentage(BigDecimal targetAmount, BigDecimal currentAmount) {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAmount.multiply(BigDecimal.valueOf(100)).divide(targetAmount, 2, BigDecimal.ROUND_HALF_UP);
    }


}
