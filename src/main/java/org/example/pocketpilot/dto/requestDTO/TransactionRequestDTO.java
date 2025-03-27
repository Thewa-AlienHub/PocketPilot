package org.example.pocketpilot.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDTO {

    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "^(income|expense)$", message = "Transaction type must be 'income' or 'expense'")
    private String type;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Min(value = 1, message = "Invalid category ID")
    private int category;

    @Size(max = 10, message = "Tags list can have at most 10 items")
    private List<String> tags;

    private boolean recurring;

    @NotNull(message = "Transaction date is required")
    @PastOrPresent(message = "Transaction date cannot be in the future")
    private LocalDateTime transactionDateTime;

    @Pattern(regexp = "^(daily|weekly|monthly|yearly)?$", message = "Invalid recurrence pattern")
    private String recurrencePattern;
}
