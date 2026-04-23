/**
 * Copyright(C) 2026 Luvina
 * [EmployeeController.java], 13/04/2026 tranledat
 */
package com.luvina.la.controller;

import com.luvina.la.dto.EmployeeDTO;
import com.luvina.la.payload.response.EmployeeListResponse;
import com.luvina.la.service.EmployeeService;
import com.luvina.la.validation.ValidateParamADM002;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.validation.EmployeeValidate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Controller quản lý các API liên quan đến nhân viên (Employee).
 * Cung cấp các endpoint để tìm kiếm, lấy danh sách nhân viên với phân trang và sắp xếp.
 *
 * @author tranledat
 */
@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private static final String CODE_SUCCESS = String.valueOf(HttpStatus.OK.value());
    private static final String ERROR_SORT_ORDER = "ER021";
    private static final String ERROR_PAGE_NOT_FOUND = "ER022";
    private static final String ERROR_SYSTEM = "ER023";
    private static final String ERROR_NUMERIC = "ER018";
    private static final String MSG_ADD_SUCCESS = "MSG001";
    private static final String MSG_EDIT_SUCCESS = "MSG002";
    private static final String OFFSET_PARAM_KEY = "employee.validation.offset.param";
    private static final String LIMIT_PARAM_KEY = "employee.validation.limit.param";
    private static final String DEPARTMENT_ID_PARAM_KEY = "employee.validation.department-id.param";
    private static final String DEFAULT_SORT_EMPLOYEE_NAME = "asc";
    private static final String DEFAULT_SORT_CERTIFICATION_NAME = "desc";
    private static final String DEFAULT_SORT_END_DATE = "asc";
    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 5;

    private final EmployeeService employeeService;
    private final MessageSource messageSource;
    private final java.util.Locale defaultLocale = Locale.JAPANESE; // Sử dụng Locale Nhật Bản theo yêu cầu project
    private final ValidateParamADM002 validateParamADM002;
    private final EmployeeValidate employeeValidate;

    /**
     * API validate dữ liệu cho màn hình ADM004 (Thêm mới/Chỉnh sửa nhân viên).
     *
     * @param request Dữ liệu nhân viên gửi từ form.
     * @return Kết quả validate (Thành công hoặc Lỗi đầu tiên phát hiện được).
     */
    @PostMapping("/employees/validate")
    public ResponseEntity<EmployeeListResponse> validateEmployee(@RequestBody EmployeeRequest request) {
        EmployeeListResponse response = new EmployeeListResponse();
        try {
            // Thực hiện validate logic nghiệp vụ và định dạng
            List<EmployeeValidate.ErrorItem> errors = employeeValidate.validate(request);

            if (!errors.isEmpty()) {
                List<EmployeeListResponse.FieldError> fieldErrors = new ArrayList<>();
                for (EmployeeValidate.ErrorItem error : errors) {
                    Object[] args = error.params != null ? error.params.toArray() : null;
                    String message = messageSource.getMessage(error.code, args, error.code, defaultLocale);
                    fieldErrors.add(new EmployeeListResponse.FieldError(error.field, message));
                }
                response.setFieldErrors(fieldErrors);
                response.setCode(errors.get(0).code);
                return ResponseEntity.ok(response);
            }

            // Nếu không có lỗi, trả về code 200 (Thành công)
            response.setCode(CODE_SUCCESS);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Xử lý lỗi hệ thống trong quá trình validate
            String message = messageSource.getMessage(ERROR_SYSTEM, null, ERROR_SYSTEM, defaultLocale);
            response.setCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            response.setMessage(message);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * API thực hiện lưu (Thêm mới hoặc Cập nhật) nhân viên.
     * Được gọi từ màn hình ADM005 sau khi người dùng nhấn OK.
     *
     * @param request Dữ liệu nhân viên đã được xác nhận.
     * @return Kết quả lưu (Thành công hoặc Lỗi hệ thống).
     */
    @PostMapping("/employees")
    public ResponseEntity<EmployeeListResponse> addEmployee(@RequestBody EmployeeRequest request) {
        EmployeeListResponse response = new EmployeeListResponse();
        try {
            // 1. Thực hiện validate lại lần cuối để đảm bảo an toàn
            List<EmployeeValidate.ErrorItem> errors = employeeValidate.validate(request);
            if (!errors.isEmpty()) {
                List<EmployeeListResponse.FieldError> fieldErrors = new ArrayList<>();
                for (EmployeeValidate.ErrorItem error : errors) {
                    Object[] args = error.params != null ? error.params.toArray() : null;
                    String message = messageSource.getMessage(error.code, args, error.code, defaultLocale);
                    fieldErrors.add(new EmployeeListResponse.FieldError(error.field, message));
                }
                response.setFieldErrors(fieldErrors);
                response.setCode(errors.get(0).code);
                return ResponseEntity.ok(response);
            }

            // 2. Gọi service Thêm mới
            employeeService.addEmployee(request);
            response.setCode(CODE_SUCCESS);
            response.setMessage(messageSource.getMessage(MSG_ADD_SUCCESS, null, MSG_ADD_SUCCESS, defaultLocale));
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Xử lý lỗi hệ thống
            String message = messageSource.getMessage(ERROR_SYSTEM, null, ERROR_SYSTEM, defaultLocale);
            response.setCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            response.setMessage(message);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    /**
     * API lấy danh sách nhân viên (ADM002).
     * Thực hiện tìm kiếm theo tên, phòng ban, sắp xếp và phân trang.
     *
     * @param employeeName Tên nhân viên cần tìm kiếm
     * @param departmentId ID phòng ban cần lọc
     * @param ordEmployeeName Thứ tự sắp xếp theo tên nhân viên (asc/desc)
     * @param ordCertificationName Thứ tự sắp xếp theo tên chứng chỉ (asc/desc)
     * @param ordEndDate Thứ tự sắp xếp theo ngày hết hạn (asc/desc)
     * @param offset Vị trí bắt đầu lấy dữ liệu
     * @param limit Số lượng bản ghi tối đa trả về
     * @return ResponseEntity chứa EmployeeListResponse (Code, Message, Data)
     */
    @GetMapping({"/employees"})
    public ResponseEntity<EmployeeListResponse> getEmployees(
            @RequestParam(value = "employee_name", required = false) String employeeName,
            @RequestParam(value = "department_id", required = false) String departmentId,
            @RequestParam(value = "ord_employee_name", required = false) String ordEmployeeName,
            @RequestParam(value = "ord_certification_name", required = false) String ordCertificationName,
            @RequestParam(value = "ord_end_date", required = false) String ordEndDate,
            @RequestParam(value = "offset", required = false) String offset,
            @RequestParam(value = "limit", required = false) String limit) {
        
        EmployeeListResponse response = new EmployeeListResponse();

        try {
            // 1. Validate các tham số sắp xếp (Sắp xếp theo Tên, Chứng chỉ, Ngày hết hạn)
            if (!validateParamADM002.isValidSortOrder(ordEmployeeName)
                    || !validateParamADM002.isValidSortOrder(ordCertificationName)
                    || !validateParamADM002.isValidSortOrder(ordEndDate)) {
                response.setCode(ERROR_SORT_ORDER);
                response.setMessage(messageSource.getMessage(ERROR_SORT_ORDER, null, ERROR_SORT_ORDER, defaultLocale));
                return ResponseEntity.ok(response);
            } 
            
            // 2. Validate tham số Offset (Phải là số nguyên không âm)
            else if (!validateParamADM002.isValidNonNegativeInteger(offset)) {
                String paramName = messageSource.getMessage(OFFSET_PARAM_KEY, null, OFFSET_PARAM_KEY, Locale.ROOT);
                response.setCode(ERROR_NUMERIC);
                response.setMessage(messageSource.getMessage(ERROR_NUMERIC, new Object[]{paramName}, ERROR_NUMERIC, defaultLocale));
                return ResponseEntity.ok(response);
            } 
            
            // 3. Validate tham số Limit (Phải là số nguyên dương)
            else if (!validateParamADM002.isValidPositiveInteger(limit)) {
                String paramName = messageSource.getMessage(LIMIT_PARAM_KEY, null, LIMIT_PARAM_KEY, Locale.ROOT);
                response.setCode(ERROR_NUMERIC);
                response.setMessage(messageSource.getMessage(ERROR_NUMERIC, new Object[]{paramName}, ERROR_NUMERIC, defaultLocale));
                return ResponseEntity.ok(response);
            } 
            
            // 4. Validate tham số DepartmentId (Nếu có truyền vào thì phải là số dương)
            else if (!validateParamADM002.isValidPositiveInteger(departmentId)) {
                String paramName = messageSource.getMessage(DEPARTMENT_ID_PARAM_KEY, null, DEPARTMENT_ID_PARAM_KEY, Locale.ROOT);
                response.setCode(ERROR_NUMERIC);
                response.setMessage(messageSource.getMessage(ERROR_NUMERIC, new Object[]{paramName}, ERROR_NUMERIC, defaultLocale));
                return ResponseEntity.ok(response);
            } 
            
            // Nếu tất cả tham số hợp lệ, tiến hành xử lý logic nghiệp vụ
            else {
                // Chuẩn hóa dữ liệu đầu vào (Trim, gán giá trị mặc định...)
                String normalizedEmployeeName = validateParamADM002.normalizeInput(employeeName);
                Long normalizedDepartmentId = validateParamADM002.parseLongOrNull(departmentId);
                Integer normalizedOffset = validateParamADM002.parseIntegerOrDefault(offset, DEFAULT_OFFSET);
                Integer normalizedLimit = validateParamADM002.parseIntegerOrDefault(limit, DEFAULT_LIMIT);
                String normalizedOrdEmployeeName = validateParamADM002.normalizeSortOrderOrDefault(ordEmployeeName, DEFAULT_SORT_EMPLOYEE_NAME);
                String normalizedOrdCertificationName = validateParamADM002.normalizeSortOrderOrDefault(ordCertificationName, DEFAULT_SORT_CERTIFICATION_NAME);
                String normalizedOrdEndDate = validateParamADM002.normalizeSortOrderOrDefault(ordEndDate, DEFAULT_SORT_END_DATE);

                // Lấy tổng số bản ghi thỏa mãn điều kiện lọc để phục vụ phân trang
                Long totalRecords = employeeService.countEmployeesWithFilter(normalizedEmployeeName, normalizedDepartmentId);
                List<EmployeeDTO> employees = new ArrayList<>();

                if (totalRecords > 0) {
                    // Kiểm tra nếu offset vượt quá tổng số bản ghi (Trang không tồn tại)
                    if (normalizedOffset.longValue() >= totalRecords) {
                        response.setCode(ERROR_PAGE_NOT_FOUND);
                        response.setMessage(messageSource.getMessage(ERROR_PAGE_NOT_FOUND, null, ERROR_PAGE_NOT_FOUND, defaultLocale));
                        return ResponseEntity.ok(response);
                    } else {
                        // Gọi Service thực hiện truy vấn danh sách nhân viên
                        employees = employeeService.getListEmployee(
                                normalizedEmployeeName,
                                normalizedDepartmentId,
                                normalizedOrdEmployeeName,
                                normalizedOrdCertificationName,
                                normalizedOrdEndDate,
                                normalizedLimit,
                                normalizedOffset
                        );
                        // Đóng gói kết quả thành công
                        response.setCode(CODE_SUCCESS);
                        response.setTotalRecords(totalRecords);
                        response.setEmployees(employees);
                        return ResponseEntity.ok(response);
                    }
                } else {
                    // Trường hợp không tìm thấy kết quả nào
                    response.setCode(CODE_SUCCESS);
                    response.setTotalRecords(0L);
                    response.setEmployees(new ArrayList<>());
                    return ResponseEntity.ok(response);
                }
            }
        } catch (Exception e) {
            // Xử lý lỗi hệ thống không mong muốn
            String message = messageSource.getMessage(ERROR_SYSTEM, null, ERROR_SYSTEM, defaultLocale);
            response.setCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            response.setMessage(message);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
