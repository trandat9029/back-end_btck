/**
 * Copyright(C) 2026 Luvina
 * [DepartmentService.java], 13/04/2026 tranledat
 */
package com.luvina.la.service.impl;

import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.service.DepartmentService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation xu ly nghiep vu phong ban.
 *
 * @author tranledat
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    private final DepartmentRepository departmentRepository;

    /**
     * Lay danh sach tat ca phong ban.
     *
     * @return Danh sach phong ban, tra ve danh sach rong neu xay ra loi.
     */
    @Override
    public List<DepartmentDTO> getAllDepartments() {
        try {
            return departmentRepository.findAllDepartmentDtos();
        } catch (Exception e) {
            log.error("Error getting all departments", e);
            return List.of();
        }
    }

    /**
     * Kiểm tra xem phòng ban có tồn tại trong hệ thống không.
     *
     * @param departmentId ID phòng ban cần kiểm tra.
     * @return true nếu tồn tại, false nếu không.
     */
    @Override
    public boolean checkExistsDepartmentById(Long departmentId) {
        if (departmentId == null) return false;
        return departmentRepository.findByDepartmentId(departmentId).isPresent();
    }
}
