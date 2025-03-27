package org.example.pocketpilot.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TransactionFilterDTO {
    private List<String> tags;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String type;
    private String category;
}