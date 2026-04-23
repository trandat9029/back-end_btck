/**
 * Copyright(C) 2026 Luvina
 * [EmployeeValidate.java], 23/04/2026 tranledat
 */
package com.luvina.la.validation;

import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.service.CertificationService;
import com.luvina.la.service.DepartmentService;
import com.luvina.la.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.context.MessageSource;

/**
 * Lớp Validate nghiệp vụ cho màn hình ADM004 (Thêm/Sửa nhân viên).
 *
 * @author tranledat
 */
@Component
@RequiredArgsConstructor
public class EmployeeValidate {

    @org.springframework.beans.factory.annotation.Autowired
    @Lazy
    private EmployeeService employeeService;

    private final DepartmentService departmentService;
    private final CertificationService certificationService;
    private final MessageSource messageSource;

    private final java.util.Locale defaultLocale = java.util.Locale.JAPANESE;

    /**
     * Inner class đóng gói lỗi.
     */
    public static class ErrorItem {
        public String field;
        public String code;
        public List<String> params;

        public ErrorItem(String field, String code, List<String> params) {
            this.field = field;
            this.code = code;
            this.params = params;
        }

        public ErrorItem(String field, String code) {
            this(field, code, null);
        }
    }

    /**
     * Thực hiện validate toàn bộ dữ liệu đầu vào.
     */
    public List<ErrorItem> validate(EmployeeRequest request) {
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
    private void validateLoginId(EmployeeRequest request, List<ErrorItem> errors) {
        String loginId = request.getEmployeeLoginId();
        String label = messageSource.getMessage("label.employeeLoginId", null, "アカウント名", defaultLocale);
        
        if (isEmpty(loginId)) {
            errors.add(new ErrorItem("employeeLoginId", "ER001", Collections.singletonList(label)));
            return;
        }

        if (loginId.length() > 50) {
            errors.add(new ErrorItem("employeeLoginId", "ER006", Arrays.asList(label, "50")));
        }

        if (!loginId.matches("^[a-zA-Z_][a-zA-Z0-9_]*$") || Character.isDigit(loginId.charAt(0))) {
            errors.add(new ErrorItem("employeeLoginId", "ER019"));
        } else {
            if (employeeService.checkExistsEmployeeByLoginId(loginId, request.getEmployeeId())) {
                errors.add(new ErrorItem("employeeLoginId", "ER003", Collections.singletonList(label)));
            }
        }
    }

    /**
     * 2. Validate departmentId (Nhóm).
     */
    private void validateDepartment(EmployeeRequest request, List<ErrorItem> errors) {
        String label = messageSource.getMessage("label.departmentName", null, "グループ", defaultLocale);
        if (request.getDepartmentId() == null) {
            errors.add(new ErrorItem("departmentId", "ER002", Collections.singletonList(label)));
        } else {
            if (!departmentService.checkExistsDepartmentById(request.getDepartmentId())) {
                errors.add(new ErrorItem("departmentId", "ER004", Collections.singletonList(label)));
            }
        }
    }

    /**
     * 3. Validate thông tin chứng chỉ (Nếu có chọn chứng chỉ).
     */
    private void validateCertification(EmployeeRequest request, List<ErrorItem> errors) {
        if (request.getCertificationId() == null) {
            return;
        }

        if (!certificationService.checkExistsCertificationById(request.getCertificationId())) {
            errors.add(new ErrorItem("certificationId", "ER004"));
        }

        // Validate Ngày cấp
        String startLabel = messageSource.getMessage("label.certificationStartDate", null, "資格交付日", defaultLocale);
        if (isEmpty(request.getCertificationStartDate())) {
            errors.add(new ErrorItem("certificationStartDate", "ER001", Collections.singletonList(startLabel)));
        } else if (!isValidDate(request.getCertificationStartDate())) {
            errors.add(new ErrorItem("certificationStartDate", "ER011", Collections.singletonList(startLabel)));
        }

        // Validate Ngày hết hạn
        String endLabel = messageSource.getMessage("label.certificationEndDate", null, "失効日", defaultLocale);
        if (isEmpty(request.getCertificationEndDate())) {
            errors.add(new ErrorItem("certificationEndDate", "ER001", Collections.singletonList(endLabel)));
        } else if (!isValidDate(request.getCertificationEndDate())) {
            errors.add(new ErrorItem("certificationEndDate", "ER011", Collections.singletonList(endLabel)));
        } else {
            // Logic so sánh Ngày bắt đầu và Ngày kết thúc (Nếu cả 2 đều hợp lệ)
            if (isValidDate(request.getCertificationStartDate())) {
                java.time.LocalDate start = java.time.LocalDate.parse(request.getCertificationStartDate());
                java.time.LocalDate end = java.time.LocalDate.parse(request.getCertificationEndDate());
                if (!end.isAfter(start)) {
                    errors.add(new ErrorItem("certificationEndDate", "ER012", Collections.singletonList(startLabel)));
                }
            }
        }

        // Validate Điểm số
        String scoreLabel = messageSource.getMessage("label.employeeCertificationScore", null, "スコア", defaultLocale);
        if (isEmpty(request.getEmployeeCertificationScore())) {
            errors.add(new ErrorItem("employeeCertificationScore", "ER001", Collections.singletonList(scoreLabel)));
        } else {
            String scoreStr = request.getEmployeeCertificationScore();
            if (!scoreStr.matches("^[0-9]+(\\.[0-9]+)?$")) { // Kiểm tra là số bán giác (half-width)
                errors.add(new ErrorItem("employeeCertificationScore", "ER018", Collections.singletonList(scoreLabel)));
            } else {
                try {
                    double score = Double.parseDouble(scoreStr);
                    if (score <= 0) {
                        errors.add(new ErrorItem("employeeCertificationScore", "ER018", Collections.singletonList(scoreLabel)));
                    }
                } catch (NumberFormatException e) {
                    errors.add(new ErrorItem("employeeCertificationScore", "ER018", Collections.singletonList(scoreLabel)));
                }
            }
        }
    }

    /**
     * 4. Validate mật khẩu và xác nhận mật khẩu.
     */
    private void validatePassword(EmployeeRequest request, List<ErrorItem> errors) {
        String password = request.getEmployeeLoginPassword();
        String confirm = request.getEmployeeLoginPasswordConfirm();
        String label = messageSource.getMessage("label.employeeLoginPassword", null, "パスワード", defaultLocale);

        if (request.getEmployeeId() == null) { // Thêm mới
            if (isEmpty(password)) {
                errors.add(new ErrorItem("employeeLoginPassword", "ER001", Collections.singletonList(label)));
            } else {
                if (password.length() < 8 || password.length() > 50) {
                    errors.add(new ErrorItem("employeeLoginPassword", "ER007", Arrays.asList(label, "8", "50")));
                }
                if (!password.matches("^[a-zA-Z0-9]*$")) { // Kiểm tra ký tự bán giác (half-width alphanumeric)
                    errors.add(new ErrorItem("employeeLoginPassword", "ER008", Collections.singletonList(label)));
                }
            }
        } else { // Cập nhật
            if (!isEmpty(password)) {
                if (password.length() < 8 || password.length() > 50) {
                    errors.add(new ErrorItem("employeeLoginPassword", "ER007", Arrays.asList(label, "8", "50")));
                }
                if (!password.matches("^[a-zA-Z0-9]*$")) {
                    errors.add(new ErrorItem("employeeLoginPassword", "ER008", Collections.singletonList(label)));
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
    private void validatePersonalDetails(EmployeeRequest request, List<ErrorItem> errors) {
        // 4.1 Họ và tên
        String nameLabel = messageSource.getMessage("label.employeeName", null, "氏名", defaultLocale);
        if (isEmpty(request.getEmployeeName())) {
            errors.add(new ErrorItem("employeeName", "ER001", Collections.singletonList(nameLabel)));
        } else if (request.getEmployeeName().length() > 125) {
            errors.add(new ErrorItem("employeeName", "ER006", Arrays.asList(nameLabel, "125")));
        }

        // 4.2 Tên Kana
        String kanaLabel = messageSource.getMessage("label.employeeNameKana", null, "カタカナ氏名", defaultLocale);
        if (isEmpty(request.getEmployeeNameKana())) {
            errors.add(new ErrorItem("employeeNameKana", "ER001", Collections.singletonList(kanaLabel)));
        } else {
            if (request.getEmployeeNameKana().length() > 125) {
                errors.add(new ErrorItem("employeeNameKana", "ER006", Arrays.asList(kanaLabel, "125")));
            }
            if (!request.getEmployeeNameKana().matches("^[ァ-ンヴー]*$")) {
                errors.add(new ErrorItem("employeeNameKana", "ER009", Collections.singletonList(kanaLabel)));
            }
        }

        // 4.3 Ngày sinh
        String birthDateLabel = messageSource.getMessage("label.employeeBirthDate", null, "生年月日", defaultLocale);
        if (isEmpty(request.getEmployeeBirthDate())) {
            errors.add(new ErrorItem("employeeBirthDate", "ER001", Collections.singletonList(birthDateLabel)));
        } else if (!isValidDate(request.getEmployeeBirthDate())) {
            errors.add(new ErrorItem("employeeBirthDate", "ER011", Collections.singletonList(birthDateLabel)));
        }

        // 4.4 Email
        String emailLabel = messageSource.getMessage("label.employeeEmail", null, "メールアドレス", defaultLocale);
        if (isEmpty(request.getEmployeeEmail())) {
            errors.add(new ErrorItem("employeeEmail", "ER001", Collections.singletonList(emailLabel)));
        } else {
            if (request.getEmployeeEmail().length() > 125) {
                errors.add(new ErrorItem("employeeEmail", "ER006", Arrays.asList(emailLabel, "125")));
            }
            if (!request.getEmployeeEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                errors.add(new ErrorItem("employeeEmail", "ER005", Arrays.asList(emailLabel, "メールアドレス")));
            }
        }

        // 4.5 Số điện thoại
        String telLabel = messageSource.getMessage("label.employeeTelephone", null, "電話番号", defaultLocale);
        if (isEmpty(request.getEmployeeTelephone())) {
            errors.add(new ErrorItem("employeeTelephone", "ER001", Collections.singletonList(telLabel)));
        } else {
            if (request.getEmployeeTelephone().length() > 50) {
                errors.add(new ErrorItem("employeeTelephone", "ER006", Arrays.asList(telLabel, "50")));
            }
            if (!request.getEmployeeTelephone().matches("^[0-9-]*$")) {
                errors.add(new ErrorItem("employeeTelephone", "ER005", Arrays.asList(telLabel, "電話番号")));
            }
        }
    }
}
