/**
 * Copyright(C) 2026 Luvina
 * [DepartmentController.java], 13/04/2026 tranledat
 */
package com.luvina.la.controller;

import com.luvina.la.config.Constants;
import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.payload.response.DepartmentListResponse;
import com.luvina.la.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller quản lý API phòng ban.
 * @author tranledat
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * Lấy danh sách tất cả phòng ban.
     * @return ResponseEntity chứa danh sách phòng ban.
     */
    @GetMapping("")
    public ResponseEntity<DepartmentListResponse> getAllDepartments() {
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        DepartmentListResponse response = new DepartmentListResponse();
        response.setCode(Constants.CODE_SUCCESS);
        response.setDepartments(departments);
        return ResponseEntity.ok(response);
    }
}
