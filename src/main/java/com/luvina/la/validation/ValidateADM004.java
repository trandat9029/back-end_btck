/**
 * Copyright(C) 2026 Luvina
 * [ValidateADM004.java], 22/04/2026 tranledat
 */
package com.luvina.la.validation;

import com.luvina.la.dto.EmployeeRequestDTO;
import com.luvina.la.service.CertificationService;
import com.luvina.la.service.DepartmentService;
import com.luvina.la.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Validate nghiệp vụ cho màn hình ADM004 (Thêm/Sửa nhân viên).
 *
 * @author tranledat
 */
@Component
@RequiredArgsConstructor
public class ValidateADM004 {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final CertificationService certificationService;

    /**
     * Inner class đóng gói lỗi.
     */
    public static class ErrorItem {
        public String field;
        public String code;

        public ErrorItem(String field, String code) {
            this.field = field;
            this.code = code;
        }
    }

    /**
     * Thực hiện validate toàn bộ dữ liệu đầu vào.
     */
    public List<ErrorItem> validate(EmployeeRequestDTO request) {
        List<ErrorItem> errors = new ArrayList<>();

        // 1. Validate Tên tài khoản
        validateLoginId(request, errors);

        // 2. Validate Nhóm
        validateDepartment(request, errors);

        // 3. Validate Thông tin cá nhân (Tên, Tên Kana, Ngày sinh, Email, SĐT)
        validatePersonalDetails(request, errors);

        // 4. Validate Mật khẩu
        validatePassword(request, errors);

        // 5. Validate Chứng chỉ
        validateCertification(request, errors);

        return errors;
    }

    /**
     * Kiểm tra chuỗi trống.
     */
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Kiểm tra định dạng ngày yyyy-MM-dd.
     */
    private boolean isValidDate(String dateStr) {
        if (dateStr == null) return false;
        try {
            java.time.LocalDate.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (java.time.format.DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 1. Validate employeeLoginId (Tên tài khoản).
     */
    private void validateLoginId(EmployeeRequestDTO request, List<ErrorItem> errors) {
        String loginId = request.getEmployeeLoginId();
        if (isEmpty(loginId)) {
            errors.add(new ErrorItem("employeeLoginId", "ER001"));
            return;
        }

        if (loginId.length() > 50) {
            errors.add(new ErrorItem("employeeLoginId", "ER006"));
        }

        if (!loginId.matches("^[a-zA-Z_][a-zA-Z0-9_]*$") || Character.isDigit(loginId.charAt(0))) {
            errors.add(new ErrorItem("employeeLoginId", "ER019"));
        } else {
            if (employeeService.checkExistsEmployeeByLoginId(loginId, request.getEmployeeId())) {
                errors.add(new ErrorItem("employeeLoginId", "ER003"));
            }
        }
    }

    /**
     * 2. Validate departmentId (Nhóm).
     */
    private void validateDepartment(EmployeeRequestDTO request, List<ErrorItem> errors) {
        if (request.getDepartmentId() == null) {
            errors.add(new ErrorItem("departmentId", "ER002"));
        } else {
            if (!departmentService.checkExistsDepartmentById(request.getDepartmentId())) {
                errors.add(new ErrorItem("departmentId", "ER004"));
            }
        }
    }

    /**
     * 3. Validate thông tin chứng chỉ (Nếu có chọn chứng chỉ).
     */
    private void validateCertification(EmployeeRequestDTO request, List<ErrorItem> errors) {
        if (request.getCertificationId() == null) {
            return;
        }

        if (!certificationService.checkExistsCertificationById(request.getCertificationId())) {
            errors.add(new ErrorItem("certificationId", "ER004"));
        }

        // Validate Ngày cấp
        if (isEmpty(request.getCertificationStartDate())) {
            errors.add(new ErrorItem("certificationStartDate", "ER001"));
        } else if (!isValidDate(request.getCertificationStartDate())) {
            errors.add(new ErrorItem("certificationStartDate", "ER011"));
        }

        // Validate Ngày hết hạn
        if (isEmpty(request.getCertificationEndDate())) {
            errors.add(new ErrorItem("certificationEndDate", "ER001"));
        } else if (!isValidDate(request.getCertificationEndDate())) {
            errors.add(new ErrorItem("certificationEndDate", "ER011"));
        } else {
            // Logic so sánh Ngày bắt đầu và Ngày kết thúc (Nếu cả 2 đều hợp lệ)
            if (isValidDate(request.getCertificationStartDate())) {
                java.time.LocalDate start = java.time.LocalDate.parse(request.getCertificationStartDate());
                java.time.LocalDate end = java.time.LocalDate.parse(request.getCertificationEndDate());
                if (!end.isAfter(start)) {
                    errors.add(new ErrorItem("certificationEndDate", "ER012"));
                }
            }
        }

        // Validate Điểm số
        if (isEmpty(request.getEmployeeCertificationScore())) {
            errors.add(new ErrorItem("employeeCertificationScore", "ER001"));
        } else {
            try {
                double score = Double.parseDouble(request.getEmployeeCertificationScore());
                if (score <= 0) {
                    errors.add(new ErrorItem("employeeCertificationScore", "ER018"));
                }
            } catch (NumberFormatException e) {
                errors.add(new ErrorItem("employeeCertificationScore", "ER018"));
            }
        }
    }

    /**
     * 4. Validate mật khẩu và xác nhận mật khẩu.
     */
    private void validatePassword(EmployeeRequestDTO request, List<ErrorItem> errors) {
        String password = request.getEmployeeLoginPassword();
        String confirm = request.getEmployeeLoginPasswordConfirm();

        if (request.getEmployeeId() == null) { // Thêm mới
            if (isEmpty(password)) {
                errors.add(new ErrorItem("employeeLoginPassword", "ER001"));
            } else if (password.length() < 8 || password.length() > 50) {
                errors.add(new ErrorItem("employeeLoginPassword", "ER007"));
            }
        } else { // Cập nhật
            if (!isEmpty(password)) {
                if (password.length() < 8 || password.length() > 50) {
                    errors.add(new ErrorItem("employeeLoginPassword", "ER007"));
                }
            }
        }

        if (!isEmpty(password) && !password.equals(confirm)) {
            errors.add(new ErrorItem("employeeLoginPasswordConfirm", "ER017"));
        }
    }

    /**
     * 4. Validate các thông tin cá nhân.
     */
    private void validatePersonalDetails(EmployeeRequestDTO request, List<ErrorItem> errors) {
        // 4.1 Họ và tên
        if (isEmpty(request.getEmployeeName())) {
            errors.add(new ErrorItem("employeeName", "ER001"));
        } else if (request.getEmployeeName().length() > 125) {
            errors.add(new ErrorItem("employeeName", "ER006"));
        }

        // 4.2 Tên Kana
        if (isEmpty(request.getEmployeeNameKana())) {
            errors.add(new ErrorItem("employeeNameKana", "ER001"));
        } else {
            if (request.getEmployeeNameKana().length() > 125) {
                errors.add(new ErrorItem("employeeNameKana", "ER006"));
            }
            if (!request.getEmployeeNameKana().matches("^[ァ-ンヴー]*$")) {
                errors.add(new ErrorItem("employeeNameKana", "ER009"));
            }
        }

        // 4.3 Ngày sinh
        if (isEmpty(request.getEmployeeBirthDate())) {
            errors.add(new ErrorItem("employeeBirthDate", "ER001"));
        } else if (!isValidDate(request.getEmployeeBirthDate())) {
            errors.add(new ErrorItem("employeeBirthDate", "ER011"));
        }

        // 4.4 Email
        if (isEmpty(request.getEmployeeEmail())) {
            errors.add(new ErrorItem("employeeEmail", "ER001"));
        } else {
            if (request.getEmployeeEmail().length() > 125) {
                errors.add(new ErrorItem("employeeEmail", "ER006"));
            }
            if (!request.getEmployeeEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                errors.add(new ErrorItem("employeeEmail", "ER005"));
            }
        }

        // 4.5 Số điện thoại
        if (isEmpty(request.getEmployeeTelephone())) {
            errors.add(new ErrorItem("employeeTelephone", "ER001"));
        } else {
            if (request.getEmployeeTelephone().length() > 50) {
                errors.add(new ErrorItem("employeeTelephone", "ER006"));
            }
            if (!request.getEmployeeTelephone().matches("^[0-9-]*$")) {
                errors.add(new ErrorItem("employeeTelephone", "ER005"));
            }
        }
    }
}
