package org.example.pocketpilot.service;


import org.bson.types.ObjectId;
import org.example.pocketpilot.dto.RequestDTO.TransactionRequestDTO;
import org.example.pocketpilot.dto.TransactionFilterDTO;
import org.springframework.http.ResponseEntity;

public interface TransactionService {


    ResponseEntity<Object> addTransaction(TransactionRequestDTO dto);

    ResponseEntity<Object> getFilteredTransactions(TransactionFilterDTO dto);

    ResponseEntity<Object> updateTransactions(ObjectId id, TransactionRequestDTO dto);

    ResponseEntity<Object> deleteTransactions(ObjectId id);

    ResponseEntity<Object> getTransactionById(ObjectId id);
}
