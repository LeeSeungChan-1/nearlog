package com.nearlog.common.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(

        Instant timestamp,

        int status,

        String code,

        String message,

        String path,

        List<FieldViolation> errors

) {

    public record FieldViolation(
            String field,
            String message
    ) {
    }

    public static ErrorResponse of(
            ErrorCode errorCode,
            String path
    ) {

        return new ErrorResponse(
                Instant.now(),
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage(),
                path,
                List.of()
        );
    }
}