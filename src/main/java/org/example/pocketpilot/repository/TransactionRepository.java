package org.example.pocketpilot.repository;

import io.micrometer.common.KeyValues;
import org.bson.types.ObjectId;
import org.example.pocketpilot.dto.TransactionFilterDTO;
import org.example.pocketpilot.entities.TransactionEntity;
import org.example.pocketpilot.enums.common.Status;
import org.example.pocketpilot.model.TransactionModel;
import org.example.pocketpilot.model.UserModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TransactionRepository {

    boolean save(TransactionEntity transactionModel);

    List<TransactionEntity>findTransactionByFilter(TransactionFilterDTO transactionFilter ,ObjectId userId);

    Optional<TransactionEntity> findById(ObjectId id);

    boolean updateTransaction(ObjectId id, TransactionEntity updatedTransaction);

    boolean deleteTransaction(ObjectId id);

    // Spending Trends Over Time
    List<Map<String, Object>> getSpendingTrends(ObjectId userId, LocalDateTime startDate, LocalDateTime endDate);

    // Income vs Expenses Summary
    Map<String, BigDecimal> getIncomeVsExpense(ObjectId userId, LocalDateTime startDate, LocalDateTime endDate);

    // Filtered Transactions by Category and Tags
    List<TransactionEntity> getFilteredTransactions(ObjectId userId, LocalDateTime startDate, LocalDateTime endDate, List<String> categories, List<String> tags);


    void saveAll(List<TransactionEntity> transactions);

    List<TransactionEntity> findByUserIdAndDateAfter(ObjectId userId,int months, String category,String type);

    List<TransactionEntity>getUserTransactions(ObjectId userId);

    List<TransactionEntity> findRecurringTransactions(LocalDateTime now);

    boolean updateSuccessStatus(ObjectId id, Status status, LocalDateTime now);

    List<TransactionEntity> findMissedRecurringTransactions(LocalDateTime now);

    List<TransactionEntity> findRecurringUpcomingTransactions(LocalDateTime now);

    void deleteAll();

    List<TransactionEntity> findAll();
}
