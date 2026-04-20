/**
 * Copyright(C) 2026 Luvina Software
 * app.java 00/00/2026 tranledat
 */
package com.luvina.la.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO luu thong tin chung chi.
 *
 * @author tranledat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDTO {

    private Long certificationId;
    private String certificationName;
}
