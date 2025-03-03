package org.example.pocketpilot.commonlib.Controller;

import org.example.pocketpilot.commonlib.ErrorMessage;
import org.example.pocketpilot.commonlib.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseController {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    public ResponseController() {
    }

    protected ResponseEntity<Object> sendResponse(Object object) {
        if (object instanceof Response) {
            Response response = (Response)object;
            return this.sendResponse(response, response.getStatus());
        } else if (object instanceof ErrorMessage) {
            ErrorMessage err = (ErrorMessage)object;
            return this.sendResponse(object, err.getError());
        } else {
            return this.sendResponse(object, HttpStatus.OK);
        }
    }

    protected ResponseEntity<Object> sendResponse(Response error) {
        return this.sendResponse(error, error.getStatus());
    }

    protected ResponseEntity<Object> sendResponse(ErrorMessage error) {
        return this.sendResponse(error, error.getError());
    }

    public ResponseEntity<Object> sendResponse(ResponseEntity<String> response) {
        return ((ResponseEntity.BodyBuilder)ResponseEntity.status(response.getStatusCode()).header("Content-Type", new String[]{"application/json"})).body(response.getBody());
    }

    private ResponseEntity<Object> sendResponse(Object object, HttpStatus httpStatus) {
        return ((ResponseEntity.BodyBuilder)ResponseEntity.status(httpStatus).header("Content-Type", new String[]{"application/json"})).body(object);
    }
}
