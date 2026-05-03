/**
 * Copyright(C) 2026 Luvina
 * [DepartmentController.java], 09/04/2026 tranledat
 */
package com.luvina.la.controller;

import com.luvina.la.payload.response.DepartmentListResponse;
import com.luvina.la.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller xử lý các yêu cầu liên quan đến phòng ban.
 * 
 * @author tranledat
 */
@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * API Lấy danh sách tất cả các phòng ban có trong hệ thống.
     * 
     * @return ResponseEntity chứa DepartmentListResponse với danh sách phòng ban
     */
    @GetMapping
    public ResponseEntity<DepartmentListResponse> getDepartments() {
        DepartmentListResponse response = new DepartmentListResponse();
        response.setDepartments(departmentService.getDepartments());
        response.setCode(HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
