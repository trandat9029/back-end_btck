package com.luvina.la.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO nhận dữ liệu từ form ADM004 gửi lên để validate hoặc lưu.
 *
 * @author tranledat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDTO implements Serializable {

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
    private String score;
}
