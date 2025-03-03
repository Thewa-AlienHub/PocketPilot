package org.example.pocketpilot.repository.Impl;

import org.bson.types.ObjectId;
import org.example.pocketpilot.dto.TransactionFilterDTO;
import org.example.pocketpilot.entities.TransactionEntity;
import org.example.pocketpilot.entities.UserEntity;
import org.example.pocketpilot.model.TransactionModel;
import org.example.pocketpilot.model.UserModel;
import org.example.pocketpilot.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
