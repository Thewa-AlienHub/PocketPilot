package org.example.pocketpilot.dto.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDTO {

    private String type;
    private BigDecimal amount;
    private String category;
    private List<String> tags;
    private boolean recurring;
    private LocalDateTime transactionDateTime;
    private String recurrencePattern;
}
