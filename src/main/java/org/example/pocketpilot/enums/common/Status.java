package org.example.pocketpilot.enums.common;

import java.util.Arrays;
import java.util.Optional;

public enum Status {
    ACTIVE(1, "Active"),
    WARNING(2, "Warning"),
    EXCEEDED(3, "Exceeded"),
    INACTIVE(4, "Inactive"),
    INITIALIZED(5, "Initialized"),
    SENT(6, "Sent"),
    VIEWED(7, "Viewed"),
    SUCCESS(8, "Success"),
    FAILED(9,"Failed");


    private final int id;
    private final String value;

    Status(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static Optional<Status> fromId(int id) {
        return Arrays.stream(values()).filter(status -> status.id == id).findFirst();
    }
}
