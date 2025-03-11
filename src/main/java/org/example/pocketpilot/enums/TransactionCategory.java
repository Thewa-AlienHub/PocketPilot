package org.example.pocketpilot.enums;

import java.util.Arrays;
import java.util.Optional;

public enum TransactionCategory {
    SALARY(1, "Salary"),
    FOOD(2, "Food"),
    TRAVEL(3, "Travel"),
    ENTERTAINMENT(4, "Entertainment"),
    BILLS(5, "Bills"),
    GOALS(6, "Goals");

    private final int id;
    private final String value;

    TransactionCategory(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static Optional<TransactionCategory> fromId(int id) {
        return Arrays.stream(values()).filter(cat -> cat.id == id).findFirst();
    }
    public static Optional<TransactionCategory> fromValue(String value) {
        return Arrays.stream(values())
                .filter(cat -> cat.value.equalsIgnoreCase(value))
                .findFirst();
    }
}
