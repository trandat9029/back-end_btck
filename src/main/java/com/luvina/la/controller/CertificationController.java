/**
 * Copyright(C) 2026 Luvina Software
 * app.java 00/00/2026 tranledat
 */
package com.luvina.la.controller;

import com.luvina.la.contant.MessageErrCode;
import com.luvina.la.payload.response.CertificationListResponse;
import com.luvina.la.service.CertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

/**
 * Controller quan ly API chung chi JP.
 *
 * @author tranledat
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/certifications")
public class CertificationController {

    private final CertificationService certificationService;
    private final MessageSource messageSource;

    /**
     * Lay danh sach tat ca chung chi.
     *
     * @return ResponseEntity chua status, message va danh sach chung chi.
     */
    @GetMapping("")
    public ResponseEntity<CertificationListResponse> getCertifications() {
        CertificationListResponse response = new CertificationListResponse();

        try {
            response.setCode(HttpStatus.OK.value());
            response.setCertifications(certificationService.getCertifications());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String message = messageSource.getMessage(
                    MessageErrCode.MSG_CODE_ER023,
                    null,
                    MessageErrCode.MSG_CODE_ER023,
                    Locale.JAPANESE
            );
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(message);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
