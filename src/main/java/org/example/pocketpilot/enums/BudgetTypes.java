package org.example.pocketpilot.enums;

import org.example.pocketpilot.enums.common.Status;

import java.util.Arrays;
import java.util.Optional;

public enum BudgetTypes {
    MONTHLYWISE(1,"MONTHLYWISE"),
    CATEGORYWISE(2,"CATEGORYWISE");

    private final int id;
    private final String value;

    BudgetTypes(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static Optional<BudgetTypes> fromId(int id) {
        return Arrays.stream(values()).filter(status -> status.id == id).findFirst();
    }
}
