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
 * Repository truy van du lieu nhan vien.
 *
 * @author tranledat
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Tim nhan vien theo login id.
     *
     * @param employeeLoginId Login id cua nhan vien.
     * @return Optional chua thong tin nhan vien.
     */
    Optional<Employee> findByEmployeeLoginId(String employeeLoginId);

    /**
     * Tim nhan vien theo employee id.
     *
     * @param employeeId Ma nhan vien.
     * @return Optional chua thong tin nhan vien.
     */
    Optional<Employee> findByEmployeeId(Long employeeId);

    /**
     * Dem tong so nhan vien thoa dieu kien tim kiem.
     *
     * @param employeeName Ten nhan vien dung de tim kiem.
     * @param departmentId Ma phong ban dung de loc.
     * @return Tong so ban ghi thoa dieu kien.
     */
    @Query(value = "SELECT COUNT(e.employee_id) FROM employees e " +
            "INNER JOIN departments d ON e.department_id = d.department_id " +
            "WHERE e.employee_login_id != 'admin' " +
            "AND (:employeeName IS NULL OR :employeeName = '' OR e.employee_name LIKE CONCAT('%', :employeeName, '%') ESCAPE '\\\\') " +
            "AND (:departmentId IS NULL OR e.department_id = :departmentId)",
            nativeQuery = true)
    Long countEmployeesWithFilter(
            @Param("employeeName") String employeeName,
            @Param("departmentId") Long departmentId
    );

    /**
     * Lay danh sach nhan vien theo dieu kien tim kiem, sap xep va phan trang.
     *
     * @param employeeName Ten nhan vien dung de tim kiem.
     * @param departmentId Ma phong ban dung de loc.
     * @param ordEmployeeName Thu tu sap xep theo ten nhan vien.
     * @param ordCertificationName Thu tu sap xep theo ten chung chi.
     * @param ordEndDate Thu tu sap xep theo ngay het han.
     * @param offset Vi tri ban ghi bat dau.
     * @param limit So luong ban ghi can lay.
     * @return Danh sach row object theo ket qua query.
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
            "AND (:employeeName IS NULL OR e.employee_name LIKE CONCAT('%', :employeeName, '%') ESCAPE '\\\\') " +
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
