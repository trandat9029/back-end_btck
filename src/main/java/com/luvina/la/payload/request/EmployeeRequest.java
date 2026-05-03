/**
 * Copyright(C) 2026 Luvina
 * [EmployeeRequest.java], 23/04/2026 tranledat
 */
package com.luvina.la.payload.request;

import lombok.Data;

/**
 * Class chứa thông tin nhân viên nhận từ request của màn hình ADM004/ADM005.
 *
 * @author tranledat
 */
@Data
public class EmployeeRequest {
    private Long employeeId;
    private String employeeLoginId;
    private String employeeName;
    private String employeeNameKana;
    private String employeeBirthDate;
    private String employeeEmail;
    private String employeeTelephone;
    private String employeeLoginPassword;
    private String departmentId;
    private CertificationRequest certificationRequest;
}
