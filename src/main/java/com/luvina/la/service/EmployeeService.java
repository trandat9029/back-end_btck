/**
 * Copyright(C) 2026 Luvina
 * [EmployeeService.java], 23/04/2026 tranledat
 */
package com.luvina.la.service;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.payload.response.EmployeeResponse;
import java.util.List;

/**
 * Interface Service định nghĩa các nghiệp vụ liên quan đến nhân viên.
 * 
 * @author tranledat
 */
public interface EmployeeService {

    /**
     * Lấy tổng số nhân viên theo các điều kiện lọc.
     * 
     * @param employeeName tên nhân viên cần tìm kiếm
     * @param departmentId ID phòng ban
     * @return Tổng số nhân viên tìm được
     */
    Long getTotalRecords(String employeeName, Long departmentId);

    /**
     * Lấy danh sách nhân viên theo các điều kiện lọc, tìm kiếm và sắp xếp.
     */
    List<EmployeeDTO> getEmployees(String employeeName, Long departmentId, String ordEmployeeName,
            String ordCertificationName, String ordEndDate, Integer offset, Integer limit);

    /**
     * Thực hiện thêm mới nhân viên.
     */
    void addEmployee(EmployeeRequest employeeRequest);

    /**
     * Lấy thông tin chi tiết nhân viên theo ID.
     */
    EmployeeResponse getEmployeeDetailById(Long employeeId);

    /**
     * Xóa nhân viên theo ID.
     */
    void deleteEmployee(Long employeeId);

    /**
     * Thực hiện cập nhật thông tin nhân viên.
     */
    void updateEmployee(EmployeeRequest employeeRequest);

    /**
     * Kiểm tra tồn tại nhân viên theo ID.
     */
    boolean checkExistsEmployeeById(Long employeeId);

    /**
     * Kiểm tra tồn tại login id (trừ nhân viên hiện tại nếu có).
     */
    boolean checkExistsEmployeeByLoginId(String loginId, Long employeeId);
}
