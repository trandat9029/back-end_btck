/**
 * Copyright(C) 2026 - Luvina
 * [UpdateEmployeeResponse.java], 28/04/2026 [tranledat]
 */
package com.luvina.la.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * DTO phản hồi sau khi cập nhật nhân viên thành công hoặc thất bại theo yêu cầu thiết kế.
 * @author tranledat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateEmployeeResponse implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private Long employeeId;
    private MessageDetail message;

    /**
     * Thông tin chi tiết của thông điệp (Nested Object).
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MessageDetail implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private String code;
        private String message; // Trường message chứa nội dung đã dịch
        private List<String> params;
    }

    /**
     * Tạo phản hồi thành công nhanh chóng.
     * @param employeeId ID nhân viên đã cập nhật.
     * @param messageCode Mã thông báo (VD: MSG002).
     * @param messageText Nội dung thông báo đã được dịch.
     * @return Đối tượng UpdateEmployeeResponse.
     */
    public static UpdateEmployeeResponse success(Long employeeId, String messageCode, String messageText) {
        return UpdateEmployeeResponse.builder()
                .code("200")
                .employeeId(employeeId)
                .message(MessageDetail.builder()
                        .code(messageCode)
                        .message(messageText)
                        .params(Collections.emptyList())
                        .build())
                .build();
    }
}
