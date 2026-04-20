/**
 * Copyright(C) 2026 Luvina
 * [CertificationRepository.java], 13/04/2026 tranledat
 */
package com.luvina.la.repository;

import com.luvina.la.entity.Certification;
import com.luvina.la.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository truy van du lieu chung chi.
 *
 * @author tranledat
 */
@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    /**
     * Lấy danh sách tất cả chung chi, sắp xếp theo ID giam dan.
     *
     * @return Danh sách các chung chi
     */
    List<Certification> findAllByOrderByCertificationIdDesc();
}
