/**
 * Copyright(C) 2026 Luvina
 * [GlobalExceptionHandler.java], 03/05/2026 tranledat
 */
package com.luvina.la.exception;

import com.luvina.la.constant.MessageCode;
import com.luvina.la.payload.response.EmployeeResponse;
import com.luvina.la.payload.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;

/**
 * Xử lý các ngoại lệ toàn cục cho ứng dụng.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý các ngoại lệ được kế thừa từ BaseException.
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<EmployeeResponse> handleBaseException(BaseException e) {
        EmployeeResponse response = EmployeeResponse.builder()
                .code(String.valueOf(e.getHttpStatus().value()))
                .message(e.getMessageResponse())
                .build();
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    /**
     * Xử lý các ngoại lệ không xác định khác.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<EmployeeResponse> handleUncaughtException(Exception e) {
        e.printStackTrace(); // Log lỗi cho mục đích debug
        
        EmployeeResponse response = EmployeeResponse.builder()
                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .message(MessageResponse.builder()
                        .code(MessageCode.MSG_CODE_ER015)
                        .params(new ArrayList<>())
                        .build())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
