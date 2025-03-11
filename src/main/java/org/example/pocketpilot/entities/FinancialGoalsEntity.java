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

@Data
@Document(collection = "Financial_Goals")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialGoalsEntity {
    @Id
    private ObjectId id;
    private ObjectId userId;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDateTime deadLine;
    private boolean autoAllocate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
