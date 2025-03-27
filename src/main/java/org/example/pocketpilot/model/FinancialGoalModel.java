package org.example.pocketpilot.model;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FinancialGoalModel {
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
