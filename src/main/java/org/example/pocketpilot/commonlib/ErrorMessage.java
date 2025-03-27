package org.example.pocketpilot.commonlib;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.pocketpilot.enums.common.ErrorCodeEnum;
import org.example.pocketpilot.enums.common.ResponseMessage;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Setter
public class ErrorMessage {

    private String message;
    private String title;
    private HttpStatus error;
    private String code;
    private String timestamp;
    private Object data;
    private String requestId;
    @JsonIgnore
    private String responseString;

    public ErrorMessage() {
    }

    public ErrorMessage(String message, HttpStatus error, ErrorCodeEnum code) {
        this.message = message;
        this.error = error;
        this.code = code.toString();
        this.timestamp = (new Date()).toString();
    }

    public ErrorMessage(String requestId, String message, HttpStatus error, ErrorCodeEnum code) {
        this(message, error, code);
        this.requestId = requestId;
    }

    public ErrorMessage(ResponseMessage message, HttpStatus error) {
        this(message.toString(), error, message.getErrorCode());
    }

    public ErrorMessage(String requestId, ResponseMessage message, HttpStatus error) {
        this(message.toString(), error, message.getErrorCode());
        this.requestId = requestId;
    }

    public ErrorMessage(HttpStatus Status, String message) {
        this.message = message;
        this.error = Status;
    }


    public HttpStatus getError() {
        return error;
    }

    public void setError(HttpStatus error) {
        this.error = error;
    }
}
