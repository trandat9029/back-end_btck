/**
 * Copyright(C) 2026 - Luvina
 * [EmployeeController.java], 24/04/2026 [tranledat]
 */
package com.luvina.la.controller;

import com.luvina.la.config.Constants;
import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.exception.CustomException;
import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.payload.response.EmployeeDetailResponse;
import com.luvina.la.payload.response.EmployeeListResponse;
import com.luvina.la.payload.response.MessageResponse;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validation.EmployeeValidate;
import com.luvina.la.validation.ParamValidate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Controller quản lý các API liên quan đến nhân viên (Employee).
 * @author tranledat
 */
@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final MessageSource messageSource;
    private final ParamValidate paramValidate;
    private final EmployeeValidate employeeValidate;
    private final Locale defaultLocale = Locale.JAPANESE;

    /**
     * API validate dữ liệu cho màn hình ADM004/ADM005.
     * @param request Dữ liệu nhân viên gửi từ form.
     * @return Kết quả validate (Danh sách lỗi hoặc mã thành công).
     */
    @PostMapping("/employees/validate")
    public ResponseEntity<Object> validateEmployee(@RequestBody EmployeeRequest request) {
        List<MessageResponse> errors = employeeValidate.validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok(new MessageResponse(Constants.CODE_SUCCESS, null));
    }

    /**
     * API thực hiện lưu (Thêm mới hoặc Cập nhật) nhân viên.
     * @param request Dữ liệu nhân viên đã được xác nhận.
     * @return Kết quả lưu thành công.
     */
    @PostMapping("/employees")
    public ResponseEntity<Object> addEmployee(@RequestBody EmployeeRequest request) {
        // 1. Thực hiện validate lại lần cuối để đảm bảo an toàn
        List<MessageResponse> errors = employeeValidate.validate(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // 2. Gọi service Thêm mới/Cập nhật
        employeeService.addEmployee(request);
        
        String successMsg = messageSource.getMessage("MSG001", null, "Add Success", defaultLocale);
        return ResponseEntity.ok(new MessageResponse(Constants.CODE_SUCCESS, successMsg));
    }

    /**
     * API lấy danh sách nhân viên (ADM002).
     * @param employeeName Tên nhân viên cần tìm kiếm
     * @param departmentId ID phòng ban cần lọc
     * @param ordEmployeeName Thứ tự sắp xếp theo tên (asc/desc)
     * @param ordCertificationName Thứ tự sắp xếp theo tên chứng chỉ (asc/desc)
     * @param ordEndDate Thứ tự sắp xếp theo ngày hết hạn (asc/desc)
     * @param offset Vị trí bắt đầu lấy dữ liệu
     * @param limit Số lượng bản ghi tối đa
     * @return ResponseEntity chứa danh sách nhân viên và tổng số bản ghi.
     */
    @GetMapping("/employees")
    public ResponseEntity<EmployeeListResponse> getEmployees(
            @RequestParam(value = "employee_name", required = false) String employeeName,
            @RequestParam(value = "department_id", required = false) String departmentId,
            @RequestParam(value = "ord_employee_name", required = false) String ordEmployeeName,
            @RequestParam(value = "ord_certification_name", required = false) String ordCertificationName,
            @RequestParam(value = "ord_end_date", required = false) String ordEndDate,
            @RequestParam(value = "offset", required = false) String offset,
            @RequestParam(value = "limit", required = false) String limit) {

        // 1. Validate các tham số đầu vào bằng hàm tập trung trong lớp Validate
        paramValidate.checkValidateParams(ordEmployeeName, ordCertificationName, ordEndDate, offset, limit, departmentId);

        // 2. Thực hiện chuẩn hóa dữ liệu đầu vào (Trim, gán giá trị mặc định)
        String name = paramValidate.normalizeInput(employeeName);
        Long deptId = paramValidate.parseLongOrNull(departmentId);
        Integer off = paramValidate.parseIntegerOrDefault(offset, 0);
        Integer lim = paramValidate.parseIntegerOrDefault(limit, 5);
        String sortName = paramValidate.normalizeSortOrderOrDefault(ordEmployeeName, "asc");
        String sortCert = paramValidate.normalizeSortOrderOrDefault(ordCertificationName, "desc");
        String sortEnd = paramValidate.normalizeSortOrderOrDefault(ordEndDate, "asc");

        // 3. Xử lý logic lấy danh sách
        Long totalRecords = employeeService.countEmployeesWithFilter(name, deptId);
        List<EmployeeDTO> employees = new ArrayList<>();

        if (totalRecords > 0) {
            if (off.longValue() >= totalRecords) {
                throw new CustomException(Constants.CODE_ERROR_PAGE_NOT_FOUND);
            }
            employees = employeeService.getListEmployee(
                    name, deptId, sortName, sortCert, sortEnd, lim, off
            );
        }

        // 4. Đóng gói response
        EmployeeListResponse response = new EmployeeListResponse();
        response.setCode(Constants.CODE_SUCCESS);
        response.setTotalRecords(totalRecords);
        response.setEmployees(employees);
        return ResponseEntity.ok(response);
    }

    /**
     * API lấy thông tin chi tiết nhân viên (ADM004 - Edit mode).
     * @param id ID của nhân viên cần lấy thông tin.
     * @return ResponseEntity chứa thông tin chi tiết nhân viên.
     */
    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeDetailResponse> getEmployee(@PathVariable("id") Long id) {
        // 1. Validate ID 
        if (id == null) {
            throw new CustomException("ER001", "ID");
        }
        
        // 2. Gọi service lấy dữ liệu (Nếu không tìm thấy, Service sẽ ném CustomException ER013)
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    /**
     * API xóa nhân viên (ADM003).
     * @param id ID của nhân viên cần xóa.
     * @return ResponseEntity thông báo thành công.
     */
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Object> deleteEmployee(@PathVariable("id") Long id) {
        // 1. Gọi service thực hiện xóa
        employeeService.deleteEmployee(id);
        
        String successMsg = messageSource.getMessage("MSG003", null, "Delete Success", Locale.JAPANESE);
        return ResponseEntity.ok(new MessageResponse(Constants.CODE_SUCCESS, successMsg));
    }
}
