/**
 * Copyright(C) 2026 Luvina
 * [EmployeeRepository.java], 13/04/2026 tranledat
 */
package com.luvina.la.repository;

import com.luvina.la.entity.Employee;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Interface Repository cung cấp các phương thức truy cập dữ liệu bảng employees.
 * 
 * @author tranledat
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Tìm kiếm nhân viên bằng tên đăng nhập.
     * 
     * @param employeeLoginId tên đăng nhập
     * @return Optional chứa thông tin nhân viên nếu tìm thấy
     */
    Optional<Employee> findByEmployeeLoginId(String employeeLoginId);

    /**
     * Tìm kiếm nhân viên bằng ID.
     * 
     * @param employeeId ID của nhân viên
     * @return Optional chứa thông tin nhân viên nếu tìm thấy
     */
    Optional<Employee> findByEmployeeId(Long employeeId);

    /**
     * Lấy thông tin chi tiết nhân viên và danh sách chứng chỉ theo ID.
     * 
     * @param employeeId ID nhân viên
     * @return Danh sách các bản ghi (mỗi bản ghi là một cặp Nhân viên - Chứng chỉ)
     */
    @Query(value = "SELECT e.employee_id AS employeeId, e.employee_name AS employeeName, e.employee_birth_date AS employeeBirthDate, "
            + "e.department_id AS departmentId, d.department_name AS departmentName, e.employee_email AS employeeEmail, "
            + "e.employee_telephone AS employeeTelephone, e.employee_name_kana AS employeeNameKana, e.employee_login_id AS employeeLoginId, "
            + "c.certification_id AS certificationId, c.certification_name AS certificationName, "
            + "ec.start_date AS startDate, ec.end_date AS endDate, ec.score AS score "
            + "FROM employees e "
            + "INNER JOIN departments d ON e.department_id = d.department_id "
            + "LEFT JOIN employees_certifications ec ON e.employee_id = ec.employee_id "
            + "LEFT JOIN certifications c ON ec.certification_id = c.certification_id "
            + "WHERE e.employee_id = :employeeId AND e.employee_role <> 0 "
            + "ORDER BY c.certification_level ASC", nativeQuery = true)
    List<Map<String, Object>> findDetailById(@Param("employeeId") Long employeeId);

    /**
     * Tìm kiếm danh sách nhân viên với phân trang, lọc và sắp xếp.
     * 
     * @param employeeName tên nhân viên (tìm kiếm mờ)
     * @param departmentId ID phòng ban
     * @param pageable     đối tượng phân trang và sắp xếp
     * @return Trang kết quả chứa danh sách Map dữ liệu nhân viên
     */
    @Query(value = "SELECT e.employee_id AS employeeId, e.employee_name AS employeeName, e.employee_birth_date AS employeeBirthDate, "
            + "d.department_name AS departmentName, e.employee_email AS employeeEmail, e.employee_telephone AS employeeTelephone, "
            + "c.certification_name AS certificationName, ec.end_date AS endDate, ec.score AS score "
            + "FROM employees e "
            + "LEFT JOIN departments d ON e.department_id = d.department_id "
            + "LEFT JOIN ( "
            + "    SELECT ec_inner.*, ROW_NUMBER() OVER(PARTITION BY ec_inner.employee_id ORDER BY c_inner.certification_level ASC, ec_inner.employee_certification_id DESC) as rn "
            + "    FROM employees_certifications ec_inner "
            + "    JOIN certifications c_inner ON ec_inner.certification_id = c_inner.certification_id "
            + ") ec ON e.employee_id = ec.employee_id AND ec.rn = 1 "
            + "LEFT JOIN certifications c ON ec.certification_id = c.certification_id "
            + "WHERE (:employeeName IS NULL OR e.employee_name LIKE CONCAT('%', :employeeName, '%') ESCAPE '\\\\') "
            + "AND (:departmentId IS NULL OR e.department_id = :departmentId) "
            + "AND e.employee_role <> 0", 
            countQuery = "SELECT COUNT(DISTINCT e.employee_id) FROM employees e "
            + "LEFT JOIN departments d ON e.department_id = d.department_id "
            + "WHERE (:employeeName IS NULL OR e.employee_name LIKE CONCAT('%', :employeeName, '%') ESCAPE '\\\\') "
            + "AND (:departmentId IS NULL OR e.department_id = :departmentId) "
            + "AND e.employee_role <> 0", nativeQuery = true)
    Page<Map<String, Object>> searchEmployees(@Param("employeeName") String employeeName,
            @Param("departmentId") Long departmentId, Pageable pageable);
}
