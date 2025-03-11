package org.example.pocketpilot.dto.RequestDTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FinancialReportRequestDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> categories;
    private List<String> tags;

}
