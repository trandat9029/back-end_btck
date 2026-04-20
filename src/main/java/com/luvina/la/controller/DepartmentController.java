/**
 * Copyright(C) 2026 Luvina
 * [DepartmentController.java], 13/04/2026 tranledat
 */
package com.luvina.la.controller;

import com.luvina.la.dto.DepartmentDTO;
import com.luvina.la.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quan ly API phong ban.
 *
 * @author tranledat
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    /**
     * Lay danh sach tat ca phong ban.
     *
     * @return ResponseEntity chua status, message va danh sach phong ban.
     */
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllDepartments() {
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Lay danh sach phong ban thanh cong");
        response.put("data", departments);
        return ResponseEntity.ok(response);
    }
}
