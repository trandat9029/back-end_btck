/**
 * Copyright(C) 2026 Luvina
 * [DepartmentService.java], 23/04/2026 tranledat
 */
package com.luvina.la.service;

import com.luvina.la.dto.DepartmentDTO;

import java.util.List;

/**
 * Interface cung cấp các phương thức xử lý nghiệp vụ liên quan đến phòng ban.
 *
 * @author tranledat
 */
public interface DepartmentService {

    /**
     * Lay danh sach tat ca phong ban.
     *
     * @return Danh sach phong ban.
     */
    List<DepartmentDTO> getAllDepartments();

    /**
     * Kiểm tra xem phòng ban có tồn tại trong hệ thống không.
     *
     * @param departmentId ID phòng ban cần kiểm tra.
     * @return true nếu tồn tại, false nếu không.
     */
    boolean checkExistsDepartmentById(Long departmentId);
}
