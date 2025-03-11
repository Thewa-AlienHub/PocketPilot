package org.example.pocketpilot.repository;

import org.bson.types.ObjectId;
import org.example.pocketpilot.model.FinancialGoalModel;
import java.util.List;
import java.util.Optional;

public interface FinancialGoalRepository {


    boolean save(FinancialGoalModel financialGoalModel);

    Optional<FinancialGoalModel> findById(ObjectId goalId);

    List<FinancialGoalModel> getAutoAllocatedGoals(ObjectId userId);

    void updateGoals(List<FinancialGoalModel> updatedGoals);
}
