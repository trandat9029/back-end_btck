/**
 * Copyright(C) 2026 Luvina
 * [CustomException.java], 24/04/2026 tranledat
 */
package com.luvina.la.exception;

import lombok.Getter;

import java.util.List;

/**
 * Exception tùy chỉnh dùng cho các lỗi nghiệp vụ (Business Logic Errors).
 * 
 * @author tranledat
 */
@Getter
public class CustomException extends RuntimeException {
    private final String code;
    private final List<String> params;
    private final String field;

    public CustomException(String code, List<String> params, String field) {
        this.code = code;
        this.params = params;
        this.field = field;
    }

    public CustomException(String code, String field) {
        this(code, null, field);
    }

    public CustomException(String code) {
        this(code, null, null);
    }
}
