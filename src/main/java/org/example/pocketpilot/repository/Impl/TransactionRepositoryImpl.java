package org.example.pocketpilot.repository.Impl;


import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import org.bson.types.ObjectId;
import org.example.pocketpilot.dto.TransactionFilterDTO;
import org.example.pocketpilot.entities.TransactionEntity;
import org.example.pocketpilot.entities.UserEntity;
import org.example.pocketpilot.enums.common.Status;
import org.example.pocketpilot.model.TransactionModel;
import org.example.pocketpilot.model.UserModel;
import org.example.pocketpilot.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.time.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    @Autowired
    private  MongoTemplate mongoTemplate;
    @Autowired
    private MongoClient mongoClient;

    private static final String month = "month";

    public TransactionRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }




    @Override
    public boolean save(TransactionEntity transaction) {
        try {
//            TransactionEntity transactionEntity = mapToTransactionEntity(transactionModel);
            mongoTemplate.save(transaction);
            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save Transaction", e);

        }
    }

    @Override
    public List<TransactionEntity> findTransactionByFilter(TransactionFilterDTO transactionFilter , ObjectId userId) {
        Query query = new Query();
        List<Criteria>criteriaList = new ArrayList<>();

        if (transactionFilter.getTags() != null && !transactionFilter.getTags().isEmpty()) {
            criteriaList.add(Criteria.where("tags").in(transactionFilter.getTags()));
        }
        if (transactionFilter.getStartDate() != null && transactionFilter.getEndDate() != null) {
            criteriaList.add(Criteria.where("transactionDateTime").gte(transactionFilter.getStartDate()).lte(transactionFilter.getEndDate()));
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
        criteriaList.add(Criteria.where("userId").is(userId));
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
        Instant startDateInstant = startDate.atZone(ZoneOffset.UTC).toInstant();
        Instant endDateInstant = endDate.atZone(ZoneOffset.UTC).toInstant();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(userId)
                        .and("transactionDateTime").gte(startDateInstant).lte(endDateInstant)
                        .and("type").is("expense")),
                Aggregation.project()
                        .and("amount").as("amount")
                        .andExpression("month(transactionDateTime)").as(month) // Extract month
                        .andExpression("dayOfMonth(transactionDateTime)").as("day"), // Extract day of month
                Aggregation.group(Aggregation.fields().and(month).and("day")) // Group by both month and day
                        .sum("amount").as("totalSpent"),
                Aggregation.project("totalSpent")
                        .and("_id.month").as(month) // Rename _id.month to month
                        .and("_id.day").as("day"),   // Rename _id.day to day
                Aggregation.sort(Sort.by(Sort.Direction.ASC, month, "day")) // Sort by month, then day
        );

        AggregationResults<Map<String, Object>> results =
                mongoTemplate.aggregate(aggregation, "transactions", (Class<Map<String, Object>>) (Class<?>) Map.class);
        List<Map<String, Object>> mappedResults = results.getMappedResults();

        // Handling the result and converting the totalSpent to BigDecimal
        for (Map<String, Object> result : mappedResults) {
            // Assuming the totalSpent is in a field called 'totalSpent'
            Object totalSpent = result.get("totalSpent");

            if (totalSpent instanceof Number) {
                BigDecimal bigDecimalTotalSpent = new BigDecimal(totalSpent.toString());
                result.put("totalSpent", bigDecimalTotalSpent); // Update the totalSpent field with BigDecimal
            }
        }


        return mappedResults;
    }





    // Income vs Expenses Summary
    @Override
    public Map<String, BigDecimal> getIncomeVsExpense(ObjectId userId, LocalDateTime startDate, LocalDateTime endDate) {
        Instant startDateInstant = startDate.atZone(ZoneOffset.UTC).toInstant();
        Instant endDateInstant = endDate.atZone(ZoneOffset.UTC).toInstant();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("userId").is(userId)
                        .and("transactionDateTime").gte(startDateInstant).lte(endDateInstant)),
                Aggregation.group("type").sum("amount").as("total")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "transactions", Map.class);
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
        Instant startDateInstant = startDate.atZone(ZoneOffset.UTC).toInstant();
        Instant endDateInstant = endDate.atZone(ZoneOffset.UTC).toInstant();

        Criteria criteria = Criteria.where("userId").is(userId)
                .and("transactionDateTime").gte(startDateInstant).lte(endDateInstant);

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
        // Create session with options
        try (ClientSession session = mongoClient.startSession(ClientSessionOptions.builder().causallyConsistent(true).build())) {
            session.startTransaction(); // Start the transaction

            MongoTemplate sessionTemplate = mongoTemplate.withSession(session);
            for (TransactionEntity transaction : transactions) {
                sessionTemplate.insert(transaction); // Insert each entity one by one
            }

            session.commitTransaction(); // Commit the transaction
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save all transactions", e);
        }
    }

    @Override
    public List<TransactionEntity> findByUserIdAndDateAfter(ObjectId userId, int months, String category,String type) {
        LocalDateTime fromDate = LocalDateTime.now().minusMonths(months);

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                        .and("type").is(type)
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

    @Override
    public List<TransactionEntity> findRecurringTransactions(LocalDateTime now) {
        Query query = new Query();
        query.addCriteria(Criteria.where("recurring").is(true)
                .and("nextOccurrence").lte(now)
                .and("status").ne(Status.SUCCESS));
        return mongoTemplate.find(query, TransactionEntity.class);
    }

    @Override
    public boolean updateSuccessStatus(ObjectId id, Status status, LocalDateTime updatedAt) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update()
                .set("status", status)
                .set("updatedAt", updatedAt);
        var updateResult = mongoTemplate.updateFirst(query, update, TransactionEntity.class);

        return updateResult.getModifiedCount()>0;
    }

    @Override
    public List<TransactionEntity> findMissedRecurringTransactions(LocalDateTime now) {
        // Define the criteria to find missed recurring transactions
        Query query = new Query()
                .addCriteria(Criteria.where("recurring").is(true))
                .addCriteria(Criteria.where("nextOccurrence").lt(now))
                .addCriteria(Criteria.where("status").ne(Status.SUCCESS));  // Ensure it is not already successful

        return mongoTemplate.find(query, TransactionEntity.class);
    }

    @Override
    public List<TransactionEntity> findRecurringUpcomingTransactions(LocalDateTime now) {
        // Adjust 'now' to be the start of tomorrow (00:00 AM)
        LocalDateTime startOfTomorrow = now.plusDays(1).toLocalDate().atStartOfDay();
        // Calculate the start of the day after tomorrow (to limit the range)
        LocalDateTime startOfDayAfterTomorrow = startOfTomorrow.plusDays(1);


        // Define the criteria to find recurring transactions scheduled only for tomorrow
        Query query = new Query()
                .addCriteria(Criteria.where("recurring").is(true))
                .addCriteria(Criteria.where("nextOccurrence").gte(startOfTomorrow)
                        .lt(startOfDayAfterTomorrow))  // Exclude future dates beyond tomorrow
                .addCriteria(Criteria.where("status").ne(Status.SUCCESS));  // Ensure it has not been marked successful yet

        List<TransactionEntity> transactions = mongoTemplate.find(query, TransactionEntity.class);


        return transactions;
    }

    @Override
    public void deleteAll() {
        mongoTemplate.remove(new Query(), TransactionEntity.class);
    }

    @Override
    public List<TransactionEntity> findAll() {
        return mongoTemplate.findAll(TransactionEntity.class);
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
//                .id(transactionModel.getId())
//                .userId(transactionModel.getUserId())
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
