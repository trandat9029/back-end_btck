/**
 * Copyright(C) 2026 Luvina
 * [EmployeeCertificationRepository.java], 13/04/2026 tranledat
 */
package com.luvina.la.repository;

import com.luvina.la.entity.EmployeeCertification;
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
     * Xóa tất cả các chứng chỉ của một nhân viên theo employeeId
     * 
     * @param employeeId ID của nhân viên cần xóa chứng chỉ
     */
    void deleteAllByEmployeeId(Long employeeId);
}
