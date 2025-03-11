package org.example.pocketpilot.dto;

import lombok.Data;
import org.example.pocketpilot.enums.BudgetTypes;

@Data
public class BudgetFilterDTO {
    private int budgetType;
    private int category;
}
