/**
 * Copyright(C) 2026 Luvina
 * [Employee.java], 13/04/2026 tranledat
 */
package com.luvina.la.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Entity dai dien cho bang employees luu thong tin nhan vien.
 *
 * @author tranledat
 */
@Entity
@Table(name = "employees")
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 5771173953267484096L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id", unique = true, nullable = false)
    private Long employeeId;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "employee_name_kana")
    private String employeeNameKana;

    @Column(name = "employee_birth_date")
    private LocalDate employeeBirthDate;

    @Column(name = "employee_email", nullable = false)
    private String employeeEmail;

    @Column(name = "employee_telephone")
    private String employeeTelephone;

    @Column(name = "employee_login_id", nullable = false)
    private String employeeLoginId;

    @Column(name = "employee_login_password", nullable = false)
    private String employeeLoginPassword;

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private List<EmployeeCertification> certifications;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
