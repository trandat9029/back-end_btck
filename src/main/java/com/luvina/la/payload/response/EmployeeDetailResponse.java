/**
 * Copyright(C) [2026] - Luvina
 * EmployeeDetailResponse.java, [24/04/2026] [tranledat]
 */
package com.luvina.la.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Payload trả về thông tin chi tiết của một nhân viên
 * @author tranledat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailResponse {
    private String code;
    private Long employeeId;
    private String employeeName;
    private String employeeBirthDate;
    private Long departmentId;
    private String departmentName;
    private String employeeEmail;
    private String employeeTelephone;
    private String employeeNameKana;
    private String employeeLoginId;
    private List<EmployeeCertificationDetailResponse> certifications;

    /**
     * Thông tin chi tiết chứng chỉ của nhân viên
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmployeeCertificationDetailResponse {
        private Long certificationId;
        private String certificationName;
        private String startDate;
        private String endDate;
        private Integer score;
    }
}
