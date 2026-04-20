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

    /**
     * Lay danh sach tat ca chung chi.
     *
     * @return Danh sach chung chi.
     */
    List<CertificationDTO> getCertifications();
}
