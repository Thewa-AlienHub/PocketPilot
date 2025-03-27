package org.example.pocketpilot.enums.common;

public enum ResponseMessage {
    SUCCESS("Success", ErrorCodeEnum.SUCCESS),
    NOTFOUND("Failed",ErrorCodeEnum.NOTFOUND);




    private final String message;
    private final ErrorCodeEnum errorCode;

    private ResponseMessage(String message, ErrorCodeEnum errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public ErrorCodeEnum getErrorCode() {
        return this.errorCode;
    }

    public String toString() {
        return this.message;
    }


}
