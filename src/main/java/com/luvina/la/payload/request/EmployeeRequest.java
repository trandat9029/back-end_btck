/**
 * Copyright(C) 2026 Luvina
 * [EmployeeRequest.java], 23/04/2026 tranledat
 */
package com.luvina.la.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Class chứa thông tin nhân viên nhận từ request của màn hình ADM004.
 * Dùng cho cả luồng validate và lưu dữ liệu.
 *
 * @author tranledat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long employeeId;
    private String employeeLoginId;
    private Long departmentId;
    private String employeeName;
    private String employeeNameKana;
    private String employeeBirthDate;
    private String employeeEmail;
    private String employeeTelephone;
    private String employeeLoginPassword;
    private String employeeLoginPasswordConfirm;
    private Long certificationId;
    private String certificationStartDate;
    private String certificationEndDate;
    private String employeeCertificationScore;
}
