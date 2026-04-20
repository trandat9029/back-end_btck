/**
 * Copyright(C) 2026 Luvina
 * [DepartmentDTO.java], 13/04/2026 tranledat
 */
package com.luvina.la.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO luu thong tin phong ban.
 *
 * @author tranledat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    private Long departmentId;
    private String departmentName;
}
