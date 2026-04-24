/**
 * Copyright(C) 2026 Luvina Software
 * app.java 00/00/2026 tranledat
 */
package com.luvina.la.controller;

import com.luvina.la.config.Constants;
import com.luvina.la.payload.response.CertificationListResponse;
import com.luvina.la.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý API chứng chỉ.
 * @author tranledat
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/certifications")
public class CertificationController {

    private final CertificationService certificationService;

    /**
     * Lấy danh sách tất cả chứng chỉ.
     * @return ResponseEntity chứa danh sách chứng chỉ.
     */
    @GetMapping("")
    public ResponseEntity<CertificationListResponse> getCertifications() {
        CertificationListResponse response = new CertificationListResponse();
        response.setCode(Constants.CODE_SUCCESS);
        response.setCertifications(certificationService.getCertifications());
        return ResponseEntity.ok(response);
    }
}
