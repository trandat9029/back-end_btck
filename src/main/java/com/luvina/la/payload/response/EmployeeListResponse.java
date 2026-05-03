/**
 * Copyright(C) 2026 Luvina
 * [EmployeeListResponse.java], 23/04/2026 tranledat
 */
package com.luvina.la.payload.response;

import com.luvina.la.dto.EmployeeDTO;
import java.util.List;
import lombok.Data;

/**
 * Đối tượng phản hồi chứa danh sách nhân viên và thông tin tổng số bản ghi.
 * 
 * @author tranledat
 */
@Data
public class EmployeeListResponse {
    private Integer code;
    private String message;
    private Long totalRecords;
    private List<EmployeeDTO> employees;
}
