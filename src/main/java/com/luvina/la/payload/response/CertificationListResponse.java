/**
 * Copyright(C) 2026 Luvina
 * [CertificationListResponse.java], 20/04/2026 tranledat
 */
package com.luvina.la.payload.response;

import com.luvina.la.dto.CertificationDTO;
import java.util.List;
import lombok.Data;

/**
 * Đối tượng phản hồi chứa danh sách chứng chỉ.
 * 
 * @author tranledat
 */
@Data
public class CertificationListResponse {
    private Integer code;
    private String message;
    private List<CertificationDTO> certifications;
}
