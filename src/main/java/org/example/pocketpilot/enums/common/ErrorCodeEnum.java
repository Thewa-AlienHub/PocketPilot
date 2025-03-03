package org.example.pocketpilot.enums.common;

public enum ErrorCodeEnum {
    SUCCESS("200"),
    NOTFOUND("401");

    private String value;

    private ErrorCodeEnum(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
