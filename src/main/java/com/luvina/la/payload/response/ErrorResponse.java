/**
 * Copyright(C) 2026 - Luvina
 * [ErrorResponse.java], 28/04/2026 [tranledat]
 */
package com.luvina.la.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO phản hồi lỗi hệ thống (500) theo đúng yêu cầu thiết kế.
 * Cấu trúc đơn giản: code (500) và thông báo lỗi dạng String.
 * @author tranledat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    private String code; // Mã lỗi (VD: 500)
    private String message; // Thông báo lỗi đơn giản (VD: "Hệ thống có lỗi.")

}
