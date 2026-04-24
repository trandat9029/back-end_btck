/**
 * Copyright(C) 2026 Luvina
 * [EmployeeListDTO.java], 13/04/2026 tranledat
 */
package com.luvina.la.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO luu thong tin hien thi tren man hinh danh sach nhan vien.
 *
 * @author tranledat
 */
@Data
@AllArgsConstructor
public class EmployeeListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long employeeId;
    private String employeeName;
    private String employeeBirthDate;
    private String departmentName;
    private String employeeEmail;
    private String employeeTelephone;
    private String certificationName;
    private String endDate;
    private BigDecimal score;
}
