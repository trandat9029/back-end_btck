/**
 * Copyright(C) 2026 - Luvina
 * [ErrorResponse.java], 28/04/2026 [tranledat]
 */
package com.luvina.la.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO phản hồi lỗi hệ thống (500) theo đúng yêu cầu thiết kế.
 * @author tranledat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    private String code;
    private MessageDetail message;

    /**
     * Thông tin chi tiết của lỗi.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MessageDetail {
        private String code;
        private List<String> params;
    }
}
