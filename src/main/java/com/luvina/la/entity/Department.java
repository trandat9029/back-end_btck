/**
 * Copyright(C) 2026 Luvina
 * [Department.java], 13/04/2026 tranledat
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
 * Entity đại diện cho bảng departments lưu thông tin phòng ban.
 *
 * @author tranledat
 */
@Entity
@Table(name = "departments")
@Data
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id", unique = true, nullable = false)
    private Long departmentId;

    @Column(name = "department_name", nullable = false)
    private String departmentName;
}
