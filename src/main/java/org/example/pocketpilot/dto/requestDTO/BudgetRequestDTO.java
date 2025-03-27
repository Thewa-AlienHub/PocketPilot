package org.example.pocketpilot.dto.requestDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetRequestDTO {

    @NotBlank(message = "Plan name is required")
    @Size(max = 100, message = "Plan name must be less than 100 characters")
    private String planName;

    @NotNull(message = "Category is required")
    @Positive(message = "Category must be a positive value")
    private int category;

    @NotNull(message = "Category is required")
    @Positive(message = "Category must be a positive value")
    private int budgetType;


    @NotNull(message = "Budget amount is required")
    @DecimalMin(value = "0.01", message = "Budget amount must be greater than zero")
    private BigDecimal budgetAmount;


    private String yearMonth;


    public YearMonth getYearMonth() {
        return YearMonth.parse(yearMonth);
    }

    public void setYearMonth(YearMonth yearMonth) {
        this.yearMonth = yearMonth.toString();
    }

}
