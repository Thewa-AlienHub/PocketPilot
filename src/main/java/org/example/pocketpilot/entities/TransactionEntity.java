package org.example.pocketpilot.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity {
    @Id
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
