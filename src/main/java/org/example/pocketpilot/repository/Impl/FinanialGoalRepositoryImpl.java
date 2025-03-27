package org.example.pocketpilot.repository.Impl;

import org.bson.types.ObjectId;
import org.example.pocketpilot.entities.FinancialGoalsEntity;
import org.example.pocketpilot.model.FinancialGoalModel;
import org.example.pocketpilot.repository.FinancialGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FinanialGoalRepositoryImpl implements FinancialGoalRepository {

    @Autowired
    private  MongoTemplate mongoTemplate;

    public FinanialGoalRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public boolean save(FinancialGoalModel financialGoalModel) {
        try {
            FinancialGoalsEntity financialGoalsEntity = mapToFinancialGoalEntity(financialGoalModel);
            mongoTemplate.save(financialGoalsEntity);
            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save Financial Goal", e);
        }
    }

    @Override
    public Optional<FinancialGoalModel> findById(ObjectId goalId) {
        Query query = new Query(Criteria.where("_id").is(goalId));
        FinancialGoalsEntity financialGoalsEntity = mongoTemplate.findOne(query, FinancialGoalsEntity.class);

        return financialGoalsEntity != null ? Optional.of(mapToFinancialGoalModel(financialGoalsEntity)) : Optional.empty();
    }

    @Override
    public List<FinancialGoalModel> getAutoAllocatedGoals(ObjectId userId) {
        Query query = new Query(Criteria.where("userId").is(userId).and("autoAllocate").is(true));
        List<FinancialGoalsEntity> goals = mongoTemplate.find(query, FinancialGoalsEntity.class);

        return goals.stream()
                .map(this::mapToFinancialGoalModel)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateGoals(List<FinancialGoalModel> updatedGoals) {
        for (FinancialGoalModel goal : updatedGoals) {
            Query query = new Query(Criteria.where("_id").is(goal.getId()));
            Update update = new Update()
                    .set("currentAmount", goal.getCurrentAmount())
                    .set("updatedAt", goal.getUpdatedAt());
            mongoTemplate.updateFirst(query, update, FinancialGoalsEntity.class);
        }

        if(updatedGoals.size() > 0){
            return true;
        }
        return false;
    }


    private FinancialGoalsEntity mapToFinancialGoalEntity(FinancialGoalModel financialGoalModel) {
        return FinancialGoalsEntity.builder()
                .id(financialGoalModel.getId())
                .userId(financialGoalModel.getUserId())
                .goalName(financialGoalModel.getGoalName())
                .targetAmount(financialGoalModel.getTargetAmount())
                .currentAmount(financialGoalModel.getCurrentAmount())
                .deadLine(financialGoalModel.getDeadLine())
                .autoAllocate(financialGoalModel.isAutoAllocate())
                .createdAt(financialGoalModel.getCreatedAt())
                .updatedAt(financialGoalModel.getUpdatedAt())
                .build();
    }
    private FinancialGoalModel mapToFinancialGoalModel(FinancialGoalsEntity entity) {
        return FinancialGoalModel.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .goalName(entity.getGoalName())
                .targetAmount(entity.getTargetAmount())
                .currentAmount(entity.getCurrentAmount())
                .deadLine(entity.getDeadLine())
                .autoAllocate(entity.isAutoAllocate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
