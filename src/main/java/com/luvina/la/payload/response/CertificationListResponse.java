/**
 * Copyright(C) 2026 Luvina Software
 * app.java 00/00/2026 tranledat
 */
package com.luvina.la.payload.response;

import com.luvina.la.dto.CertificationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Doi tuong phan hoi chua danh sach chung chi.
 *
 * @author tranledat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificationListResponse {

    private Integer code;
    private String message;
    private List<CertificationDTO> certifications;
}
