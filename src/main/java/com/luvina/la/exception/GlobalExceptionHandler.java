/**
 * Copyright(C) 2026 Luvina
 * [GlobalExceptionHandler.java], 24/04/2026 tranledat
 */
package com.luvina.la.exception;

import com.luvina.la.payload.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Lớp xử lý lỗi tập trung cho toàn bộ ứng dụng.
 * 
 * @author tranledat
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    private final Locale defaultLocale = Locale.JAPANESE;

    /**
     * Bắt và xử lý các lỗi nghiệp vụ chủ động throw từ Service.
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<List<MessageResponse>> handleCustomException(CustomException ex) {
        log.error("Business Error: Code={}, Field={}", ex.getCode(), ex.getField());
        
        String message = messageSource.getMessage(ex.getCode(), 
                ex.getParams() != null ? ex.getParams().toArray() : null, 
                "System Error", 
                defaultLocale);

        MessageResponse response = MessageResponse.builder().code(ex.getCode()).message(message).field(ex.getField()).build();
        return new ResponseEntity<>(Collections.singletonList(response), HttpStatus.BAD_REQUEST);
    }

    /**
     * Bắt các lỗi không xác định (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Unexpected Error: ", ex);
        
        ErrorResponse response = ErrorResponse.builder()
                .code("500")
                .message(ErrorResponse.MessageDetail.builder()
                        .code("ER999") // Hoặc mã lỗi hệ thống tương ứng
                        .params(Collections.emptyList())
                        .build())
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
