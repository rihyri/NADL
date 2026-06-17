package com.rihyri.NADL.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private final int status;
    private final String code;
    private final String message;

    private ErrorResponse(HttpStatus status, String code, String message) {
        this.status = status.value();
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getStatus(),
                errorCode.name(),
                errorCode.getMessage()
        );
    }

    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status, "VALIDATION_ERROR", message);
    }
}
