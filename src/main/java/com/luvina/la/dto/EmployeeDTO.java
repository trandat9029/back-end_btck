/**
 * Copyright(C) 2026 Luvina
 * [EmployeeDTO.java], 13/04/2026 tranledat
 */
package com.luvina.la.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Đối tượng vận chuyển dữ liệu (DTO) cho thông tin nhân viên hiển thị tại màn ADM002.
 * 
 * @author tranledat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO implements Serializable {

    private static final long serialVersionUID = 6868189362900231672L;

    private Long employeeId;
    private String employeeName;
    private Date employeeBirthDate;
    private String departmentName;
    private String employeeEmail;
    private String employeeTelephone;
    private String certificationName;
    private Date endDate;
    private BigDecimal score;
}
