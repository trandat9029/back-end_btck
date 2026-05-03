/**
 * Copyright(C) 2026 Luvina
 * [DepartmentServiceImpl.java], 09/04/2026 tranledat
 */
package com.luvina.la.service.impl;

import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.entity.Department;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.service.DepartmentService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Lớp triển khai các nghiệp vụ liên quan đến phòng ban.
 * 
 * @author tranledat
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentDTO> getDepartments() {
        List<Department> departments = departmentRepository.findAllByOrderByDepartmentIdAsc();
        return departments.stream().map(d -> {
            DepartmentDTO dto = new DepartmentDTO();
            dto.setDepartmentId(d.getDepartmentId());
            dto.setDepartmentName(d.getDepartmentName());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean checkExistsDepartmentById(Long id) {
        return departmentRepository.existsById(id);
    }
}
