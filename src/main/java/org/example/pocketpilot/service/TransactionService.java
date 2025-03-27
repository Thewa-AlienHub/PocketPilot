package org.example.pocketpilot.service;


import org.bson.types.ObjectId;
import org.example.pocketpilot.dto.requestDTO.TransactionRequestDTO;
import org.example.pocketpilot.dto.TransactionFilterDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;

public interface TransactionService {


    ResponseEntity<Object> addTransaction(TransactionRequestDTO dto);

    ResponseEntity<Object> getFilteredTransactions(TransactionFilterDTO dto);

    ResponseEntity<Object> updateTransactions(ObjectId id, TransactionRequestDTO dto);

    ResponseEntity<Object> deleteTransactions(ObjectId id);

    ResponseEntity<Object> getTransactionById(ObjectId id);

    @Scheduled(cron = "0 0 0 * * ?")// Runs every day at 12:00 AM
    void processRecurringTransactions();
}
