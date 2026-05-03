package com.luvina.la.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luvina.la.dto.CertificationDTO;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Đối tượng phản hồi cho API nhân viên (thêm, sửa, lấy chi tiết).
 * 
 * @author tranledat
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private String code;
    private MessageResponse message;

    private Long employeeId;
    private String employeeLoginId;
    private String employeeName;
    private String employeeNameKana;
    private String employeeBirthDate;
    private String employeeEmail;
    private String employeeTelephone;
    private String departmentId;
    private String departmentName;
    private List<CertificationDTO> certifications;
}
