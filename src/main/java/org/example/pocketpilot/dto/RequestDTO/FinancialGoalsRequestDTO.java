package org.example.pocketpilot.dto.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialGoalsRequestDTO {
    private String goalName;
    private BigDecimal targetAmount;
    private LocalDateTime deadline;
    private boolean autoAllocate;

}
