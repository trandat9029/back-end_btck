/**
 * Copyright(C) 2026 Luvina
 * [CertificationRepository.java], 13/04/2026 tranledat
 */
package com.luvina.la.repository;

import com.luvina.la.entity.Certification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface Repository cung cấp các phương thức truy cập dữ liệu bảng certifications.
 * 
 * @author tranledat
 */
@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    /**
     * Lấy danh sách tất cả chứng chỉ, sắp xếp theo level tăng dần.
     * 
     * @return Danh sách các chứng chỉ
     */
    List<Certification> findAllByOrderByCertificationLevelAsc();
}
