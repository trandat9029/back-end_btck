/**
 * Copyright(C) 2026 Luvina Software
 * app.java 00/00/2026 tranledat
 */
package com.luvina.la.service.impl;

import com.luvina.la.dto.CertificationDTO;
import com.luvina.la.entity.Certification;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation xử lý nghiệp vụ chứng chỉ.
 * @author tranledat
 */
@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;

    /**
     * Lấy danh sách tất cả các chứng chỉ và chuyển đổi sang DTO.
     * @return Danh sách CertificationDTO.
     */
    @Override
    public List<CertificationDTO> getCertifications() {
        List<Certification> certifications = certificationRepository.findAllByOrderByCertificationIdDesc();

        return certifications.stream().map(certification -> {
            CertificationDTO dto = new CertificationDTO();
            dto.setCertificationId(certification.getCertificationId());
            dto.setCertificationName(certification.getCertificationName());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Kiểm tra xem chứng chỉ có tồn tại trong hệ thống không.
     * @param certificationId ID chứng chỉ cần kiểm tra.
     * @return true nếu tồn tại, false nếu không.
     */
    @Override
    public boolean checkExistsCertificationById(Long certificationId) {
        if (certificationId == null) return false;
        return certificationRepository.findById(certificationId).isPresent();
    }
}
