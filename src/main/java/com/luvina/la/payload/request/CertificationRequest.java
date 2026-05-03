/**
 * Copyright(C) 2026 Luvina
 * [CertificationRequest.java], 19/04/2026 tranledat
 */
package com.luvina.la.payload.request;

import lombok.Data;

/**
 * Request DTO chứa thông tin chứng chỉ đính kèm khi thêm/sửa nhân viên.
 * 
 * @author tranledat
 */
@Data
public class CertificationRequest {
    private String certificationId;
    private String certificationStartDate;
    private String certificationEndDate;
    private String employeeCertificationScore;
}
