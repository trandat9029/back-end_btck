/**
 * Copyright(C) 2026 Luvina
 * [EmployeeService.java], 13/04/2026 tranledat
 */
package com.luvina.la.service;

import com.luvina.la.dto.EmployeeDTO;

import java.util.List;

/**
 * Service xu ly nghiep vu lien quan den nhan vien.
 *
 * @author tranledat
 */
public interface EmployeeService {

    /**
     * Lay danh sach nhan vien theo dieu kien tim kiem, sap xep va phan trang.
     *
     * @param employeeName Ten nhan vien dung de tim kiem.
     * @param departmentId Ma phong ban dung de loc.
     * @param sortEmployeeName Thu tu sap xep theo ten nhan vien.
     * @param sortCertificationName Thu tu sap xep theo ten chung chi.
     * @param sortEndDate Thu tu sap xep theo ngay het han chung chi.
     * @param limit So luong ban ghi tren mot trang.
     * @param offset Vi tri bat dau ban ghi.
     * @return Danh sach nhan vien.
     */
    List<EmployeeDTO> getListEmployee(
            String employeeName,
            Long departmentId,
            String sortEmployeeName,
            String sortCertificationName,
            String sortEndDate,
            Integer limit,
            Integer offset
    );

    /**
     * Dem tong so nhan vien thoa dieu kien tim kiem.
     *
     * @param employeeName Ten nhan vien dung de tim kiem.
     * @param departmentId Ma phong ban dung de loc.
     * @return Tong so nhan vien thoa dieu kien.
     */
    Long countEmployeesWithFilter(String employeeName, Long departmentId);

    /**
     * Kiểm tra xem tài khoản (loginId) đã tồn tại trong hệ thống chưa.
     * Dùng để validate ER003.
     *
     * @param loginId Tên tài khoản cần kiểm tra.
     * @param employeeId ID của nhân viên hiện tại (nếu là update) để loại trừ.
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    boolean checkExistsEmployeeByLoginId(String loginId, Long employeeId);
}
