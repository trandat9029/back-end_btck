/**
 * Copyright(C) 2026 Luvina
 * [EmployeeRepository.java], 13/04/2026 tranledat
 */
package com.luvina.la.repository;

import com.luvina.la.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository truy vấn dữ liệu nhân viên.
 *
 * @author tranledat
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Tìm nhân viên theo login id.
     *
     * @param employeeLoginId Login id của nhân viên.
     * @return Optional chứa thông tin nhân viên.
     */
    Optional<Employee> findByEmployeeLoginId(String employeeLoginId);

    /**
     * Tìm nhân viên theo employee id.
     *
     * @param employeeId Mã nhân viên.
     * @return Optional chứa thông tin nhân viên.
     */
    Optional<Employee> findByEmployeeId(Long employeeId);

    /**
     * Đếm tổng số nhân viên thỏa điều kiện tìm kiếm.
     *
     * @param employeeName Tên nhân viên dùng để tìm kiếm.
     * @param departmentId Mã phòng ban dùng để lọc.
     * @return Tổng số bản ghi thỏa điều kiện.
     */
    @Query(value = "SELECT COUNT(e.employee_id) FROM employees e " +
            "INNER JOIN departments d ON e.department_id = d.department_id " +
            "WHERE e.employee_login_id != 'admin' " +
            "AND (:employeeName IS NULL OR :employeeName = '' OR CONCAT(' ', e.employee_name, ' ') LIKE CONCAT('% ', :employeeName, ' %') ESCAPE '\\\\') " +
            "AND (:departmentId IS NULL OR e.department_id = :departmentId)",
            nativeQuery = true)
    Long countEmployeesWithFilter(
            @Param("employeeName") String employeeName,
            @Param("departmentId") Long departmentId
    );

    /**
     * Lấy danh sách nhân viên theo điều kiện tìm kiếm, sắp xếp và phân trang.
     *
     * @param employeeName Tên nhân viên dùng để tìm kiếm.
     * @param departmentId Mã phòng ban dùng để lọc.
     * @param sortEmployeeName Thứ tự sắp xếp theo tên nhân viên.
     * @param sortCertificationName Thứ tự sắp xếp theo tên chứng chỉ.
     * @param sortEndDate Thứ tự sắp xếp theo ngày hết hạn.
     * @param offset Vị trí bản ghi bắt đầu.
     * @param limit Số lượng bản ghi cần lấy.
     * @return Danh sách row object theo kết quả query.
     */

    @Query(value = "SELECT e.employee_id, e.employee_name, e.employee_birth_date, " +
            "d.department_id, d.department_name, e.employee_email, e.employee_telephone, " +
            "e.employee_name_kana, e.employee_login_id, " +
            "rc.certification_name, rc.end_date, rc.score " +
            "FROM employees e " +
            "INNER JOIN departments d ON e.department_id = d.department_id " +
            "LEFT JOIN (" +
            "    SELECT ranked.employee_id, ranked.certification_name, ranked.certification_level, ranked.end_date, ranked.score " +
            "    FROM (" +
            "        SELECT ec.employee_id, c.certification_name, c.certification_level, ec.end_date, ec.score, " +
            "               ROW_NUMBER() OVER ( " +
            "                   PARTITION BY ec.employee_id " +
            "                   ORDER BY " +
            "                       CASE WHEN :sortCertificationName = 'desc' THEN c.certification_level END ASC, " +
            "                       CASE WHEN :sortCertificationName = 'asc' THEN c.certification_level END DESC, " +
            "                       CASE WHEN :sortEndDate = 'asc' THEN ec.end_date END ASC, " +
            "                       CASE WHEN :sortEndDate = 'desc' THEN ec.end_date END DESC, " +
            "                       ec.employee_certification_id DESC " +
            "               ) AS row_num " +
            "        FROM employees_certifications ec " +
            "        INNER JOIN certifications c ON ec.certification_id = c.certification_id " +
            "    ) ranked " +
            "    WHERE ranked.row_num = 1 " +
            ") rc ON e.employee_id = rc.employee_id " +
            "WHERE e.employee_login_id != 'admin' " +
            "AND (:employeeName IS NULL OR CONCAT(' ', e.employee_name, ' ') LIKE CONCAT('% ', :employeeName, ' %') ESCAPE '\\\\') " +
            "AND (:departmentId IS NULL OR e.department_id = :departmentId) " +
            "ORDER BY " +
            "CASE WHEN :sortEmployeeName = 'asc' THEN e.employee_name END ASC, " +
            "CASE WHEN :sortEmployeeName = 'desc' THEN e.employee_name END DESC, " +
            "(rc.certification_level IS NULL) ASC, " +
            "CASE WHEN :sortCertificationName = 'desc' THEN rc.certification_level END ASC, " +
            "CASE WHEN :sortCertificationName = 'asc' THEN rc.certification_level END DESC, " +
            "(rc.end_date IS NULL) ASC, " +
            "CASE WHEN :sortEndDate = 'asc' THEN rc.end_date END ASC, " +
            "CASE WHEN :sortEndDate = 'desc' THEN rc.end_date END DESC, " +
            "e.employee_id ASC " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<Object[]> getListEmployee(
            @Param("employeeName") String employeeName,
            @Param("departmentId") Long departmentId,
            @Param("sortEmployeeName") String sortEmployeeName,
            @Param("sortCertificationName") String sortCertificationName,
            @Param("sortEndDate") String sortEndDate,
            @Param("limit") Integer limit,
            @Param("offset") Integer offset
    );
}
