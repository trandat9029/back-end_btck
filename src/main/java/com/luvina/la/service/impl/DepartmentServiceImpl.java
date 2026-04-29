/**
 * Copyright(C) 2026 Luvina
 * [DepartmentService.java], 13/04/2026 tranledat
 */
package com.luvina.la.service.impl;

import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation xử lý nghiệp vụ phòng ban.
 * @author tranledat
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    /**
     * Lấy danh sách tất cả phòng ban.
     * @return Danh sách DepartmentDTO.
     */
    @Override
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAllDepartmentDtos();
    }

    /**
     * Kiểm tra xem phòng ban có tồn tại trong hệ thống không.
     * @param departmentId ID phòng ban cần kiểm tra.
     * @return true nếu tồn tại, false nếu không.
     */
    @Override
    public boolean checkExistsDepartmentById(Long departmentId) {
        if (departmentId == null) return false;
        return departmentRepository.findByDepartmentId(departmentId).isPresent();
    }
}
