/**
 * Copyright(C) 2026 Luvina
 * [ResourceNotFoundException.java], 03/05/2026 tranledat
 */
package com.luvina.la.exception;

import com.luvina.la.payload.response.MessageResponse;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

/**
 * Ngoại lệ xảy ra khi không tìm thấy dữ liệu.
 */
public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String code) {
        super(MessageResponse.builder()
                .code(code)
                .params(new ArrayList<>())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
