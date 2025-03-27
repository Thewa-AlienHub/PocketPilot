package org.example.pocketpilot.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.pocketpilot.commonlib.Controller.ResponseController;
import org.example.pocketpilot.commonlib.ErrorMessage;
import org.example.pocketpilot.commonlib.Response;
import org.example.pocketpilot.dto.BudgetFilterDTO;
import org.example.pocketpilot.dto.requestDTO.BudgetRequestDTO;
import org.example.pocketpilot.entities.BudgetEntity;
import org.example.pocketpilot.enums.BudgetTypes;
import org.example.pocketpilot.enums.TransactionCategory;
import org.example.pocketpilot.enums.common.ResponseMessage;
import org.example.pocketpilot.enums.common.Status;
import org.example.pocketpilot.model.BudgetModel;
import org.example.pocketpilot.repository.BudgetRepository;
import org.example.pocketpilot.service.BudgetService;
import org.example.pocketpilot.utils.CustomUserDetails;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetSeviceImpl extends ResponseController implements BudgetService {

    private final BudgetRepository budgetRepository;
    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public ResponseEntity<Object> setBudgetPlan(BudgetRequestDTO dto) {
        try {


            Authentication authentication = getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            if (authentication == null || !authentication.isAuthenticated()) {
                return sendResponse(new ErrorMessage(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
            }

            ObjectId userId = userDetails.getUserId();
            String category = (TransactionCategory.fromId(dto.getCategory()).get().getValue());
            YearMonth yearMonth = dto.getYearMonth();

            if(budgetRepository.budgetPlanExists(userId,category,yearMonth,dto.getBudgetType())){
                return sendResponse(new ErrorMessage(HttpStatus.CONFLICT, "Budget Plan is already exists for given period and given category"));
            }

            // Get recommendation
            BigDecimal recommendedBudget = calculateRecommendedBudget(userId, category, yearMonth);


            BudgetEntity budgetEntity = mapToBudgetEntity(dto,userId, Status.ACTIVE.getId());
            budgetRepository.saveBudgetPlan(budgetEntity);
            if(recommendedBudget.compareTo(BigDecimal.ZERO) == 0){
                return sendResponse(new Response(ResponseMessage.SUCCESS, HttpStatus.OK));
            }else {
                String message = "Recommended budget for " + category + ": " + recommendedBudget;
                return sendResponse(new Response(ResponseMessage.SUCCESS, HttpStatus.OK,message));
            }



        }catch (Exception e) {
            e.printStackTrace();
            return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during save BudgetPlan"));
        }
    }

    @Override
    public ResponseEntity<Object> getBudgetPlanFilter(BudgetFilterDTO dto) {
            try{
                Authentication authentication = getAuthentication();
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                if (authentication == null || !authentication.isAuthenticated()) {
                    return sendResponse(new ErrorMessage(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
                }

                ObjectId userId = userDetails.getUserId();

                Query query = new Query();
                query.addCriteria(Criteria.where("userId").is(userId));
                if(dto.getBudgetType()>0){
                    query.addCriteria(Criteria.where("budgetType").is(dto.getBudgetType()));
                }

                 if((BudgetTypes.MONTHLYWISE.getId())==dto.getBudgetType()){
                    YearMonth currentYearMonth = YearMonth.now();
                     query.addCriteria(Criteria.where("yearMonth").is(currentYearMonth.toString()));
                }

                if (dto.getCategory() > 0) {
                    query.addCriteria(Criteria.where("category").is(TransactionCategory.fromId(dto.getCategory()).get().getValue()));
                }

                List<BudgetModel> budgetPlanList = budgetRepository.findBudget(query);


                return ResponseEntity.ok(budgetPlanList);

            }catch (Exception e) {
                e.printStackTrace();
                return sendResponse(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during get BudgetPlan"));
            }
    }

    @Override
    public boolean updateBudgetPlan(int Category,ObjectId userId , BigDecimal transactionAmount,String userEmail) {
        try{
            String category = (TransactionCategory.fromId(Category).get().getValue());

            //get categoory-wise budgets
            List<BudgetEntity> categoryWiseBudgets = budgetRepository.findCategoryWiseBudgets(userId,category);

            //get monthlywise budgets
            List<BudgetEntity> monthlyWiseBudgets = budgetRepository.findMonthlyWiseBudgets(userId, category);

            boolean budgetUpdated = false;

            for (BudgetEntity budgetEntity : categoryWiseBudgets) {
                budgetUpdated = budgetRepository.updateSpentAmount(budgetEntity,transactionAmount,userEmail);
                if (budgetUpdated) {
                    log.info("HIT - BudgetService | uppdateBudgetPlan | categoryWise budget updated : {}", budgetEntity);
                }else {
                    log.info("HIT - BudgetService | uppdateBudgetPlan | categoryWise budget Not updated : {}", budgetEntity);
                }

            }

            for (BudgetEntity budgetEntity : monthlyWiseBudgets) {
                budgetUpdated = budgetRepository.updateSpentAmount(budgetEntity,transactionAmount,userEmail);

                if (budgetUpdated) {
                    log.info("HIT - BudgetService | uppdateBudgetPlan | monthlyWise budget updated : {}", budgetEntity);
                }else {
                    log.info("HIT - BudgetService | uppdateBudgetPlan | monthlyWise budget Not updated : {}", budgetEntity);
                }

            }

            return true;


        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public BigDecimal calculateRecommendedBudget(ObjectId userId, String category, YearMonth yearMonth) {
        // Fetch only the last 5 months of budget data
        List<BudgetEntity> pastBudgets = budgetRepository.findRecentBudgets(userId, category, 5);

        if (pastBudgets.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalSpent = BigDecimal.ZERO;
        BigDecimal totalBudgeted = BigDecimal.ZERO;
        int monthsCount = pastBudgets.size();

        for (BudgetEntity budget : pastBudgets) {
            totalSpent = totalSpent.add(budget.getSpentAmount());
            totalBudgeted = totalBudgeted.add(budget.getBudgetAmount());
        }

        BigDecimal avgSpent = totalSpent.divide(BigDecimal.valueOf(monthsCount), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal avgBudgeted = totalBudgeted.divide(BigDecimal.valueOf(monthsCount), 2, BigDecimal.ROUND_HALF_UP);

        // Suggest an increase if spending exceeds budget by more than 10%
        if (avgSpent.compareTo(avgBudgeted.multiply(new BigDecimal("1.1"))) > 0) {
            return avgSpent.multiply(new BigDecimal("1.1"));
        }

        // Suggest a decrease if spending is 20% lower than the budget
        if (avgSpent.compareTo(avgBudgeted.multiply(new BigDecimal("0.8"))) < 0) {
            return avgSpent.multiply(new BigDecimal("0.8"));
        }

        return avgBudgeted;
    }

    private BudgetEntity mapToBudgetEntity(BudgetRequestDTO dto, ObjectId userId,int Status) {
        String category = TransactionCategory.fromId(dto.getCategory())
                .map(TransactionCategory::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + dto.getCategory()));

        return BudgetEntity.builder()
                .userId(userId)
                .planName(dto.getPlanName())
                .category(category)
                .budgetAmount(dto.getBudgetAmount())
                .spentAmount(BigDecimal.ZERO)
                .yearMonth(dto.getYearMonth().toString())
                .budgetType(dto.getBudgetType())
                .Status(Status)
                .build();
    }
}
