/**
 * Copyright(C) 2026 Luvina
 * [CertificationService.java], 20/04/2026 tranledat
 */
package com.luvina.la.service;

import com.luvina.la.dto.CertificationDTO;
import java.util.List;

/**
 * Interface Service cung cấp các nghiệp vụ liên quan đến chứng chỉ.
 * 
 * @author tranledat
 */
public interface CertificationService {

    /**
     * Lấy danh sách tất cả chứng chỉ.
     * 
     * @return Danh sách các DTO chứng chỉ
     */
    List<CertificationDTO> getCertifications();

    /**
     * Kiểm tra sự tồn tại của chứng chỉ dựa trên ID.
     * 
     * @param id ID của chứng chỉ cần kiểm tra
     * @return true nếu tồn tại, ngược lại false
     */
    boolean checkExistsCertificationById(Long id);
}
