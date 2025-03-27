package org.example.pocketpilot.model;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Data
@Builder
public class BudgetModel {
    private ObjectId id;
    private String planName;
    private ObjectId userId;
    private String budgetType;
    private String category;
    private BigDecimal budgetAmount;
    private BigDecimal spentAmount;
    private YearMonth yearMonth;
    private String Status;
}
