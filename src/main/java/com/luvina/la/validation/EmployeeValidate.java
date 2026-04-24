/**
 * Copyright(C) 2026 Luvina
 * [EmployeeValidate.java], 23/04/2026 tranledat
 */
package com.luvina.la.validation;

import com.luvina.la.config.Constants;
import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.payload.response.MessageResponse;
import com.luvina.la.service.CertificationService;
import com.luvina.la.service.DepartmentService;
import com.luvina.la.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Lớp Validate nghiệp vụ cho màn hình ADM004/ADM005.
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

    private final Locale defaultLocale = Locale.JAPANESE;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * Thực hiện validate toàn bộ dữ liệu đầu vào và gom tất cả lỗi.
     * @param request Dữ liệu từ Client gửi lên.
     * @return Danh sách các lỗi phát hiện được.
     */
    public List<MessageResponse> validate(EmployeeRequest request) {
        List<MessageResponse> errors = new ArrayList<>();

        // 1. Validate Tên tài khoản
        validateLoginId(request, errors);

        // 2. Validate Nhóm
        validateDepartment(request, errors);

        // 3. Validate Thông tin cá nhân (Tên, Tên Kana, Ngày sinh, Email, SĐT)
        validatePersonalDetails(request, errors);

        // 4. Validate Mật khẩu
        validatePassword(request, errors);

        // 5. Validate Chứng chỉ (Nếu có chọn)
        validateCertification(request, errors);

        return errors;
    }

    /**
     * Kiểm tra chuỗi có trống hay không.
     * @param str Chuỗi cần kiểm tra.
     * @return true nếu trống, false nếu không.
     */
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Kiểm tra độ dài tối đa của chuỗi.
     * @param value Giá trị cần kiểm tra.
     * @param maxLength Độ dài tối đa cho phép.
     * @return true nếu hợp lệ, false nếu vượt quá.
     */
    private boolean isValidMaxLength(String value, int maxLength) {
        return value == null || value.length() <= maxLength;
    }

    /**
     * Kiểm tra độ dài tối thiểu của chuỗi.
     * @param value Giá trị cần kiểm tra.
     * @param minLength Độ dài tối thiểu yêu cầu.
     * @return true nếu hợp lệ, false nếu không đủ độ dài.
     */
    private boolean isValidMinLength(String value, int minLength) {
        return value != null && value.length() >= minLength;
    }

    /**
     * Kiểm tra định dạng ngày tháng (yyyy/MM/dd).
     * @param date Chuỗi ngày tháng cần kiểm tra.
     * @return true nếu đúng định dạng, false nếu sai.
     */
    private boolean isValidDateFormat(String date) {
        if (isEmpty(date)) return true;
        try {
            LocalDate.parse(date, dateFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Kiểm tra chuỗi có phải là số Half-size hay không.
     * @param text Chuỗi cần kiểm tra.
     * @return true nếu là số Half-size, false nếu không.
     */
    private boolean isHalfsizeNumber(String text) {
        return isEmpty(text) || Pattern.matches(Constants.HALFSIDE_NUMBER_PATTERN, text);
    }

    /**
     * Validate trường Login ID.
     * @param request Dữ liệu request.
     * @param errors Danh sách lỗi để add vào.
     */
    private void validateLoginId(EmployeeRequest request, List<MessageResponse> errors) {
        String loginId = request.getEmployeeLoginId();
        String label = getLabel(Constants.LABEL_LOGIN_ID, Constants.DEFAULT_LABEL_LOGIN_ID);
        String field = Constants.FIELD_LOGIN_ID;

        if (isEmpty(loginId)) {
            errors.add(new MessageResponse("ER001", Collections.singletonList(label), field));
        } else if (!isValidMaxLength(loginId, 50)) {
            errors.add(new MessageResponse("ER006", Arrays.asList(label, "50"), field));
        } else if (!Pattern.matches(Constants.LOGIN_ID_PATTERN, loginId) || Character.isDigit(loginId.charAt(0))) {
            errors.add(new MessageResponse("ER019", null, field));
        } else if (employeeService.checkExistsEmployeeByLoginId(loginId, request.getEmployeeId())) {
            errors.add(new MessageResponse("ER003", Collections.singletonList(label), field));
        }
    }

    /**
     * Validate trường Phòng ban (Department).
     * @param request Dữ liệu request.
     * @param errors Danh sách lỗi để add vào.
     */
    private void validateDepartment(EmployeeRequest request, List<MessageResponse> errors) {
        String label = getLabel(Constants.LABEL_DEPARTMENT_NAME, Constants.DEFAULT_LABEL_DEPARTMENT);
        String field = Constants.FIELD_DEPARTMENT_ID;

        if (request.getDepartmentId() == null) {
            errors.add(new MessageResponse("ER002", Collections.singletonList(label), field));
        } else if (!departmentService.checkExistsDepartmentById(request.getDepartmentId())) {
            errors.add(new MessageResponse("ER004", Collections.singletonList(label), field));
        }
    }

    /**
     * Validate các thông tin cá nhân cơ bản.
     * @param request Dữ liệu request.
     * @param errors Danh sách lỗi để add vào.
     */
    private void validatePersonalDetails(EmployeeRequest request, List<MessageResponse> errors) {
        // Họ và tên
        String nameLabel = getLabel(Constants.LABEL_EMPLOYEE_NAME, Constants.DEFAULT_LABEL_NAME);
        String nameField = Constants.FIELD_NAME;
        if (isEmpty(request.getEmployeeName())) {
            errors.add(new MessageResponse("ER001", Collections.singletonList(nameLabel), nameField));
        } else if (!isValidMaxLength(request.getEmployeeName(), 125)) {
            errors.add(new MessageResponse("ER006", Arrays.asList(nameLabel, "125"), nameField));
        }

        // Tên Kana (Yêu cầu Half-size Katakana)
        String kanaLabel = getLabel(Constants.LABEL_EMPLOYEE_NAME_KANA, Constants.DEFAULT_LABEL_NAME_KANA);
        String kanaField = Constants.FIELD_NAME_KANA;
        if (isEmpty(request.getEmployeeNameKana())) {
            errors.add(new MessageResponse("ER001", Collections.singletonList(kanaLabel), kanaField));
        } else {
            if (!isValidMaxLength(request.getEmployeeNameKana(), 125)) {
                errors.add(new MessageResponse("ER006", Arrays.asList(kanaLabel, "125"), kanaField));
            }
            if (!Pattern.matches(Constants.HALFSIDE_KATAKANA_PATTERN, request.getEmployeeNameKana())) {
                errors.add(new MessageResponse("ER009", Collections.singletonList(kanaLabel), kanaField));
            }
        }

        // Ngày sinh
        String birthLabel = getLabel(Constants.LABEL_BIRTH_DATE, Constants.DEFAULT_LABEL_BIRTH_DATE);
        String birthField = Constants.FIELD_BIRTH_DATE;
        if (isEmpty(request.getEmployeeBirthDate())) {
            errors.add(new MessageResponse("ER001", Collections.singletonList(birthLabel), birthField));
        } else if (!isValidDateFormat(request.getEmployeeBirthDate())) {
            errors.add(new MessageResponse("ER011", Collections.singletonList(birthLabel), birthField));
        }

        // Email
        String emailLabel = getLabel(Constants.LABEL_EMAIL, Constants.DEFAULT_LABEL_EMAIL);
        String emailField = Constants.FIELD_EMAIL;
        if (isEmpty(request.getEmployeeEmail())) {
            errors.add(new MessageResponse("ER001", Collections.singletonList(emailLabel), emailField));
        } else {
            if (!isValidMaxLength(request.getEmployeeEmail(), 125)) {
                errors.add(new MessageResponse("ER006", Arrays.asList(emailLabel, "125"), emailField));
            }
            if (!Pattern.matches(Constants.EMAIL_PATTERN, request.getEmployeeEmail())) {
                errors.add(new MessageResponse("ER005", Arrays.asList(emailLabel, "メールアドレス"), emailField));
            }
        }

        // Số điện thoại
        String telLabel = getLabel(Constants.LABEL_TELEPHONE, Constants.DEFAULT_LABEL_TELEPHONE);
        String telField = Constants.FIELD_TELEPHONE;
        if (isEmpty(request.getEmployeeTelephone())) {
            errors.add(new MessageResponse("ER001", Collections.singletonList(telLabel), telField));
        } else {
            if (!isValidMaxLength(request.getEmployeeTelephone(), 50)) {
                errors.add(new MessageResponse("ER006", Arrays.asList(telLabel, "50"), telField));
            }
            if (!isHalfsizeNumber(request.getEmployeeTelephone())) {
                errors.add(new MessageResponse("ER008", Collections.singletonList(telLabel), telField));
            }
        }
    }

    /**
     * Validate mật khẩu và xác nhận mật khẩu.
     * @param request Dữ liệu request.
     * @param errors Danh sách lỗi để add vào.
     */
    private void validatePassword(EmployeeRequest request, List<MessageResponse> errors) {
        String password = request.getEmployeeLoginPassword();
        String confirm = request.getEmployeeLoginPasswordConfirm();
        String label = getLabel(Constants.LABEL_PASSWORD, Constants.DEFAULT_LABEL_PASSWORD);
        String field = Constants.FIELD_PASSWORD;

        if (request.getEmployeeId() == null) { // Thêm mới
            if (isEmpty(password)) {
                errors.add(new MessageResponse("ER001", Collections.singletonList(label), field));
            } else {
                if (!isValidMinLength(password, 8) || !isValidMaxLength(password, 50)) {
                    errors.add(new MessageResponse("ER007", Arrays.asList(label, "8", "50"), field));
                }
            }
        } else if (!isEmpty(password)) { // Cập nhật (Nếu nhập pass mới)
            if (!isValidMinLength(password, 8) || !isValidMaxLength(password, 50)) {
                errors.add(new MessageResponse("ER007", Arrays.asList(label, "8", "50"), field));
            }
        }

        if (!isEmpty(password) && !password.equals(confirm)) {
            errors.add(new MessageResponse("ER017", null, Constants.FIELD_PASSWORD_CONFIRM));
        }
    }

    /**
     * Validate thông tin chứng chỉ đi kèm.
     * @param request Dữ liệu request.
     * @param errors Danh sách lỗi để add vào.
     */
    private void validateCertification(EmployeeRequest request, List<MessageResponse> errors) {
        if (request.getCertificationId() == null) return;

        if (!certificationService.checkExistsCertificationById(request.getCertificationId())) {
            errors.add(new MessageResponse("ER004", Collections.singletonList(getLabel(Constants.LABEL_CERT_NAME, Constants.DEFAULT_LABEL_CERT_NAME)), Constants.FIELD_CERT_ID));
        }

        // Ngày cấp
        String startLabel = getLabel(Constants.LABEL_CERT_START_DATE, Constants.DEFAULT_LABEL_CERT_START_DATE);
        String startField = Constants.FIELD_CERT_START_DATE;
        if (isEmpty(request.getCertificationStartDate())) {
            errors.add(new MessageResponse("ER001", Collections.singletonList(startLabel), startField));
        } else if (!isValidDateFormat(request.getCertificationStartDate())) {
            errors.add(new MessageResponse("ER011", Collections.singletonList(startLabel), startField));
        }

        // Ngày hết hạn
        String endLabel = getLabel(Constants.LABEL_CERT_END_DATE, Constants.DEFAULT_LABEL_CERT_END_DATE);
        String endField = Constants.FIELD_CERT_END_DATE;
        if (isEmpty(request.getCertificationEndDate())) {
            errors.add(new MessageResponse("ER001", Collections.singletonList(endLabel), endField));
        } else if (!isValidDateFormat(request.getCertificationEndDate())) {
            errors.add(new MessageResponse("ER011", Collections.singletonList(endLabel), endField));
        } else if (isValidDateFormat(request.getCertificationStartDate())) {
            // So sánh ngày
            try {
                LocalDate start = LocalDate.parse(request.getCertificationStartDate(), dateFormatter);
                LocalDate end = LocalDate.parse(request.getCertificationEndDate(), dateFormatter);
                if (!end.isAfter(start)) {
                    errors.add(new MessageResponse("ER012", null, endField));
                }
            } catch (Exception ignored) {}
        }

        // Điểm số
        String scoreLabel = getLabel(Constants.LABEL_CERT_SCORE, Constants.DEFAULT_LABEL_CERT_SCORE);
        String scoreField = Constants.FIELD_CERT_SCORE;
        if (isEmpty(request.getEmployeeCertificationScore())) {
            errors.add(new MessageResponse("ER001", Collections.singletonList(scoreLabel), scoreField));
        } else if (!isHalfsizeNumber(request.getEmployeeCertificationScore())) {
            errors.add(new MessageResponse("ER018", Collections.singletonList(scoreLabel), scoreField));
        }
    }

    /**
     * Lấy nội dung label từ message source.
     * @param key Key định danh trong file properties.
     * @param defaultLabel Giá trị mặc định nếu không tìm thấy key.
     * @return Nội dung label đã được quốc tế hóa.
     */
    private String getLabel(String key, String defaultLabel) {
        return messageSource.getMessage(key, null, defaultLabel, defaultLocale);
    }
}
