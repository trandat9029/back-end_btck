/**
 * Copyright(C) 2026 Luvina
 * [DepartmentListResponse.java], 09/04/2026 tranledat
 */
package com.luvina.la.payload.response;

import com.luvina.la.dto.DepartmentDTO;
import java.util.List;
import lombok.Data;

/**
 * Đối tượng phản hồi chứa danh sách phòng ban.
 * 
 * @author tranledat
 */
@Data
public class DepartmentListResponse {
    private Integer code;
    private String message;
    private List<DepartmentDTO> departments;
}
