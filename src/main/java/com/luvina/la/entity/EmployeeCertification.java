/**
 * Copyright(C) 2026 Luvina
 * [EmployeeCertification.java], 13/04/2026 tranledat
 */
package com.luvina.la.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity dai dien cho bang employees_certifications luu quan he nhan vien va chung chi.
 *
 * @author tranledat
 */
@Entity
@Table(name = "employees_certifications")
@Data
public class EmployeeCertification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_certification_id", unique = true, nullable = false)
    private Long employeeCertificationId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "score", nullable = false)
    private BigDecimal score;
}
