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
public class UpdateEmployeeResponse {

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
    public static class MessageDetail {
        private String code;
        private List<String> params;
    }

    /**
     * Tạo phản hồi thành công nhanh chóng.
     * @param employeeId ID nhân viên đã cập nhật.
     * @param messageCode Mã thông báo (VD: MSG002).
     * @return Đối tượng UpdateEmployeeResponse.
     */
    public static UpdateEmployeeResponse success(Long employeeId, String messageCode) {
        return UpdateEmployeeResponse.builder()
                .code("200")
                .employeeId(employeeId)
                .message(new MessageDetail(messageCode, Collections.emptyList()))
                .build();
    }
}
