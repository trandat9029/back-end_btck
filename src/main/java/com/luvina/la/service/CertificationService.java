/**
 * Copyright(C) 2026 - Luvina
 * [CertificationService.java], 24/04/2026 [tranledat]
 */
package com.luvina.la.service;

import com.luvina.la.dto.CertificationDTO;

import java.util.List;

/**
 * Interface cung cấp các phương thức xử lý nghiệp vụ liên quan đến chứng chỉ.
 *
 * @author tranledat
 */
public interface CertificationService {

    List<CertificationDTO> getCertifications();

    /**
     * Kiểm tra xem chứng chỉ có tồn tại trong hệ thống không.
     *
     * @param certificationId ID chứng chỉ cần kiểm tra.
     * @return true nếu tồn tại, false nếu không.
     */
    boolean checkExistsCertificationById(Long certificationId);
}
