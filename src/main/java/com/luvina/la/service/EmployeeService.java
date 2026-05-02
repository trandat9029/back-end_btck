/**
 * Copyright(C) 2026 Luvina
 * [EmployeeService.java], 23/04/2026 tranledat
 */
package com.luvina.la.service;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.payload.response.EmployeeDetailResponse;
import java.util.List;

/**
 * Interface cung cấp các phương thức xử lý nghiệp vụ liên quan đến nhân viên.
 * Bao gồm tìm kiếm, đếm số lượng, kiểm tra tồn tại và thêm mới nhân viên.
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

    /**
     * Kiểm tra xem ID nhân viên có tồn tại trong hệ thống hay không.
     * Dùng để validate ER013.
     * 
     * @param employeeId ID nhân viên cần kiểm tra.
     * @return true nếu tồn tại, false nếu không.
     */
    boolean checkExistsEmployeeById(Long employeeId);

    /**
     * Thêm mới nhân viên.
     * Bao gồm cả việc lưu thông tin chứng chỉ vào bảng employees_certifications.
     *
     * @param request Dữ liệu nhân viên từ request.
     * @return ID của nhân viên vừa được thêm mới.
     */
    Long addEmployee(EmployeeRequest request);

    /**
     * Cập nhật thông tin nhân viên.
     * @param request Dữ liệu nhân viên cần cập nhật.
     * @return ID của nhân viên vừa được cập nhật.
     */
    Long updateEmployee(EmployeeRequest request);

    /**
     * Lấy thông tin chi tiết nhân viên theo ID.
     * @param employeeId ID của nhân viên.
     * @return Đối tượng chứa thông tin chi tiết nhân viên.
     */
    EmployeeDetailResponse getEmployeeById(Long employeeId);

    /**
     * Xóa nhân viên theo ID.
     * @param employeeId ID của nhân viên cần xóa.
     */
    void deleteEmployee(Long employeeId);
}
