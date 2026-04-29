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
 * Đối tượng phản hồi chứa danh sách chứng chỉ.
 * @author tranledat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificationListResponse {
    private String code;
    private String message;
    private List<CertificationDTO> certifications;
}
