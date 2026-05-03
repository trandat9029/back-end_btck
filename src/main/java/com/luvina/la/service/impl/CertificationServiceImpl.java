/**
 * Copyright(C) 2026 Luvina
 * [CertificationServiceImpl.java], 20/04/2026 tranledat
 */
package com.luvina.la.service.impl;

import com.luvina.la.dto.CertificationDTO;
import com.luvina.la.entity.Certification;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.service.CertificationService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Lớp triển khai các nghiệp vụ liên quan đến chứng chỉ.
 * 
 * @author tranledat
 */
@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;

    @Override
    public List<CertificationDTO> getCertifications() {
        List<Certification> certifications = certificationRepository.findAllByOrderByCertificationLevelAsc();
        return certifications.stream().map(c -> {
            CertificationDTO dto = new CertificationDTO();
            dto.setCertificationId(c.getCertificationId());
            dto.setCertificationName(c.getCertificationName());
            dto.setCertificationLevel(c.getCertificationLevel());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean checkExistsCertificationById(Long id) {
        return certificationRepository.existsById(id);
    }
}
