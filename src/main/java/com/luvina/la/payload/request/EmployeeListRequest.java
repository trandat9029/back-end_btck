/**
 * Copyright(C) 2026 Luvina
 * [EmployeeListRequest.java], 14/04/2026 tranledat
 */
package com.luvina.la.payload.request;

import com.luvina.la.constant.MessageCode;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * Request DTO chứa các tham số tìm kiếm và phân trang nhân viên.
 * 
 * @author tranledat
 */
@Data
public class EmployeeListRequest {

    private String employeeName;
    private Long departmentId;

    @Pattern(regexp = "^(ASC|DESC)$", message = MessageCode.MSG_CODE_ER021)
    private String ordEmployeeName;

    @Pattern(regexp = "^(ASC|DESC)$", message = MessageCode.MSG_CODE_ER021)
    private String ordCertificationName;

    @Pattern(regexp = "^(ASC|DESC)$", message = MessageCode.MSG_CODE_ER021)
    private String ordEndDate;

    @Min(value = 0, message = MessageCode.MSG_CODE_ER018)
    private Integer offset;

    @Min(value = 1, message = MessageCode.MSG_CODE_ER018)
    private Integer limit;
}
