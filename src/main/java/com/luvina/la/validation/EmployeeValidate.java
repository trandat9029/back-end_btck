/**
 * Copyright(C) 2026 Luvina
 * [EmployeeValidate.java], 23/04/2026 tranledat
 */
package com.luvina.la.validation;

import com.luvina.la.common.validate.ValidatorUtils;
import com.luvina.la.constants.AppConstants;
import com.luvina.la.constants.MessageCode;
import com.luvina.la.entity.Employee;
import com.luvina.la.payload.request.CertificationRequest;
import com.luvina.la.payload.request.EmployeeListRequest;
import com.luvina.la.payload.request.EmployeeRequest;
import com.luvina.la.payload.response.MessageResponse;
import com.luvina.la.repository.CertificationRepository;
import com.luvina.la.repository.DepartmentRepository;
import com.luvina.la.repository.EmployeeRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Lớp Validate nghiệp vụ cho màn hình ADM004/ADM005.
 * 
 * @author tranledat
 */
@Component
@RequiredArgsConstructor
public class EmployeeValidate {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CertificationRepository certificationRepository;
    private final MessageSource messageSource;

       /**
     * Kiểm tra định dạng ngày tháng có đúng với cấu trúc quy định (yyyy/MM/dd).
     * 
     * @param date Chuỗi ngày tháng cần kiểm tra
     * @return true nếu đúng định dạng, ngược lại false
     */
    private boolean isValidDateFormat(String date) {
        try {
            java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat(AppConstants.DATE_FORMAT);
            simpleDateFormat.setLenient(false);
            simpleDateFormat.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lấy nhãn (label) từ file i18n dựa trên key.
     * 
     * @param key Key trong file properties
     * @return Giá trị nhãn tương ứng
     */
    private String getLabel(String key) {
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }

    /**
     * Helper method để đóng gói MessageResponse lỗi một cách nhanh chóng.
     * 
     * @param code Mã lỗi
     * @param params Danh sách tham số truyền vào thông báo
     * @return Đối tượng MessageResponse đã được thiết lập dữ liệu
     */
    private MessageResponse buildError(String code, String... params) {
        List<String> paramList = new ArrayList<>();
        Collections.addAll(paramList, params);
        return MessageResponse.builder().code(code).params(paramList).build();
    }

    /**
     * Chặng 1: Validate để chuyển từ màn hình nhập liệu sang xác nhận (ADM004 -> ADM005).
     * 
     * @param request Dữ liệu nhân viên
     * @return MessageResponse lỗi đầu tiên, hoặc null nếu hợp lệ
     */
    public MessageResponse validateForSubmit(EmployeeRequest request) {
        if (request.getEmployeeId() == null) {
            // Trường hợp thêm mới (Add) - Theo Hình 1: Check Login ID
            return validateLoginId(request);
        } else {
            // Trường hợp chỉnh sửa (Edit) - Theo Hình 2: Check ID
            String labelId = getLabel(AppConstants.LABEL_ID);
            if (request.getEmployeeId() == null) { 
                return buildError(MessageCode.MSG_CODE_ER001, labelId);
            } else if (!employeeRepository.existsById(request.getEmployeeId())) {
                return buildError(MessageCode.MSG_CODE_ER013, labelId);
            }
        }
        return null;
    }

    /**
     * Chặng 2: Validate toàn bộ dữ liệu trước khi lưu vào Database (ADM005 OK).
     * 
     * @param request Dữ liệu nhân viên
     * @return MessageResponse lỗi đầu tiên, hoặc null nếu hợp lệ
     */
    public MessageResponse validateForConfirm(EmployeeRequest request) {
        return validateEmployee(request);
    }

    /**
     * Thực hiện validate dữ liệu đầu vào toàn phần. Cơ chế Fail-fast.
     * 
     * @param request Dữ liệu từ Client gửi lên.
     * @return MessageResponse chứa lỗi đầu tiên, hoặc null nếu hợp lệ.
     */
    public MessageResponse validateEmployee(EmployeeRequest request) {
        MessageResponse error;

        // 1. Validate ID nhân viên (nếu là mode Edit)
        if (request.getEmployeeId() != null) {
            if (!employeeRepository.existsById(request.getEmployeeId())) {
                return buildError(MessageCode.MSG_CODE_ER013, getLabel(AppConstants.LABEL_ID));
            }
        }

        // 2. Validate Login ID
        error = validateLoginId(request);
        if (error != null) return error;

        // 3. Validate Department
        error = validateDepartment(request.getDepartmentId());
        if (error != null) return error;

        // 4. Validate Employee Name
        error = validateEmployeeName(request.getEmployeeName());
        if (error != null) return error;

        // 5. Validate Employee Name Kana
        error = validateEmployeeNameKana(request.getEmployeeNameKana());
        if (error != null) return error;

        // 6. Validate Birth Date
        error = validateBirthDate(request.getEmployeeBirthDate());
        if (error != null) return error;

        // 7. Validate Email
        error = validateEmail(request.getEmployeeEmail());
        if (error != null) return error;

        // 8. Validate Telephone
        error = validateTelephone(request.getEmployeeTelephone());
        if (error != null) return error;

        // 9. Validate Password (chỉ khi thêm mới hoặc có nhập password)
        if (request.getEmployeeId() == null || !ValidatorUtils.isEmpty(request.getEmployeeLoginPassword())) {
            error = validatePassword(request.getEmployeeLoginPassword());
            if (error != null) return error;
        }

        // 10. Validate Certification (nếu có chọn)
        if (request.getCertificationRequest() != null && !ValidatorUtils.isEmpty(request.getCertificationRequest().getCertificationId())) {
            error = validateCertification(request.getCertificationRequest());
            if (error != null) return error;
        }

        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của Login ID.
     * 
     * @param request Chứa Login ID và ID nhân viên
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validateLoginId(EmployeeRequest request) {
        String loginId = request.getEmployeeLoginId();
        String label = getLabel(AppConstants.LABEL_LOGIN_ID);
        if (ValidatorUtils.isEmpty(loginId)) {
            return buildError(MessageCode.MSG_CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(loginId, AppConstants.MAX_LENGTH_50)) {
            return buildError(MessageCode.MSG_CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_50));
        } else if (!ValidatorUtils.isValidLoginId(loginId)) {
            return buildError(MessageCode.MSG_CODE_ER019, label);
        }
        
        // Check uniqueness
        Optional<Employee> existingEmployee = employeeRepository.findByEmployeeLoginId(loginId);
        if (existingEmployee.isPresent()) {
            if (request.getEmployeeId() == null || !existingEmployee.get().getEmployeeId().equals(request.getEmployeeId())) {
                return buildError(MessageCode.MSG_CODE_ER003, label);
            }
        }
        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của phòng ban.
     * 
     * @param departmentId ID phòng ban
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validateDepartment(String departmentId) {
        String label = getLabel(AppConstants.LABEL_DEPARTMENT);
        if (ValidatorUtils.isEmpty(departmentId)) {
            return buildError(MessageCode.MSG_CODE_ER002, label);
        }
        try {
            Long id = Long.parseLong(departmentId);
            if (!departmentRepository.existsById(id)) {
                return buildError(MessageCode.MSG_CODE_ER004, label);
            }
        } catch (NumberFormatException e) {
            return buildError(MessageCode.MSG_CODE_ER004, label);
        }
        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của tên nhân viên.
     * 
     * @param name Tên nhân viên
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validateEmployeeName(String name) {
        String label = getLabel(AppConstants.LABEL_NAME);
        if (ValidatorUtils.isEmpty(name)) {
            return buildError(MessageCode.MSG_CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(name, AppConstants.MAX_LENGTH_125)) {
            return buildError(MessageCode.MSG_CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_125));
        }
        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của tên nhân viên (Katakana).
     * 
     * @param nameKana Tên Katakana
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validateEmployeeNameKana(String nameKana) {
        String label = getLabel(AppConstants.LABEL_NAME_KANA);
        if (ValidatorUtils.isEmpty(nameKana)) {
            return buildError(MessageCode.MSG_CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(nameKana, AppConstants.MAX_LENGTH_125)) {
            return buildError(MessageCode.MSG_CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_125));
        } else if (!ValidatorUtils.isKatakana(nameKana)) {
            return buildError(MessageCode.MSG_CODE_ER009, label);
        }
        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của ngày sinh.
     * 
     * @param birthDate Ngày sinh (yyyy/MM/dd)
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validateBirthDate(String birthDate) {
        String label = getLabel(AppConstants.LABEL_BIRTH_DATE);
        if (ValidatorUtils.isEmpty(birthDate)) {
            return buildError(MessageCode.MSG_CODE_ER001, label);
        } else if (!isValidDateFormat(birthDate)) {
            return buildError(MessageCode.MSG_CODE_ER005, label, AppConstants.DATE_FORMAT);
        }
        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của Email.
     * 
     * @param email Email
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validateEmail(String email) {
        String label = getLabel(AppConstants.LABEL_EMAIL);
        if (ValidatorUtils.isEmpty(email)) {
            return buildError(MessageCode.MSG_CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(email, AppConstants.MAX_LENGTH_125)) {
            return buildError(MessageCode.MSG_CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_125));
        } else if (!ValidatorUtils.isValidEmail(email)) {
            return buildError(MessageCode.MSG_CODE_ER005, label, "email");
        }
        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của số điện thoại.
     * 
     * @param tel Số điện thoại
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validateTelephone(String tel) {
        String label = getLabel(AppConstants.LABEL_TELEPHONE);
        if (ValidatorUtils.isEmpty(tel)) {
            return buildError(MessageCode.MSG_CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(tel, AppConstants.MAX_LENGTH_50)) {
            return buildError(MessageCode.MSG_CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_50));
        } else if (!ValidatorUtils.isHalfSize(tel)) {
            return buildError(MessageCode.MSG_CODE_ER008, label);
        }
        return null;
    }

    /**
     * Kiểm tra logic nghiệp vụ cho mật khẩu.
     * 
     * @param password Mật khẩu cần kiểm tra
     * @return MessageResponse chứa lỗi, hoặc null nếu hợp lệ
     */
    private MessageResponse validatePassword(String password) {
        String label = getLabel(AppConstants.LABEL_PASSWORD);
        if (ValidatorUtils.isEmpty(password)) {
            return buildError(MessageCode.MSG_CODE_ER001, label);
        } else if (ValidatorUtils.isInvalidLengthRange(password, AppConstants.MIN_LENGTH_8, AppConstants.MAX_LENGTH_50)) {
            return buildError(MessageCode.MSG_CODE_ER007, label, String.valueOf(AppConstants.MIN_LENGTH_8), String.valueOf(AppConstants.MAX_LENGTH_50));
        }
        return null;
    }

    /**
     * Kiểm tra logic nghiệp vụ cho thông tin chứng chỉ.
     * Bao gồm: Sự tồn tại của chứng chỉ, định dạng ngày tháng, logic ngày bắt đầu/kết thúc và điểm số.
     * 
     * @param certificationRequest Đối tượng chứa thông tin chứng chỉ
     * @return MessageResponse chứa lỗi đầu tiên phát hiện được, hoặc null nếu hợp lệ
     */
    private MessageResponse validateCertification(CertificationRequest certificationRequest) {
        // Certification ID
        String certLabel = getLabel(AppConstants.LABEL_CERT_NAME);
        if (!ValidatorUtils.isEmpty(certificationRequest.getCertificationId())) {
            try {
                Long id = Long.parseLong(certificationRequest.getCertificationId());
                if (!certificationRepository.existsById(id)) {
                    return buildError(MessageCode.MSG_CODE_ER004, certLabel);
                }
            } catch (NumberFormatException e) {
                return buildError(MessageCode.MSG_CODE_ER004, certLabel);
            }
        }

        // Start Date
        String startLabel = getLabel(AppConstants.LABEL_CERT_START_DATE);
        if (ValidatorUtils.isEmpty(certificationRequest.getCertificationStartDate())) {
            return buildError(MessageCode.MSG_CODE_ER001, startLabel);
        } else if (!isValidDateFormat(certificationRequest.getCertificationStartDate())) {
            return buildError(MessageCode.MSG_CODE_ER005, startLabel, AppConstants.DATE_FORMAT);
        }

        // End Date
        String endLabel = getLabel(AppConstants.LABEL_CERT_END_DATE);
        if (ValidatorUtils.isEmpty(certificationRequest.getCertificationEndDate())) {
            return buildError(MessageCode.MSG_CODE_ER001, endLabel);
        } else if (!isValidDateFormat(certificationRequest.getCertificationEndDate())) {
            return buildError(MessageCode.MSG_CODE_ER005, endLabel, AppConstants.DATE_FORMAT);
        }

        // Compare dates
        if (ValidatorUtils.isEndDateBeforeStartDate(certificationRequest.getCertificationStartDate(), certificationRequest.getCertificationEndDate())) {
            return buildError(MessageCode.MSG_CODE_ER012, getLabel(AppConstants.LABEL_CERT_START_DATE));
        }

        // Score
        String scoreLabel = getLabel(AppConstants.LABEL_CERT_SCORE);
        if (ValidatorUtils.isEmpty(certificationRequest.getEmployeeCertificationScore())) {
            return buildError(MessageCode.MSG_CODE_ER001, scoreLabel);
        } else if (!ValidatorUtils.isPositiveNumber(certificationRequest.getEmployeeCertificationScore())) {
            return buildError(MessageCode.MSG_CODE_ER018, scoreLabel);
        }

        return null;
    }

    /**
     * Validate tham số tìm kiếm và phân trang cho danh sách nhân viên.
     * 
     * @param request Tham số từ Client
     * @return MessageResponse lỗi đầu tiên hoặc null nếu hợp lệ
     */
    public MessageResponse validateEmployeeList(EmployeeListRequest request) {
        // Validate Sort Order (ASC/DESC)
        if (!ValidatorUtils.isValidSortOrder(request.getOrdEmployeeName())) {
            return buildError(MessageCode.MSG_CODE_ER021);
        }
        if (!ValidatorUtils.isValidSortOrder(request.getOrdCertificationName())) {
            return buildError(MessageCode.MSG_CODE_ER021);
        }
        if (!ValidatorUtils.isValidSortOrder(request.getOrdEndDate())) {
            return buildError(MessageCode.MSG_CODE_ER021);
        }

        // Validate Offset/Limit (Positive numbers)
        if (request.getOffset() != null && request.getOffset() < 0) {
            return buildError(MessageCode.MSG_CODE_ER018);
        }
        if (request.getLimit() != null && request.getLimit() <= 0) {
            return buildError(MessageCode.MSG_CODE_ER018);
        }

        return null;
    }
}
    
