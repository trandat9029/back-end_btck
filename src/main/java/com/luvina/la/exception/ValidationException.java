/**
 * Copyright(C) 2026 Luvina
 * [ValidationException.java], 03/05/2026 tranledat
 */
package com.luvina.la.exception;

import com.luvina.la.payload.response.MessageResponse;
import org.springframework.http.HttpStatus;

/**
 * Ngoại lệ xảy ra khi validate dữ liệu thất bại.
 */
public class ValidationException extends BaseException {
    public ValidationException(MessageResponse messageResponse) {
        super(messageResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
