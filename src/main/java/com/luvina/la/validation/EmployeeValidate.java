/**
 * Copyright(C) 2026 Luvina Software
 * EmployeeValidate.java, 09/04/2026 tranledat
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
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Lớp Validate nghiệp vụ cho màn hình ADM004/ADM005.
 * Áp dụng cơ chế Checklist với vòng lặp Suppliers để tối ưu hóa code và dễ bảo trì.
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
     * Lấy nhãn (label) từ file i18n dựa trên key truyền vào.
     * 
     * @param key Key trong file properties (ví dụ: label.employee_name)
     * @return Giá trị nhãn tương ứng với Locale hiện tại
     */
    private String getLabel(String key) {
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }

    /**
     * Helper để xác định xem có phải đang trong ngữ cảnh Edit hay không.
     * Có thể dựa vào một flag hoặc logic nghiệp vụ cụ thể.
     */
    private boolean isEditContext(EmployeeRequest request) {
        // Trong dự án này, nếu có employeeId thì coi như là Edit
        return request.getEmployeeId() != null;
    }

    /**
     * Helper method để đóng gói MessageResponse lỗi một cách nhanh chóng.
     * 
     * @param code Mã lỗi định nghĩa trong MessageCode
     * @param params Danh sách các tham số truyền vào thông báo lỗi (varargs)
     * @return Đối tượng MessageResponse đã được thiết lập dữ liệu
     */
    private MessageResponse buildError(String code, String... params) {
        List<String> paramList = new ArrayList<>();
        Collections.addAll(paramList, params);
        return MessageResponse.builder().code(code).params(paramList).build();
    }

    /**
     * Chặng 1: Validate để chuyển từ màn hình nhập liệu sang xác nhận (ADM004 -> ADM005).
     * Phân biệt giữa Thêm mới (add) và Chỉnh sửa (edit) để kiểm tra các trường định danh tương ứng.
     * 
     * @param employeeRequest Dữ liệu nhân viên từ Client
     * @param action Hành động ("add" hoặc "edit")
     * @return MessageResponse lỗi đầu tiên, hoặc null nếu hợp lệ
     */
    public MessageResponse validateForSubmit(EmployeeRequest employeeRequest, String action) {
        // Trường hợp Chỉnh sửa (Edit): Kiểm tra employeeId
        if (AppConstants.ACTION_EDIT.equalsIgnoreCase(action)) {
            String labelId = getLabel(AppConstants.LABEL_ID);
            if (employeeRequest.getEmployeeId() == null) {
                return buildError(MessageCode.CODE_ER001, labelId);
            } else if (!employeeRepository.existsById(employeeRequest.getEmployeeId())) {
                return buildError(MessageCode.CODE_ER013, labelId);
            }
            return null;
        } 
        
        // Trường hợp Thêm mới (Add) hoặc mặc định: Kiểm tra employeeLoginId
        return validateLoginId(employeeRequest);
    }

    

    /**
     * Chặng 2: Validate toàn bộ dữ liệu trước khi lưu vào Database (Sau khi ADM005 xác nhận).
     * 
     * @param employeeRequest Dữ liệu nhân viên từ Client
     * @return MessageResponse lỗi đầu tiên, hoặc null nếu hợp lệ
     */
    public MessageResponse validateForConfirm(EmployeeRequest employeeRequest) {
        return validateEmployee(employeeRequest);
    }

    /**
     * Thực hiện validate dữ liệu đầu vào toàn phần bằng cơ chế Checklist (Fail-fast).
     * 
     * @param employeeRequest Dữ liệu nhân viên cần kiểm tra
     * @return MessageResponse chứa lỗi đầu tiên phát hiện được, hoặc null nếu tất cả hợp lệ
     */
    public MessageResponse validateEmployee(EmployeeRequest employeeRequest) {
        List<Supplier<MessageResponse>> checklist = List.of(
            // 1. Validate ID nhân viên (Edit mode)
            () -> (employeeRequest.getEmployeeId() != null && !employeeRepository.existsById(employeeRequest.getEmployeeId())) 
                  ? buildError(MessageCode.CODE_ER013, getLabel(AppConstants.LABEL_ID)) : null,
            // 2. Validate Login ID
            () -> validateLoginId(employeeRequest),
            // 3. Validate Department
            () -> validateDepartment(employeeRequest.getDepartmentId()),
            // 4. Validate Employee Name
            () -> validateEmployeeName(employeeRequest.getEmployeeName()),
            // 5. Validate Employee Name Kana
            () -> validateEmployeeNameKana(employeeRequest.getEmployeeNameKana()),
            // 6. Validate Birth Date
            () -> validateBirthDate(employeeRequest.getEmployeeBirthDate()),
            // 7. Validate Email
            () -> validateEmail(employeeRequest.getEmployeeEmail()),
            // 8. Validate Telephone
            () -> validateTelephone(employeeRequest.getEmployeeTelephone()),
            // 9. Validate Password
            () -> (employeeRequest.getEmployeeId() == null || !ValidatorUtils.isEmpty(employeeRequest.getEmployeeLoginPassword()))
                  ? validatePassword(employeeRequest.getEmployeeLoginPassword()) : null,
            // 10. Validate Certification
            () -> (employeeRequest.getCertificationRequest() != null && !ValidatorUtils.isEmpty(employeeRequest.getCertificationRequest().getCertificationId()))
                  ? validateCertification(employeeRequest.getCertificationRequest()) : null
        );

        for (Supplier<MessageResponse> step : checklist) {
            MessageResponse error = step.get();
            if (error != null) return error;
        }

        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của Login ID (Bắt buộc, Độ dài, Định dạng, Trùng lặp).
     * 
     * @param employeeRequest Chứa thông tin Login ID và Employee ID
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validateLoginId(EmployeeRequest employeeRequest) {
        String loginId = employeeRequest.getEmployeeLoginId();
        String label = getLabel(AppConstants.LABEL_LOGIN_ID);
        if (ValidatorUtils.isEmpty(loginId)) {
            return buildError(MessageCode.CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(loginId, AppConstants.MAX_LENGTH_50)) {
            return buildError(MessageCode.CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_50));
        } else if (!ValidatorUtils.isValidLoginId(loginId)) {
            return buildError(MessageCode.CODE_ER019, label);
        }
        
        Optional<Employee> existingEmployee = employeeRepository.findByEmployeeLoginId(loginId);
        if (existingEmployee.isPresent()) {
            if (employeeRequest.getEmployeeId() == null || !existingEmployee.get().getEmployeeId().equals(employeeRequest.getEmployeeId())) {
                return buildError(MessageCode.CODE_ER003, label);
            }
        }
        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của phòng ban (Bắt buộc, Sự tồn tại trong DB).
     * 
     * @param departmentId ID phòng ban dưới dạng String
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validateDepartment(String departmentId) {
        String label = getLabel(AppConstants.LABEL_DEPARTMENT);
        if (ValidatorUtils.isEmpty(departmentId)) {
            return buildError(MessageCode.CODE_ER002, label);
        }
        try {
            Long id = Long.parseLong(departmentId);
            if (!departmentRepository.existsById(id)) {
                return buildError(MessageCode.CODE_ER004, label);
            }
        } catch (NumberFormatException e) {
            return buildError(MessageCode.CODE_ER004, label);
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
            return buildError(MessageCode.CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(name, AppConstants.MAX_LENGTH_125)) {
            return buildError(MessageCode.CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_125));
        }
        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của tên nhân viên dạng Katakana.
     * 
     * @param nameKana Tên Katakana
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validateEmployeeNameKana(String nameKana) {
        String label = getLabel(AppConstants.LABEL_NAME_KANA);
        if (ValidatorUtils.isEmpty(nameKana)) {
            return buildError(MessageCode.CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(nameKana, AppConstants.MAX_LENGTH_125)) {
            return buildError(MessageCode.CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_125));
        } else if (!ValidatorUtils.isKatakana(nameKana)) {
            return buildError(MessageCode.CODE_ER009, label);
        }
        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của ngày sinh (Bắt buộc, Đúng định dạng).
     * 
     * @param birthDate Chuỗi ngày sinh
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validateBirthDate(String birthDate) {
        String label = getLabel(AppConstants.LABEL_BIRTH_DATE);
        if (ValidatorUtils.isEmpty(birthDate)) {
            return buildError(MessageCode.CODE_ER001, label);
        } else if (!isValidDateFormat(birthDate)) {
            return buildError(MessageCode.CODE_ER005, label, AppConstants.DATE_FORMAT);
        }
        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của Email.
     * 
     * @param email Địa chỉ email
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validateEmail(String email) {
        String label = getLabel(AppConstants.LABEL_EMAIL);
        if (ValidatorUtils.isEmpty(email)) {
            return buildError(MessageCode.CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(email, AppConstants.MAX_LENGTH_125)) {
            return buildError(MessageCode.CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_125));
        } else if (!ValidatorUtils.isValidEmail(email)) {
            return buildError(MessageCode.CODE_ER005, label, "email");
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
            return buildError(MessageCode.CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(tel, AppConstants.MAX_LENGTH_50)) {
            return buildError(MessageCode.CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_50));
        } else if (!ValidatorUtils.isHalfSize(tel)) {
            return buildError(MessageCode.CODE_ER008, label);
        }
        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của mật khẩu (Độ dài từ 8-50).
     * 
     * @param password Chuỗi mật khẩu
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validatePassword(String password) {
        String label = getLabel(AppConstants.LABEL_PASSWORD);
        if (ValidatorUtils.isEmpty(password)) {
            return buildError(MessageCode.CODE_ER001, label);
        } else if (ValidatorUtils.isInvalidLengthRange(password, AppConstants.MIN_LENGTH_8, AppConstants.MAX_LENGTH_50)) {
            return buildError(MessageCode.CODE_ER007, label, String.valueOf(AppConstants.MIN_LENGTH_8), String.valueOf(AppConstants.MAX_LENGTH_50));
        }
        return null;
    }

    /**
     * Kiểm tra tính hợp lệ của thông tin chứng chỉ (Sự tồn tại, Ngày tháng, Điểm số).
     * 
     * @param certificationRequest Đối tượng chứa thông tin chứng chỉ
     * @return MessageResponse nếu có lỗi, ngược lại null
     */
    private MessageResponse validateCertification(CertificationRequest certificationRequest) {
        String certLabel = getLabel(AppConstants.LABEL_CERT_NAME);
        if (!ValidatorUtils.isEmpty(certificationRequest.getCertificationId())) {
            try {
                Long id = Long.parseLong(certificationRequest.getCertificationId());
                if (!certificationRepository.existsById(id)) {
                    return buildError(MessageCode.CODE_ER004, certLabel);
                }
            } catch (NumberFormatException e) {
                return buildError(MessageCode.CODE_ER004, certLabel);
            }
        }

        String startLabel = getLabel(AppConstants.LABEL_CERT_START_DATE);
        if (ValidatorUtils.isEmpty(certificationRequest.getCertificationStartDate())) {
            return buildError(MessageCode.CODE_ER001, startLabel);
        } else if (!isValidDateFormat(certificationRequest.getCertificationStartDate())) {
            return buildError(MessageCode.CODE_ER005, startLabel, AppConstants.DATE_FORMAT);
        }

        String endLabel = getLabel(AppConstants.LABEL_CERT_END_DATE);
        if (ValidatorUtils.isEmpty(certificationRequest.getCertificationEndDate())) {
            return buildError(MessageCode.CODE_ER001, endLabel);
        } else if (!isValidDateFormat(certificationRequest.getCertificationEndDate())) {
            return buildError(MessageCode.CODE_ER005, endLabel, AppConstants.DATE_FORMAT);
        }

        if (ValidatorUtils.isEndDateBeforeStartDate(certificationRequest.getCertificationStartDate(), certificationRequest.getCertificationEndDate())) {
            return buildError(MessageCode.CODE_ER012, getLabel(AppConstants.LABEL_CERT_START_DATE));
        }

        String scoreLabel = getLabel(AppConstants.LABEL_CERT_SCORE);
        if (ValidatorUtils.isEmpty(certificationRequest.getEmployeeCertificationScore())) {
            return buildError(MessageCode.CODE_ER001, scoreLabel);
        } else if (!ValidatorUtils.isPositiveNumber(certificationRequest.getEmployeeCertificationScore())) {
            return buildError(MessageCode.CODE_ER018, scoreLabel);
        } else if (ValidatorUtils.isMaxLength(certificationRequest.getEmployeeCertificationScore(), AppConstants.MAX_LENGTH_3)) {
            return buildError(MessageCode.CODE_ER006, scoreLabel, String.valueOf(AppConstants.MAX_LENGTH_3));
        }

        return null;
    }

    /**
     * Kiểm tra tham số tìm kiếm và phân trang cho danh sách nhân viên.
     * 
     * @param employeeListRequest Đối tượng chứa tham số từ Client
     * @return MessageResponse nếu tham số không hợp lệ, ngược lại null
     */
    public MessageResponse validateEmployeeList(EmployeeListRequest employeeListRequest) {
        if (!ValidatorUtils.isValidSortOrder(employeeListRequest.getEmployeeNameSort())) {
            return buildError(MessageCode.CODE_ER021);
        }
        if (!ValidatorUtils.isValidSortOrder(employeeListRequest.getCertificationNameSort())) {
            return buildError(MessageCode.CODE_ER021);
        }
        if (!ValidatorUtils.isValidSortOrder(employeeListRequest.getEndDateSort())) {
            return buildError(MessageCode.CODE_ER021);
        }

        if (employeeListRequest.getOffset() != null && employeeListRequest.getOffset() < 0) {
            return buildError(MessageCode.CODE_ER018);
        }
        if (employeeListRequest.getLimit() != null && employeeListRequest.getLimit() <= 0) {
            return buildError(MessageCode.CODE_ER018);
        }

        return null;
    }
}
