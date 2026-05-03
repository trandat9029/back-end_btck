/**
 * Copyright(C) 2026 Luvina
 * [Certification.java], 13/04/2026 tranledat
 */
package com.luvina.la.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Entity đại diện cho bảng certifications lưu thông tin chứng chỉ.
 *
 * @author tranledat
 */
@Entity
@Table(name = "certifications")
@Data
public class Certification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certification_id", unique = true, nullable = false)
    private Long certificationId;

    @Column(name = "certification_name", nullable = false)
    private String certificationName;

    @Column(name = "certification_level", nullable = false)
    private Integer certificationLevel;
}
