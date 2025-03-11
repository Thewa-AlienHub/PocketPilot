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
import java.time.YearMonth;

@Data
@Document(collection = "Budget")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetEntity {
    @Id
    private ObjectId id;
    private String planName;
    private ObjectId userId;
    private String category;
    private int budgetType;
    private BigDecimal budgetAmount;
    private BigDecimal spentAmount;
    private YearMonth yearMonth;
    private int Status;

}
