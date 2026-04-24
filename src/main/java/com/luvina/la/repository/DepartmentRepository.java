/**
 * Copyright(C) 2026 Luvina
 * [DepartmentRepository.java], 13/04/2026 tranledat
 */
package com.luvina.la.repository;

import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.entity.Department;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository truy van du lieu phong ban.
 *
 * @author tranledat
 */
@Repository
public interface DepartmentRepository extends CrudRepository<Department, Long> {

    /**
     * Lay danh sach phong ban duoi dang DTO.
     *
     * @return Danh sach DepartmentDTO duoc sap xep theo department id.
     */
    @Query("SELECT new com.luvina.la.dto.DepartmentDTO(d.departmentId, d.departmentName) " +
            "FROM Department d ORDER BY d.departmentId")
    List<DepartmentDTO> findAllDepartmentDtos();

    /**
     * Tim phong ban theo department id.
     *
     * @param departmentId Ma phong ban.
     * @return Optional chua thong tin phong ban.
     */
    Optional<Department> findByDepartmentId(Long departmentId);

    /**
     * Tim phong ban theo ten phong ban.
     *
     * @param departmentName Ten phong ban.
     * @return Optional chua thong tin phong ban.
     */
    Optional<Department> findByDepartmentName(String departmentName);
}
