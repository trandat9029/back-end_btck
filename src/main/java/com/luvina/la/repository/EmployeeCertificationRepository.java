/**
 * Copyright(C) 2026 Luvina
 * [EmployeeCertificationRepository.java], 13/04/2026 tranledat
 */
package com.luvina.la.repository;

import com.luvina.la.entity.EmployeeCertification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository truy van du lieu quan he nhan vien va chung chi.
 *
 * @author tranledat
 */
@Repository
public interface EmployeeCertificationRepository extends CrudRepository<EmployeeCertification, Long> {

    /**
     * Tim quan he nhan vien-chung chi theo id.
     *
     * @param employeeCertificationId Ma quan he nhan vien-chung chi.
     * @return Optional chua thong tin quan he.
     */
    Optional<EmployeeCertification> findByEmployeeCertificationId(Long employeeCertificationId);

    /**
     * Lay danh sach chung chi theo ma nhan vien.
     *
     * @param employeeId Ma nhan vien.
     * @return Danh sach thong tin nhan vien-chung chi.
     */
    List<EmployeeCertification> findByEmployeeEmployeeId(Long employeeId);

    /**
     * Lay danh sach nhan vien theo ma chung chi.
     *
     * @param certificationId Ma chung chi.
     * @return Danh sach thong tin nhan vien-chung chi.
     */
    List<EmployeeCertification> findByCertificationCertificationId(Long certificationId);

    /**
     * Lấy danh sách chứng chỉ của nhân viên, sắp xếp theo Certification Level (Tăng dần).
     * @param employeeId Ma nhan vien.
     * @return Danh sách chứng chỉ nhân viên.
     */
    @Query("SELECT ec FROM EmployeeCertification ec WHERE ec.employee.employeeId = :employeeId " +
           "ORDER BY ec.certification.certificationLevel ASC")
    List<EmployeeCertification> findCertsByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * Xóa toàn bộ chứng chỉ của một nhân viên.
     * @param employeeId Ma nhan vien.
     */
    void deleteByEmployeeEmployeeId(Long employeeId);
}
