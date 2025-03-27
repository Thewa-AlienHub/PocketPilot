package org.example.pocketpilot.repository.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.pocketpilot.components.NotificationQueue;
import org.example.pocketpilot.entities.BudgetEntity;
import org.example.pocketpilot.enums.BudgetTypes;
import org.example.pocketpilot.enums.NotificationType;
import org.example.pocketpilot.enums.common.Status;
import org.example.pocketpilot.model.BudgetModel;
import org.example.pocketpilot.model.NotificationModel;
import org.example.pocketpilot.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BudgetRepositoryImpl implements BudgetRepository {


    private MongoTemplate mongoTemplate;
    private NotificationQueue notificationQueue;

    @Autowired
    public BudgetRepositoryImpl(MongoTemplate mongoTemplate , NotificationQueue notificationQueue) {
        this.mongoTemplate = mongoTemplate;
        this.notificationQueue = notificationQueue;
    }


    @Override
    public boolean budgetPlanExists(ObjectId userId, String category, YearMonth yearMonth,int BudgetType) {
        try{
            Query query = new Query();
            query.addCriteria(Criteria.where("userId").is(userId)
                    .and("budgetType").is(BudgetType)
                    .and("category").is(category)
            );

            // Only add yearMonth criteria if budgetType is 1
            if (BudgetType == (BudgetTypes.MONTHLYWISE.getId())) {
                query.addCriteria(Criteria.where("yearMonth").is(yearMonth.toString()));
            }

            return mongoTemplate.exists(query, BudgetEntity.class);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to check exist budget plan", e);

        }

    }

    @Override
    public boolean saveBudgetPlan(BudgetEntity budgetEntity) {
        try {
            mongoTemplate.save(budgetEntity);
            return true;
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save BudgetPlan", e);
        }
    }

    @Override
    public List<BudgetModel> findBudget(Query query) {
        try {
            List<BudgetEntity> budgetEntities = mongoTemplate.find(query, BudgetEntity.class);
            return budgetEntities.stream()
                    .map(this::mapToModel)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve Budget Plans", e);
        }
    }

    @Override
    public List<BudgetEntity> findCategoryWiseBudgets(ObjectId userId, String category) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .and("category").is(category)
                .and("budgetType").is(BudgetTypes.CATEGORYWISE.getId())); // 1 = Category-wise budget
        return mongoTemplate.find(query, BudgetEntity.class);
    }

    @Override
    public List<BudgetEntity> findMonthlyWiseBudgets(ObjectId userId, String category) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .and("category").is(category)
                .and("budgetType").is(2) // 2 = Monthly-wise budget
                .and("yearMonth").is(YearMonth.now().toString()));  // Store as String "YYYY-MM"

        List<BudgetEntity> budgets = mongoTemplate.find(query, BudgetEntity.class);

        return budgets.isEmpty() ? Collections.emptyList() : budgets;
    }

    @Override
    public boolean updateSpentAmount(BudgetEntity budgets, BigDecimal transactionAmount,String userEmail) {
        try{
            BigDecimal newSpentAmount = budgets.getSpentAmount().add(transactionAmount);
            BigDecimal budgetAmount = budgets.getBudgetAmount();
            int status = budgets.getStatus();
            String notificationMessage = null;

            if(status != Status.INACTIVE.getId()) {

                //calculate the 80% budgetAmount
                BigDecimal eightyPercent = budgetAmount.multiply(BigDecimal.valueOf(0.8));

                if (newSpentAmount.compareTo(budgetAmount) >= 0) {
                    status = Status.EXCEEDED.getId();
                    notificationMessage = "Alert! Budget exceeded for category: " + budgets.getCategory() + " plane Name : " + budgets.getPlanName();
                    log.warn(notificationMessage);
                } else if (newSpentAmount.compareTo(eightyPercent) >= 0) {
                    status = Status.WARNING.getId();
                    notificationMessage = "Warning: 80% of the budget is used for category: " + budgets.getCategory() + " plane Name : " + budgets.getPlanName();
                    log.warn(notificationMessage);
                }

                Query query = new Query(Criteria.where("_id").is(budgets.getId()));
                Update update = new Update()
                        .set("spentAmount", newSpentAmount)
                        .set("status", status);
                var Result = mongoTemplate.updateFirst(query, update, BudgetEntity.class);

                // Send an IMMEDIATE notification if needed
                if (notificationMessage != null) {
                    NotificationModel notification = NotificationModel.builder()
                            .userId(budgets.getUserId())
                            .userEmail(userEmail)
                            .subject("Budget Plan Warning")
                            .msgBody(notificationMessage)
                            .type(NotificationType.IMMEDIATE)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .status(Status.INITIALIZED)
                            .build();
                    notificationQueue.enqueue(notification);
                }

                return true;

            }
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update the budget plan spent amount", e);

        }


        return false;
    }

    @Override
    public List<BudgetEntity> findRecentBudgets(ObjectId userId, String category, int months) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .and("category").is(category));

        // Sort by yearMonth in descending order (latest first)
        query.with(Sort.by(Sort.Order.desc("yearMonth")));

        // Limit the results to the last 5 months
        query.limit(months);

        return mongoTemplate.find(query, BudgetEntity.class);
    }

    @Override
    public List<BudgetEntity> findAllBudgetsForCurrentMonth() {
        YearMonth currentMonth = YearMonth.now();

        Query query = new Query();
        query.addCriteria(Criteria.where("yearMonth").is(currentMonth));

        return mongoTemplate.find(query, BudgetEntity.class);
    }

    private BudgetModel mapToModel(BudgetEntity entity) {
        return BudgetModel.builder()
                .id(entity.getId())
                .planName(entity.getPlanName())
                .userId(entity.getUserId())
                .budgetType(BudgetTypes.fromId(entity.getBudgetType()).get().getValue())
                .category(entity.getCategory())
                .budgetAmount(entity.getBudgetAmount())
                .spentAmount(entity.getSpentAmount())
                .yearMonth(YearMonth.parse(entity.getYearMonth()))
                .Status(Status.fromId(entity.getStatus()).get().getValue())
                .build();
    }
}
