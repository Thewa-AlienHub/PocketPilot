package org.example.pocketpilot.model;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TransactionModel {
    private ObjectId id;
    private ObjectId userId;
    private String type;
    private BigDecimal amount;
    private String category;
    private List<String> tags;
    private LocalDateTime transactionDateTime;
    private boolean recurring;
    private String recurrencePattern;
    private LocalDateTime nextOccurrence;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
