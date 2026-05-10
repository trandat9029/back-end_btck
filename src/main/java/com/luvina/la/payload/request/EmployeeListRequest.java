/**
 * Copyright(C) 2026 Luvina Software
 * EmployeeListRequest.java, 14/04/2026 tranledat
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

    /** Tên nhân viên dùng để tìm kiếm */
    private String employeeNameSearch;

    /** ID phòng ban dùng để lọc */
    private Long departmentIdFilter;

    /** Trạng thái sắp xếp theo tên nhân viên (ASC/DESC) */
    private String employeeNameSort;

    /** Trạng thái sắp xếp theo tên chứng chỉ (ASC/DESC) */
    private String certificationNameSort;

    /** Trạng thái sắp xếp theo ngày hết hạn chứng chỉ (ASC/DESC) */
    private String endDateSort;

    /** Vị trí bắt đầu lấy dữ liệu (phân trang) */
    private Integer offset;

    /** Số lượng bản ghi tối đa trên một trang */
    private Integer limit;
}
