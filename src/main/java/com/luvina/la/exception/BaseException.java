/**
 * Copyright(C) 2026 Luvina
 * [BaseException.java], 03/05/2026 tranledat
 */
package com.luvina.la.exception;

import com.luvina.la.payload.response.MessageResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp ngoại lệ cơ sở cho toàn bộ ứng dụng.
 */
@Getter
public class BaseException extends RuntimeException {
    private final MessageResponse messageResponse;
    private final HttpStatus httpStatus;

    public BaseException(MessageResponse messageResponse, HttpStatus httpStatus) {
        super(messageResponse.getCode());
        this.messageResponse = messageResponse;
        this.httpStatus = httpStatus;
    }

    public BaseException(String code, HttpStatus httpStatus) {
        super(code);
        this.messageResponse = MessageResponse.builder()
                .code(code)
                .params(new ArrayList<>())
                .build();
        this.httpStatus = httpStatus;
    }

    public BaseException(String code, String param, HttpStatus httpStatus) {
        super(code);
        this.messageResponse = MessageResponse.builder()
                .code(code)
                .params(List.of(param))
                .build();
        this.httpStatus = httpStatus;
    }
}
