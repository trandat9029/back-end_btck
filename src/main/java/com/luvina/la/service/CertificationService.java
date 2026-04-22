/**
 * Copyright(C) 2026 Luvina Software
 * app.java 00/00/2026 tranledat
 */

package com.luvina.la.service;


import com.luvina.la.dto.CertificationDTO;

import java.util.List;

/**
 * Service xu ly nghiep vu lien quan den chung chi.
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
