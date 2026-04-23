/**
 * Copyright(C) 2026 Luvina
 * [EmployeeListResponse.java], 23/04/2026 tranledat
 */
package com.luvina.la.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Payload chứa thông tin phản hồi từ API nhân viên.
 * Dùng chung cho cả API lấy danh sách, validate và thêm/sửa nhân viên.
 *
 * @author tranledat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeListResponse {

    @Data
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }

    private String code;
    private String message;
    private List<String> params = new ArrayList<>();
    private Long totalRecords;
    private List<EmployeeDTO> employees;
    private List<FieldError> fieldErrors;
}
