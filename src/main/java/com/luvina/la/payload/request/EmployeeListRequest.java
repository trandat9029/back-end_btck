/**
 * Copyright(C) 2026 Luvina
 * [EmployeeListRequest.java], 14/04/2026 tranledat
 */
package com.luvina.la.payload.request;

import lombok.Data;

/**
 * Request DTO chứa các tham số tìm kiếm và phân trang nhân viên.
 * 
 * @author tranledat
 */
@Data
public class EmployeeListRequest {

    private String employeeName;
    private Long departmentId;

    private String ordEmployeeName;

    private String ordCertificationName;

    private String ordEndDate;

    private Integer offset;

    private Integer limit;
}
