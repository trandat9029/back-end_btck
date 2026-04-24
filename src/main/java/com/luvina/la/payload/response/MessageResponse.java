/**
 * Copyright(C) 2026 - Luvina
 * [MessageResponse.java], 24/04/2026 [tranledat]
 */
package com.luvina.la.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO phản hồi thông điệp hệ thống hoặc lỗi nghiệp vụ.
 * Hỗ trợ cả thông báo chung và lỗi chi tiết theo trường (field).
 * @author tranledat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {

    private String code;
    private String message;
    private List<String> params;
    private String field;

    /**
     * Constructor cho thông báo thành công hoặc lỗi chung (chỉ có code và message).
     */
    public MessageResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Constructor cho lỗi nghiệp vụ có tham số truyền vào message.
     */
    public MessageResponse(String code, List<String> params, String field) {
        this.code = code;
        this.params = params;
        this.field = field;
    }
}
