package org.example.pocketpilot.dto.requestDTO;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FinancialReportRequestDTO {


    @NotNull(message = "Start date is required.")
    @PastOrPresent(message = "Start date must be in the past or present.")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required.")
    private LocalDateTime endDate;

    @Size(max = 10, message = "You can provide up to 10 categories.")
    private List<String> categories;

    @Size(max = 10, message = "You can provide up to 10 tags.")
    private List<String> tags;

}
