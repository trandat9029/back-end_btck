/**
 * Copyright(C) 2026 Luvina
 * [CertificationRepository.java], 13/04/2026 tranledat
 */
package com.luvina.la.repository;

import com.luvina.la.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository truy vấn dữ liệu chứng chỉ.
 * @author tranledat
 */
@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    /**
     * Lấy danh sách tất cả chứng chỉ, sắp xếp theo ID giảm dần.
     * @return Danh sách các chứng chỉ.
     */
    List<Certification> findAllByOrderByCertificationIdDesc();
}
