package org.example.pocketpilot.repository;

import io.micrometer.common.KeyValues;
import org.bson.types.ObjectId;
import org.example.pocketpilot.dto.TransactionFilterDTO;
import org.example.pocketpilot.entities.TransactionEntity;
import org.example.pocketpilot.model.TransactionModel;
import org.example.pocketpilot.model.UserModel;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    boolean save(TransactionModel transactionModel);

    List<TransactionEntity>findTransactionByFilter(TransactionFilterDTO transactionFilter);

    Optional<TransactionEntity> findById(ObjectId id);

    boolean updateTransaction(ObjectId id, TransactionEntity updatedTransaction);

    boolean deleteTransaction(ObjectId id);
}
