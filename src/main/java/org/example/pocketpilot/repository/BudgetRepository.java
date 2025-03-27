package org.example.pocketpilot.repository;

import org.bson.types.ObjectId;
import org.example.pocketpilot.entities.BudgetEntity;
import org.example.pocketpilot.model.BudgetModel;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public interface BudgetRepository {

    boolean budgetPlanExists(ObjectId userId, String category, YearMonth yearMonth,int BudgetType);

    boolean saveBudgetPlan(BudgetEntity budgetEntity);

    List<BudgetModel> findBudget(Query query);

    List<BudgetEntity> findCategoryWiseBudgets(ObjectId userId, String category);

    List<BudgetEntity> findMonthlyWiseBudgets(ObjectId userId, String category);

    boolean updateSpentAmount(BudgetEntity budgets, BigDecimal transactionAmount,String userEmail);

    List<BudgetEntity> findRecentBudgets(ObjectId userId, String category, int months);

    List<BudgetEntity> findAllBudgetsForCurrentMonth();
}
