/**
 * Copyright(C) 2026 Luvina Software
 * EmployeeRepository.java, 09/04/2026 tranledat
 */
package com.luvina.la.repository;

import com.luvina.la.entity.Employee;
import java.util.List;
import java.util.Optional;
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
     * Tìm kiếm nhân viên theo Login ID (tên đăng nhập).
     * Được sử dụng trong luồng xác thực (Authentication) và kiểm tra trùng lặp.
     *
     * @param employeeLoginId Tên đăng nhập cần tìm
     * @return Optional chứa nhân viên nếu tìm thấy, Optional.empty() nếu không tồn tại
     */
    Optional<Employee> findByEmployeeLoginId(String employeeLoginId);

    /**
     * Kiểm tra login_id đã tồn tại trong hệ thống chưa (dùng khi thêm mới).
     *
     * @param employeeLoginId Tên đăng nhập cần kiểm tra
     * @return true nếu Login ID đã tồn tại, false nếu chưa có
     */
    boolean existsByEmployeeLoginId(String employeeLoginId);

    /**
     * Đếm tổng số nhân viên (loại trừ admin) theo điều kiện tìm kiếm.
     * Mệnh đề LIKE có "ESCAPE '!'" để các ký tự '%', '_', '\' đã được
     * escape ở tầng Service (CommonUtils#escapeLike) được hiểu là ký tự
     * thường, không phải wildcard.
     *
     * @param employeeName Tên nhân viên cần lọc (null = tất cả, đã được escape ký tự đặc biệt)
     * @param departmentId ID phòng ban cần lọc (null = tất cả phòng ban)
     * @return Tổng số bản ghi nhân viên thỏa mãn điều kiện
     */
    @Query(value =
        "SELECT COUNT(e.employee_id) " +
        "FROM employees e " +
        "  INNER JOIN departments d ON e.department_id = d.department_id " +
        "WHERE e.employee_login_id != 'admin' " +
        "  AND (:employeeName IS NULL OR e.employee_name LIKE CONCAT('%', :employeeName, '%') ESCAPE '!') " +
        "  AND (:departmentId IS NULL OR e.department_id = :departmentId)",
        nativeQuery = true)
    Long countEmployees(
            @Param("employeeName") String employeeName,
            @Param("departmentId") Long departmentId);

    /**
     * Lấy danh sách nhân viên (loại trừ admin) với tìm kiếm, sắp xếp, phân trang.
     * Thứ tự sắp xếp cố định: employee_name → certification_name → end_date → employee_id.
     * Mệnh đề LIKE có "ESCAPE '!'" tương tự countEmployees để đảm bảo
     * ký tự đặc biệt ('%', '_', '\') không còn hiệu ứng wildcard sau khi được
     * escape ở tầng Service.
     *
     * @param employeeName        Tên nhân viên cần tìm kiếm (null = tất cả, đã escape ký tự đặc biệt)
     * @param departmentId        ID phòng ban cần lọc (null = tất cả phòng ban)
     * @param ordEmployeeName     Hướng sắp xếp theo tên nhân viên: "ASC" hoặc "DESC"
     * @param ordCertificationName Hướng sắp xếp theo cấp độ chứng chỉ: "ASC" hoặc "DESC"
     * @param ordEndDate          Hướng sắp xếp theo ngày hết hạn chứng chỉ: "ASC" hoặc "DESC"
     * @param offset              Vị trí bắt đầu lấy dữ liệu (dùng cho phân trang)
     * @param limit               Số lượng bản ghi tối đa trả về
     * @return Danh sách mảng Object[], mỗi phần tử là một dòng dữ liệu từ SQL
     */
    @Query(value =
        "SELECT " +
        "  e.employee_id, " +
        "  e.employee_name, " +
        "  e.employee_birth_date, " +
        "  d.department_name, " +
        "  e.employee_email, " +
        "  e.employee_telephone, " +
        "  c.certification_name, " +
        "  ec.end_date, " +
        "  ec.score " +
        "FROM employees e " +
        "  INNER JOIN departments d ON e.department_id = d.department_id " +
        "  LEFT JOIN employees_certifications ec ON e.employee_id = ec.employee_id " +
        "  LEFT JOIN certifications c ON ec.certification_id = c.certification_id " +
        "WHERE e.employee_login_id != 'admin' " +
        "  AND (:employeeName IS NULL OR e.employee_name LIKE CONCAT('%', :employeeName, '%') ESCAPE '!') " +
        "  AND (:departmentId IS NULL OR e.department_id = :departmentId) " +
        "ORDER BY " +
        "  CASE WHEN :ordEmployeeName = 'ASC' THEN e.employee_name END ASC, " +
        "  CASE WHEN :ordEmployeeName = 'DESC' THEN e.employee_name END DESC, " +
        "  CASE WHEN :ordCertificationName = 'ASC' THEN c.certification_level END DESC, " +
        "  CASE WHEN :ordCertificationName = 'DESC' THEN c.certification_level END ASC, " +
        "  CASE WHEN :ordEndDate = 'ASC' THEN ec.end_date END ASC, " +
        "  CASE WHEN :ordEndDate = 'DESC' THEN ec.end_date END DESC, " +
        "  e.employee_id ASC " +
        "LIMIT :limit OFFSET :offset",
        nativeQuery = true)
    List<Object[]> getEmployees(
            @Param("employeeName") String employeeName,
            @Param("departmentId") Long departmentId,
            @Param("ordEmployeeName") String ordEmployeeName,
            @Param("ordCertificationName") String ordCertificationName,
            @Param("ordEndDate") String ordEndDate,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);

}
