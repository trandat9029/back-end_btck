/**
 * Copyright(C) 2026 Luvina
 * [EmployeeDTO.java], 13/04/2026 tranledat
 */
package com.luvina.la.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO luu thong tin co ban cua nhan vien.
 *
 * @author tranledat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO implements Serializable {

    private static final long serialVersionUID = 6868189362900231672L;

    private Long employeeId;
    private Long departmentId;
    private String departmentName;
    private String employeeName;
    private String employeeNameKana;
    private LocalDate employeeBirthDate;
    private String employeeEmail;
    private String employeeTelephone;
    private String employeeLoginId;
    private String employeeLoginPassword;
    private String certificationName;
    private LocalDate endDate;
    private Double score;

    /**
     * DTO thong tin danh sach nhan vien cho API ADM002.
     *
     * @param employeeId Ma nhan vien.
     * @param employeeName Ten nhan vien.
     * @param employeeBirthDate Ngay sinh nhan vien.
     * @param departmentName Ten phong ban.
     * @param employeeEmail Email nhan vien.
     * @param employeeTelephone So dien thoai nhan vien.
     * @param certificationName Ten chung chi.
     * @param endDate Ngay het han chung chi.
     * @param score Diem chung chi.
     */
    public EmployeeDTO(Long employeeId,
                       String employeeName,
                       LocalDate employeeBirthDate,
                       Long departmentId,
                       String departmentName,
                       String employeeEmail,
                       String employeeTelephone,
                       String employeeNameKana,
                       String employeeLoginId,
                       String certificationName,
                       LocalDate endDate,
                       Double score) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeBirthDate = employeeBirthDate;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.employeeEmail = employeeEmail;
        this.employeeTelephone = employeeTelephone;
        this.employeeNameKana = employeeNameKana;
        this.employeeLoginId = employeeLoginId;
        this.certificationName = certificationName;
        this.endDate = endDate;
        this.score = score;
    }
}
