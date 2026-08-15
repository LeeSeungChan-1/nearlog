package com.nearlog.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse>
    handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {

        ErrorCode errorCode =
                exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(
                        ErrorResponse.of(
                                errorCode,
                                request.getRequestURI()
                        )
                );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ErrorResponse>
    handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        List<ErrorResponse.FieldViolation> errors =
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error ->
                                new ErrorResponse.FieldViolation(
                                        error.getField(),
                                        error.getDefaultMessage()
                                )
                        )
                        .toList();

        ErrorResponse response =
                new ErrorResponse(
                        Instant.now(),
                        400,
                        "COMMON_001",
                        "입력값을 확인해주세요.",
                        request.getRequestURI(),
                        errors
                );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
    handleException(
            Exception exception,
            HttpServletRequest request
    ) {

        log.error(
                "Unhandled exception",
                exception
        );

        ErrorCode errorCode =
                ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(
                        ErrorResponse.of(
                                errorCode,
                                request.getRequestURI()
                        )
                );
    }
}