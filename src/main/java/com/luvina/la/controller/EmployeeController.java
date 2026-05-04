/**
 * Copyright(C) 2026 Luvina
 * [EmployeeController.java], 09/04/2026 tranledat
 */
package com.luvina.la.controller;

import com.luvina.la.constants.AppConstants;
import com.luvina.la.constants.MessageCode;
import com.luvina.la.exception.BaseException;
import com.luvina.la.payload.request.EmployeeListRequest;
import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.payload.response.EmployeeResponse;
import com.luvina.la.payload.response.EmployeeListResponse;
import com.luvina.la.payload.response.MessageResponse;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validation.EmployeeValidate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

/**
 * Controller xử lý các yêu cầu liên quan đến nhân viên.
 * 
 * @author tranledat
 */
@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeValidate employeeValidator;

    /**
     * Tự động trim khoảng trắng dư thừa cho các tham số kiểu String đầu vào.
     * 
     * @param binder WebDataBinder dùng để đăng ký CustomEditor
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        StringTrimmerEditor stringTrimmer = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringTrimmer);
    }

    /**
     * API Lấy danh sách nhân viên có phân trang, tìm kiếm và sắp xếp.
     * 
     * @param request Đối tượng chứa các tham số lọc, tìm kiếm và phân trang
     * @return ResponseEntity chứa EmployeeListResponse
     */
    @GetMapping
    public ResponseEntity<EmployeeListResponse> getEmployees(EmployeeListRequest request) {
        // Validate logic nghiệp vụ cho phân trang và sắp xếp
        MessageResponse validateRes = employeeValidator.validateEmployeeList(request);
        if (validateRes != null) {
            throw new BaseException(validateRes, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        EmployeeListResponse response = new EmployeeListResponse();
        Long total = employeeService.getTotalRecords(request.getEmployeeName(), request.getDepartmentId());

        if (total > 0) {
            response.setEmployees(employeeService.getEmployees(
                    request.getEmployeeName(),
                    request.getDepartmentId(),
                    request.getOrdEmployeeName(),
                    request.getOrdCertificationName(),
                    request.getOrdEndDate(),
                    request.getOffset(),
                    request.getLimit()));
        } else {
            response.setEmployees(new ArrayList<>());
        }

        response.setCode(HttpStatus.OK.value());
        response.setTotalRecords(total);
        return ResponseEntity.ok(response);
    }

    /**
     * API Thêm mới nhân viên vào hệ thống.
     * Thực hiện validate nghiệp vụ trước khi lưu vào database.
     * 
     * @param employeeRequest Dữ liệu nhân viên cần thêm mới
     * @param mode Chế độ validate (SUBMIT hoặc CONFIRM)
     * @return ResponseEntity chứa EmployeeResponse với mã thành công và thông báo
     * @throws BaseException Nếu dữ liệu không vượt qua được bước validate nghiệp vụ
     */
    @PostMapping
    public ResponseEntity<EmployeeResponse> addEmployee(
            @RequestBody EmployeeRequest employeeRequest,
            @RequestParam(value = AppConstants.MODE, defaultValue = AppConstants.MODE_CONFIRM) String mode) {
        
        // Điều phối validate dựa trên mode
        MessageResponse validateRes;
        if (AppConstants.MODE_SUBMIT.equals(mode)) {
            validateRes = employeeValidator.validateForSubmit(employeeRequest);
        } else {
            validateRes = employeeValidator.validateForConfirm(employeeRequest);
        }

        if (validateRes != null) {
            throw new BaseException(validateRes, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        employeeService.addEmployee(employeeRequest);

        EmployeeResponse employeeResponse = EmployeeResponse.builder()
                .code(String.valueOf(HttpStatus.OK.value()))
                .employeeId(employeeRequest.getEmployeeId())
                .message(MessageResponse.builder()
                        .code(MessageCode.MSG_CODE_MSG001)
                        .params(new ArrayList<>())
                        .build())
                .build();
        return ResponseEntity.ok(employeeResponse);
    }

    /**
     * API Validate dữ liệu nhân viên (Dùng cho màn hình ADM004/ADM005).
     * 
     * @param employeeRequest Dữ liệu nhân viên cần kiểm tra
     * @param mode Chế độ validate (SUBMIT hoặc CONFIRM)
     * @return ResponseEntity chứa EmployeeResponse với mã thành công 200 nếu dữ liệu hợp lệ
     * @throws BaseException Nếu dữ liệu có lỗi nghiệp vụ
     */
    @PostMapping("/validate")
    public ResponseEntity<EmployeeResponse> validateEmployee(
            @RequestBody EmployeeRequest employeeRequest,
            @RequestParam(value = AppConstants.MODE, defaultValue = AppConstants.MODE_CONFIRM) String mode) {
        
        // Điều phối validate dựa trên mode
        MessageResponse validateRes;
        if (AppConstants.MODE_SUBMIT.equals(mode)) {
            validateRes = employeeValidator.validateForSubmit(employeeRequest);
        } else {
            validateRes = employeeValidator.validateForConfirm(employeeRequest);
        }

        if (validateRes != null) {
            throw new BaseException(validateRes, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        EmployeeResponse employeeResponse = EmployeeResponse.builder()
                .code(String.valueOf(HttpStatus.OK.value()))
                .build();
        return ResponseEntity.ok(employeeResponse);
    }

    /**
     * API Lấy thông tin chi tiết của một nhân viên dựa trên ID.
     * 
     * @param employeeId ID của nhân viên cần lấy thông tin
     * @return ResponseEntity chứa EmployeeResponse bao gồm thông tin cá nhân và chứng chỉ
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponse> getEmployeeDetailById(@PathVariable Long employeeId) {
        EmployeeResponse employeeResponse = employeeService.getEmployeeDetailById(employeeId);
        return ResponseEntity.ok(employeeResponse);
    }

    /**
     * API Xóa một nhân viên khỏi hệ thống.
     * 
     * @param employeeId ID của nhân viên cần xóa
     * @return ResponseEntity chứa EmployeeResponse với thông báo xóa thành công
     * @throws ResourceNotFoundException Nếu nhân viên không tồn tại
     */
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponse> deleteEmployee(@PathVariable Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        
        EmployeeResponse employeeResponse = EmployeeResponse.builder()
                .code(String.valueOf(HttpStatus.OK.value()))
                .message(MessageResponse.builder()
                        .code(MessageCode.MSG_CODE_MSG003)
                        .params(new ArrayList<>())
                        .build())
                .build();
        return ResponseEntity.ok(employeeResponse);
    }

    /**
     * API Cập nhật thông tin cho nhân viên đã tồn tại.
     * 
     * @param employeeRequest Dữ liệu cập nhật nhân viên
     * @return ResponseEntity chứa EmployeeResponse với thông báo cập nhật thành công
     * @throws BaseException Nếu dữ liệu cập nhật không hợp lệ
     * @throws ResourceNotFoundException Nếu nhân viên không tồn tại
     */
    @PutMapping
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @RequestBody EmployeeRequest employeeRequest,
            @RequestParam(value = AppConstants.MODE, defaultValue = AppConstants.MODE_CONFIRM) String mode) {

        // Điều phối validate dựa trên mode
        MessageResponse validateRes;
        if (AppConstants.MODE_SUBMIT.equals(mode)) {
            validateRes = employeeValidator.validateForSubmit(employeeRequest);
        } else {
            validateRes = employeeValidator.validateForConfirm(employeeRequest);
        }

        if (validateRes != null) {
            throw new BaseException(validateRes, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        employeeService.updateEmployee(employeeRequest);

        EmployeeResponse employeeResponse = EmployeeResponse.builder()
                .code(String.valueOf(HttpStatus.OK.value()))
                .employeeId(employeeRequest.getEmployeeId())
                .message(MessageResponse.builder()
                        .code(MessageCode.MSG_CODE_MSG002)
                        .params(new ArrayList<>())
                        .build())
                .build();
        return ResponseEntity.ok(employeeResponse);
    }
}
