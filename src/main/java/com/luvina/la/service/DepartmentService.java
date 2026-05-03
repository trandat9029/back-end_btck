/**
 * Copyright(C) 2026 Luvina
 * [DepartmentService.java], 09/04/2026 tranledat
 */
package com.luvina.la.service;

import com.luvina.la.dto.DepartmentDTO;
import java.util.List;

/**
 * Interface Service cung cấp các nghiệp vụ liên quan đến phòng ban.
 * 
 * @author tranledat
 */
public interface DepartmentService {

    /**
     * Lấy danh sách tất cả phòng ban.
     * 
     * @return Danh sách các DTO phòng ban
     */
    List<DepartmentDTO> getDepartments();

    /**
     * Kiểm tra sự tồn tại của phòng ban dựa trên ID.
     * 
     * @param id ID của phòng ban cần kiểm tra
     * @return true nếu tồn tại, ngược lại false
     */
    boolean checkExistsDepartmentById(Long id);
}
