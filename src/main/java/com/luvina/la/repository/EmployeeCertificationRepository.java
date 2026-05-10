/**
 * Copyright(C) 2026 Luvina Software
 * EmployeeCertificationRepository.java, 13/04/2026 tranledat
 */
package com.luvina.la.repository;

import com.luvina.la.entity.EmployeeCertification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface Repository cung cấp các phương thức truy cập dữ liệu bảng employees_certifications.
 * 
 * @author tranledat
 */
@Repository
public interface EmployeeCertificationRepository extends JpaRepository<EmployeeCertification, Long> {

    /**
     * Tìm toàn bộ bản ghi chứng chỉ của một nhân viên theo ID.
     * Dùng trong luồng lấy chi tiết nhân viên (ADM003) để hiển thị danh sách chứng chỉ.
     *
     * @param employeeId ID của nhân viên cần tìm chứng chỉ
     * @return Danh sách EmployeeCertification liên kết với nhân viên, rỗng nếu không có
     */
    List<EmployeeCertification> findAllByEmployeeId(Long employeeId);

    /**
     * Xóa toàn bộ bản ghi chứng chỉ liên kết với một nhân viên.
     * Được gọi trước khi xóa nhân viên hoặc khi cập nhật lại toàn bộ chứng chỉ.
     * Mục đích: Tránh vi phạm ràng buộc khóa ngoại (Foreign Key Constraint).
     *
     * @param employeeId ID của nhân viên cần xóa toàn bộ chứng chỉ
     */
    void deleteAllByEmployeeId(Long employeeId);
}
