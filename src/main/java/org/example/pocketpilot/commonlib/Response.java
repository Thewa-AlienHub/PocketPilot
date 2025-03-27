package org.example.pocketpilot.commonlib;

import lombok.*;
import org.example.pocketpilot.enums.common.ResponseMessage;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor

public class Response {
    private String message;
    private Object data;
    private String requestId;
    private String code;
    private HttpStatus status;

    public Response() {
        this.status = HttpStatus.OK;
    }

    public Response(String message) {
        this.status = HttpStatus.OK;
        this.message = message;
    }

    public Response(ResponseMessage message) {
        this(message.toString());
    }

    public Response(ResponseMessage message, HttpStatus httpStatus) {
        this(message.toString());
        this.status = httpStatus;
        this.code = message.getErrorCode().toString();
    }

    public Response(ResponseMessage message, Object data) {
        this(message.toString(), data);
    }

    public Response(String requestId, ResponseMessage message, Object data) {
        this(message.toString(), data);
        this.requestId = requestId;
    }

    public Response(String requestId, ResponseMessage message) {
        this(message.toString());
        this.requestId = requestId;
    }

    public Response(String requestId, String message, ResponseMessage responseMessage) {
        this.status = HttpStatus.OK;
        this.requestId = requestId;
        this.message = message;
        this.code = responseMessage.getErrorCode().toString();
    }

    private Response(String message, Object data) {
        this.status = HttpStatus.OK;
        this.message = message;
        this.data = data;
    }

    public Response(HttpStatus httpStatus, String message, Object data) {
        this.status = httpStatus;
        this.message = message;
        this.data = data;
    }

    public Response(ResponseMessage responseMessage, String message, Object data) {
        this.code = responseMessage.getErrorCode().toString();
        this.message = message;
        this.data = data;
    }

    public Response(ResponseMessage responseMessage, HttpStatus httpStatus, String message) {
        this.code = responseMessage.getErrorCode().toString();
        this.message = message;
        this.status = httpStatus;
    }


    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
