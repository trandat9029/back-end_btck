/**
 * Copyright(C) 2026 - Luvina
 * [EmployeeController.java], 24/04/2026 tranledat
 */
package com.luvina.la.controller;

import com.luvina.la.config.Constants;
import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.exception.CustomException;
import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.payload.response.EmployeeDetailResponse;
import com.luvina.la.payload.response.EmployeeListResponse;
import com.luvina.la.payload.response.MessageResponse;
import com.luvina.la.payload.response.UpdateEmployeeResponse;
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
     * API thực hiện thêm mới thông tin nhân viên vào hệ thống.
     * @param request Đối tượng EmployeeRequest chứa toàn bộ thông tin nhân viên từ Form.
     * @return ResponseEntity Đối tượng chứa mã trạng thái và thông báo kết quả thực hiện (code 200, employeeId, message object).
     */
    @PostMapping("/employees")
    public ResponseEntity<Object> addEmployee(@RequestBody EmployeeRequest request) {
        // 1. Gọi service Thêm mới
        Long newId = employeeService.addEmployee(request);
        
        // 2. Trả về response theo đúng cấu trúc thiết kế
        return ResponseEntity.ok(UpdateEmployeeResponse.success(newId, "MSG001"));
    }

    /**
     * API thực hiện cập nhật thông tin nhân viên vào hệ thống.
     * @param id ID của nhân viên cần cập nhật.
     * @param request Đối tượng EmployeeRequest chứa toàn bộ thông tin nhân viên từ Form.
     * @return ResponseEntity Đối tượng chứa mã trạng thái và thông báo kết quả thực hiện (code 200, employeeId, message object).
     */
    @PutMapping("/employees/{id}")
    public ResponseEntity<Object> updateEmployee(@PathVariable("id") Long id, @RequestBody EmployeeRequest request) {
        // 1. Gán ID từ PathVariable vào request để đồng bộ
        request.setEmployeeId(id);

        // 2. Gọi service Cập nhật
        Long updatedId = employeeService.updateEmployee(request);
        
        // 3. Trả về response theo đúng cấu trúc thiết kế
        return ResponseEntity.ok(UpdateEmployeeResponse.success(updatedId, "MSG002"));
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
                throw new CustomException(Constants.CODE_ER022);
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
     * API thực hiện lấy thông tin chi tiết của một nhân viên phục vụ màn hình xem chi tiết (ADM003) hoặc chỉnh sửa (ADM004).
     * @param id Mã ID của nhân viên cần thực hiện truy vấn thông tin.
     * @return ResponseEntity Đối tượng EmployeeDetailResponse chứa toàn bộ thông tin chi tiết của nhân viên.
     */
    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeDetailResponse> getEmployee(@PathVariable("id") Long id) {
        // 1. Validate ID 
        if (id == null) {
            throw new CustomException(Constants.CODE_ER001, "ID");
        }
        
        // 2. Gọi service lấy dữ liệu (Nếu không tìm thấy, Service sẽ ném CustomException ER013)
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    /**
     * API thực hiện xóa thông tin một nhân viên khỏi hệ thống dựa trên ID nhân viên.
     * @param id Mã ID của nhân viên cần thực hiện xóa.
     * @return ResponseEntity Đối tượng chứa mã trạng thái và thông báo kết quả xóa thành công.
     */
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Object> deleteEmployee(@PathVariable("id") Long id) {
        // 1. Gọi service thực hiện xóa
        employeeService.deleteEmployee(id);
        
        String successMsg = messageSource.getMessage("MSG003", null, "Delete Success", Locale.JAPANESE);
        return ResponseEntity.ok(new MessageResponse(Constants.CODE_SUCCESS, successMsg));
    }
}
