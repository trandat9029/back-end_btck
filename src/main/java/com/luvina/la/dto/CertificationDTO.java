/**
 * Copyright(C) 2026 Luvina Software
 * CertificationDTO.java 00/00/2026 tranledat
 */
package com.luvina.la.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

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
    private String startDate;
    private String endDate;
    private BigDecimal score;
}
