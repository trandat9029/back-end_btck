/**
 * Copyright(C) 2026 - Luvina
 * [DepartmentListResponse.java], 24/04/2026 [tranledat]
 */
package com.luvina.la.payload.response;

import com.luvina.la.dto.DepartmentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Đối tượng phản hồi chứa danh sách phòng ban.
 * @author tranledat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentListResponse {
    private String code;
    private String message;
    private List<DepartmentDTO> departments;
}
