/**
 * Copyright(C) 2026 Luvina
 * [CertificationController.java], 20/04/2026 tranledat
 */
package com.luvina.la.controller;

import com.luvina.la.payload.response.CertificationListResponse;
import com.luvina.la.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các yêu cầu liên quan đến chứng chỉ.
 * 
 * @author tranledat
 */
@RestController
@RequestMapping("/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    /**
     * API Lấy danh sách tất cả các chứng chỉ có trong hệ thống.
     * 
     * @return ResponseEntity chứa CertificationListResponse với danh sách các chứng chỉ
     */
    @GetMapping
    public ResponseEntity<CertificationListResponse> getCertifications() {
        CertificationListResponse response = new CertificationListResponse();
        response.setCertifications(certificationService.getCertifications());
        response.setCode(HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
