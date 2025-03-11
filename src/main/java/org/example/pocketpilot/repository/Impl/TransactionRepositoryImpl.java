package org.example.pocketpilot.repository.Impl;

import org.bson.types.ObjectId;
import org.example.pocketpilot.dto.TransactionFilterDTO;
import org.example.pocketpilot.entities.TransactionEntity;
import org.example.pocketpilot.entities.UserEntity;
import org.example.pocketpilot.model.TransactionModel;
import org.example.pocketpilot.model.UserModel;
import org.example.pocketpilot.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    @Autowired
    private  MongoTemplate mongoTemplate;

    public TransactionRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }




    @Override
    public boolean save(TransactionModel transactionModel) {
        try {
            TransactionEntity transactionEntity = mapToTransactionEntity(transactionModel);
            mongoTemplate.save(transactionEntity);
            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save user", e);
        }
    }

    @Override
    public List<TransactionEntity> findTransactionByFilter(TransactionFilterDTO transactionFilter) {
        Query query = new Query();
        List<Criteria>criteriaList = new ArrayList<>();

        if (transactionFilter.getTags() != null && !transactionFilter.getTags().isEmpty()) {
            criteriaList.add(Criteria.where("tags").in(transactionFilter.getTags()));
        }
        if (transactionFilter.getStartDate() != null && transactionFilter.getEndDate() != null) {
            criteriaList.add(Criteria.where("date").gte(transactionFilter.getStartDate()).lte(transactionFilter.getEndDate()));
        }
        if (transactionFilter.getType() != null) {
            criteriaList.add(Criteria.where("type").is(transactionFilter.getType()));
        }
        if (transactionFilter.getCategory() != null) {
            criteriaList.add(Criteria.where("category").is(transactionFilter.getCategory()));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, TransactionEntity.class);
    }

    @Override
    public Optional<TransactionEntity> findById(ObjectId id) {
        return Optional.ofNullable(mongoTemplate.findById(id, TransactionEntity.class));
    }

    @Override
    public boolean updateTransaction(ObjectId id, TransactionEntity updatedTransaction) {
        Query query = new Query();
        Update update = new Update()
                .set("userId", updatedTransaction.getUserId())
                .set("type", updatedTransaction.getType())
                .set("amount", updatedTransaction.getAmount())
                .set("category", updatedTransaction.getCategory())
                .set("tags", updatedTransaction.getTags())
                .set("transactionDateTime", updatedTransaction.getTransactionDateTime())
                .set("recurring", updatedTransaction.isRecurring())
                .set("recurrencePattern", updatedTransaction.getRecurrencePattern())
                .set("nextOccurrence", updatedTransaction.getNextOccurrence())
                .set("updatedAt", updatedTransaction.getUpdatedAt());

        var updateResult = mongoTemplate.updateFirst(query, update, TransactionEntity.class);
        return updateResult.getModifiedCount()>0;

    }

    @Override
    public boolean deleteTransaction(ObjectId id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.remove(query, TransactionEntity.class).getDeletedCount() > 0;
    }

    // Spending Trends Over Time
    @Override
    public List<Map<String, Object>> getSpendingTrends(ObjectId userId, LocalDateTime startDate, LocalDateTime endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(userId)
                        .and("transactionDateTime").gte(startDate).lte(endDate)
                        .and("type").is("expense")),
                Aggregation.project("amount", "transactionDateTime")
                        .andExpression("dayOfMonth(transactionDateTime)").as("day"),
                Aggregation.group("day").sum("amount").as("totalSpent"),
                Aggregation.project("totalSpent").and("_id").as("day"),  // Renaming _id back to day
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "day")) // Use Sort.by() instead
        );

        AggregationResults<Map<String, Object>> results =
                mongoTemplate.aggregate(aggregation, "transaction", (Class<Map<String, Object>>) (Class<?>) Map.class);

        return results.getMappedResults();
    }



    // Income vs Expenses Summary
    @Override
    public Map<String, BigDecimal> getIncomeVsExpense(ObjectId userId, LocalDateTime startDate, LocalDateTime endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(userId)
                        .and("transactionDateTime").gte(startDate).lte(endDate)),
                Aggregation.group("type").sum("amount").as("total")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "transaction", Map.class);
        return results.getMappedResults().stream().collect(
                Collectors.toMap(
                        entry -> entry.get("_id").toString(),
                        entry -> new BigDecimal(entry.get("total").toString())
                )
        );
    }

    // Filtered Transactions by Category and Tags
    @Override
    public List<TransactionEntity> getFilteredTransactions(ObjectId userId, LocalDateTime startDate, LocalDateTime endDate, List<String> categories, List<String> tags) {
        Criteria criteria = Criteria.where("userId").is(userId)
                .and("transactionDateTime").gte(startDate).lte(endDate);

        if (categories != null && !categories.isEmpty()) {
            criteria.and("category").in(categories);
        }
        if (tags != null && !tags.isEmpty()) {
            criteria.and("tags").in(tags);
        }

        return mongoTemplate.find(
                new Query(criteria),
                TransactionEntity.class
        );
    }

    @Override
    public void saveAll(List<TransactionEntity> transactions) {
        mongoTemplate.insertAll(transactions);
    }

    @Override
    public List<TransactionEntity> findByUserIdAndDateAfter(ObjectId userId, int months, String category) {
        LocalDateTime fromDate = LocalDateTime.now().minusMonths(months);

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                        .and("category").is(category)
                .and("transactionDateTime").gte(fromDate));

        return mongoTemplate.find(query, TransactionEntity.class);
    }

    @Override
    public List<TransactionEntity> getUserTransactions(ObjectId userId) {
        return mongoTemplate.find(
                Query.query(Criteria.where("userId").is(userId)),
                TransactionEntity.class
        );
    }


    private UserModel mapToUserModel(UserEntity entity) {
        UserModel model = new UserModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setEmail(entity.getEmail());
        model.setUserName(entity.getUserName());
        model.setUserRole(entity.getUserRole());
        return model;
    }

    private TransactionEntity mapToTransactionEntity(TransactionModel transactionModel) {
        return TransactionEntity.builder()
                .id(transactionModel.getId())
                .userId(transactionModel.getUserId())
                .type(transactionModel.getType())
                .amount(transactionModel.getAmount())
                .category(transactionModel.getCategory())
                .tags(transactionModel.getTags())
                .transactionDateTime(transactionModel.getTransactionDateTime())
                .recurring(transactionModel.isRecurring())
                .recurrencePattern(transactionModel.getRecurrencePattern())
                .nextOccurrence(transactionModel.getNextOccurrence())
                .createdAt(transactionModel.getCreatedAt())
                .updatedAt(transactionModel.getUpdatedAt())
                .build();
    }

}
