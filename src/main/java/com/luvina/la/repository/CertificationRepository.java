/**
 * Copyright(C) 2026 Luvina
 * [CertificationRepository.java], 13/04/2026 tranledat
 */
package com.luvina.la.repository;

import com.luvina.la.entity.Certification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository truy van du lieu chung chi.
 *
 * @author tranledat
 */
@Repository
public interface CertificationRepository extends CrudRepository<Certification, Long> {

    /**
     * Tim chung chi theo certification id.
     *
     * @param certificationId Ma chung chi.
     * @return Optional chua thong tin chung chi.
     */
    Optional<Certification> findByCertificationId(Long certificationId);

    /**
     * Tim chung chi theo ten chung chi.
     *
     * @param certificationName Ten chung chi.
     * @return Optional chua thong tin chung chi.
     */
    Optional<Certification> findByCertificationName(String certificationName);
}
