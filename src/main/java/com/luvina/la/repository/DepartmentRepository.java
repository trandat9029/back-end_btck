/**
 * Copyright(C) 2026 Luvina
 * [DepartmentRepository.java], 13/04/2026 tranledat
 */
package com.luvina.la.repository;

import com.luvina.la.entity.Department;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface Repository cung cấp các phương thức truy cập dữ liệu bảng departments.
 * 
 * @author tranledat
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Lấy danh sách tất cả phòng ban, sắp xếp theo ID tăng dần.
     * 
     * @return Danh sách các phòng ban
     */
    List<Department> findAllByOrderByDepartmentIdAsc();
}
