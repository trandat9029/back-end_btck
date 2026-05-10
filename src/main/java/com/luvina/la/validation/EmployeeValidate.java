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

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Lớp Validate nghiệp vụ cho màn hình ADM004/ADM005.
 * Áp dụng cơ chế Checklist (Fail-fast) để dừng ngay tại lỗi đầu tiên.
 *
 * @author tranledat
 */
@Component
@RequiredArgsConstructor
public class EmployeeValidate {

    /** Repository truy cập bảng employees */
    private final EmployeeRepository employeeRepository;

    /** Repository truy cập bảng departments */
    private final DepartmentRepository departmentRepository;

    /** Repository truy cập bảng certifications */
    private final CertificationRepository certificationRepository;

    /** Nguồn thông báo i18n để lấy nhãn hiển thị */
    private final MessageSource messageSource;

    // =========================================================================
    // UTILITY (Hàm tiện ích dùng chung)
    // =========================================================================

    /**
     * Kiểm tra định dạng ngày tháng có đúng cấu trúc quy định (yyyy/MM/dd) không.
     *
     * @param date Chuỗi ngày tháng cần kiểm tra
     * @return true nếu đúng định dạng, false nếu sai
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
     * Lấy nhãn hiển thị từ file i18n theo key.
     *
     * @param key Key trong file properties (ví dụ: label.employee_name)
     * @return Giá trị nhãn tương ứng với Locale hiện tại
     */
    private String getLabel(String key) {
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }

    /**
     * Đóng gói lỗi thành MessageResponse theo mã và danh sách tham số.
     *
     * @param code   Mã lỗi định nghĩa trong MessageCode
     * @param params Danh sách tham số đính kèm thông báo lỗi (varargs)
     * @return Đối tượng MessageResponse đã được thiết lập
     */
    private MessageResponse buildError(String code, String... params) {
        List<String> paramList = new ArrayList<>();
        Collections.addAll(paramList, params);
        return MessageResponse.builder().code(code).params(paramList).build();
    }

    // =========================================================================
    // PUBLIC API (Hàm validate công khai được gọi từ Controller)
    // =========================================================================

    /**
     * Kiểm tra tồn tại dữ liệu định danh trước khi chuyển sang màn hình xác nhận.
     * Điều phối sang validateExistenceEdit hoặc validateExistenceAdd tùy theo action.
     *
     * @param employeeRequest Dữ liệu nhân viên từ Client
     * @param action          Hành động ("add" hoặc "edit")
     * @return MessageResponse lỗi đầu tiên phát hiện được, hoặc null nếu hợp lệ
     */
    public MessageResponse validateExistence(EmployeeRequest employeeRequest, String action) {
        if (AppConstants.ACTION_EDIT.equalsIgnoreCase(action)) {
            return validateExistenceEdit(employeeRequest);
        }
        return validateExistenceAdd(employeeRequest);
    }

    /**
     * Validate tham số tìm kiếm và phân trang cho màn hình danh sách nhân viên (ADM002).
     *
     * @param employeeListRequest Đối tượng chứa tham số lọc và phân trang từ Client
     * @return MessageResponse nếu tham số không hợp lệ, null nếu hợp lệ
     */
    public MessageResponse validateEmployeeList(EmployeeListRequest employeeListRequest) {
        // Kiểm tra hướng sắp xếp: chỉ chấp nhận ASC, DESC hoặc rỗng
        if (!ValidatorUtils.isValidSortOrder(employeeListRequest.getEmployeeNameSort())) {
            return buildError(MessageCode.CODE_ER021);
        }
        if (!ValidatorUtils.isValidSortOrder(employeeListRequest.getCertificationNameSort())) {
            return buildError(MessageCode.CODE_ER021);
        }
        if (!ValidatorUtils.isValidSortOrder(employeeListRequest.getEndDateSort())) {
            return buildError(MessageCode.CODE_ER021);
        }

        // Kiểm tra giá trị phân trang: offset không được âm, limit phải dương
        if (employeeListRequest.getOffset() != null && employeeListRequest.getOffset() < 0) {
            return buildError(MessageCode.CODE_ER018);
        }
        if (employeeListRequest.getLimit() != null && employeeListRequest.getLimit() <= 0) {
            return buildError(MessageCode.CODE_ER018);
        }

        return null;
    }

    // =========================================================================
    // SUBMIT VALIDATION (Validate trước màn hình xác nhận)
    // =========================================================================

    /**
     * Validate định danh cho trường hợp Chỉnh sửa (Edit):
     *   1. Kiểm tra employee_id tồn tại trong bảng employees (ER013, "ＩＤ").
     *   2. Kiểm tra departmentId không được vắng mặt (ER002, "グループ").
     *   3. Kiểm tra certificationId tồn tại trong certifications (ER004, tên chứng chỉ).
     *
     * @param employeeRequest Dữ liệu nhân viên từ Client
     * @return MessageResponse lỗi đầu tiên, hoặc null nếu hợp lệ
     */
    private MessageResponse validateExistenceEdit(EmployeeRequest employeeRequest) {
        String labelId = getLabel(AppConstants.LABEL_ID);
        String labelDept = getLabel(AppConstants.LABEL_DEPARTMENT);
        MessageResponse error;

        if (employeeRequest.getEmployeeId() == null) {
            // Thiếu employee_id → ER001, "ＩＤ"
            error = buildError(MessageCode.CODE_ER001, labelId);
        } else if (!employeeRepository.existsById(employeeRequest.getEmployeeId())) {
            // Không tồn tại employee_id trong bảng employees → ER013, "ＩＤ"
            error = buildError(MessageCode.CODE_ER013, labelId);
        } else if (ValidatorUtils.isEmpty(employeeRequest.getDepartmentId())) {
            // Thiếu parameter departmentId → ER002, "グループ"
            error = buildError(MessageCode.CODE_ER002, labelDept);
        } else {
            // Kiểm tra certificationId tồn tại trong bảng certifications (nếu có)
            error = validateCertificationExists(employeeRequest.getCertificationRequest());
        }

        return error;
    }

    /**
     * Validate định danh cho trường hợp Thêm mới (Add):
     *   1. Kiểm tra employee_login_id chưa tồn tại trong employees (ER003, "アカウント名").
     *   2. Kiểm tra departmentId tồn tại trong bảng departments (ER004, "グループ").
     *   3. Kiểm tra certificationId tồn tại trong certifications (ER004, tên chứng chỉ).
     *
     * @param employeeRequest Dữ liệu nhân viên từ Client
     * @return MessageResponse lỗi đầu tiên, hoặc null nếu hợp lệ
     */
    private MessageResponse validateExistenceAdd(EmployeeRequest employeeRequest) {
        String labelDept = getLabel(AppConstants.LABEL_DEPARTMENT);
        MessageResponse error;

        // Kiểm tra employee_login_id chưa tồn tại → ER003, "アカウント名"
        error = validateLoginId(employeeRequest);

        if (error == null && !ValidatorUtils.isEmpty(employeeRequest.getDepartmentId())) {
            // Kiểm tra departmentId tồn tại trong bảng departments → ER004, "グループ"
            try {
                Long deptId = Long.parseLong(employeeRequest.getDepartmentId());
                if (!departmentRepository.existsById(deptId)) {
                    error = buildError(MessageCode.CODE_ER004, labelDept);
                }
            } catch (NumberFormatException e) {
                error = buildError(MessageCode.CODE_ER004, labelDept);
            }
        }

        if (error == null) {
            // Kiểm tra certificationId tồn tại trong bảng certifications (nếu có)
            error = validateCertificationExists(employeeRequest.getCertificationRequest());
        }

        return error;
    }

    // =========================================================================
    // CONFIRM VALIDATION (Validate toàn bộ trước khi lưu DB)
    // =========================================================================

    /**
     * Validate toàn bộ dữ liệu nhân viên theo thứ tự tuần tự (Fail-fast).
     * Dừng ngay và trả về lỗi đầu tiên phát hiện được.
     *
     * @param employeeRequest Dữ liệu nhân viên cần kiểm tra
     * @return MessageResponse chứa lỗi đầu tiên, hoặc null nếu tất cả hợp lệ
     */
    public MessageResponse validateEmployee(EmployeeRequest employeeRequest) {
        MessageResponse error = null;

        // 1. Kiểm tra employee_id tồn tại trong DB (Edit mode)
        if (employeeRequest.getEmployeeId() != null && !employeeRepository.existsById(employeeRequest.getEmployeeId())) {
            error = buildError(MessageCode.CODE_ER013, getLabel(AppConstants.LABEL_ID));
        }

        // 2. Kiểm tra Login ID
        if (error == null) error = validateLoginId(employeeRequest);

        // 3. Kiểm tra Phòng ban
        if (error == null) error = validateDepartment(employeeRequest.getDepartmentId());

        // 4. Kiểm tra Tên nhân viên
        if (error == null) error = validateEmployeeName(employeeRequest.getEmployeeName());

        // 5. Kiểm tra Tên Katakana
        if (error == null) error = validateEmployeeNameKana(employeeRequest.getEmployeeNameKana());

        // 6. Kiểm tra Ngày sinh
        if (error == null) error = validateBirthDate(employeeRequest.getEmployeeBirthDate());

        // 7. Kiểm tra Email
        if (error == null) error = validateEmail(employeeRequest.getEmployeeEmail());

        // 8. Kiểm tra Số điện thoại
        if (error == null) error = validateTelephone(employeeRequest.getEmployeeTelephone());

        // 9. Kiểm tra Mật khẩu: bắt buộc khi Add, chỉ validate khi Edit nếu có nhập mới
        if (error == null && (employeeRequest.getEmployeeId() == null || !ValidatorUtils.isEmpty(employeeRequest.getEmployeeLoginPassword()))) {
            error = validatePassword(employeeRequest.getEmployeeLoginPassword());
        }

        // 10. Kiểm tra Chứng chỉ (nếu có chọn)
        CertificationRequest cert = employeeRequest.getCertificationRequest();
        if (error == null && cert != null && !ValidatorUtils.isEmpty(cert.getCertificationId())) {
            error = validateCertification(cert);
        }

        return error;
    }

    // =========================================================================
    // FIELD VALIDATORS (Validate từng trường dữ liệu)
    // =========================================================================

    /**
     * Validate Login ID: bắt buộc, tối đa 50 ký tự, đúng định dạng, không trùng lặp.
     *
     * @param employeeRequest Chứa loginId và employeeId (dùng để loại trừ khi Edit)
     * @return MessageResponse nếu có lỗi, null nếu hợp lệ
     */
    private MessageResponse validateLoginId(EmployeeRequest employeeRequest) {
        String loginId = employeeRequest.getEmployeeLoginId();
        String label = getLabel(AppConstants.LABEL_LOGIN_ID);
        MessageResponse error;

        if (ValidatorUtils.isEmpty(loginId)) {
            // Không được để trống → ER001
            error = buildError(MessageCode.CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(loginId, AppConstants.MAX_LENGTH_50)) {
            // Vượt quá 50 ký tự → ER006
            error = buildError(MessageCode.CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_50));
        } else if (!ValidatorUtils.isValidLoginId(loginId)) {
            // Sai định dạng ký tự → ER019
            error = buildError(MessageCode.CODE_ER019, label);
        } else {
            // Kiểm tra trùng lặp: loại trừ chính nhân viên đang sửa (Edit mode)
            Optional<Employee> existing = employeeRepository.findByEmployeeLoginId(loginId);
            if (existing.isPresent() &&
                (employeeRequest.getEmployeeId() == null || !existing.get().getEmployeeId().equals(employeeRequest.getEmployeeId()))) {
                error = buildError(MessageCode.CODE_ER003, label);
            } else {
                error = null;
            }
        }

        return error;
    }

    /**
     * Validate Phòng ban: bắt buộc, phải tồn tại trong bảng departments.
     *
     * @param departmentId ID phòng ban dưới dạng String
     * @return MessageResponse nếu có lỗi, null nếu hợp lệ
     */
    private MessageResponse validateDepartment(String departmentId) {
        String label = getLabel(AppConstants.LABEL_DEPARTMENT);
        MessageResponse error;

        if (ValidatorUtils.isEmpty(departmentId)) {
            // Không được để trống → ER002
            error = buildError(MessageCode.CODE_ER002, label);
        } else {
            try {
                Long id = Long.parseLong(departmentId);
                // Không tồn tại trong bảng departments → ER004
                error = departmentRepository.existsById(id) ? null : buildError(MessageCode.CODE_ER004, label);
            } catch (NumberFormatException e) {
                error = buildError(MessageCode.CODE_ER004, label);
            }
        }

        return error;
    }

    /**
     * Validate Tên nhân viên: bắt buộc, tối đa 125 ký tự.
     *
     * @param name Tên nhân viên
     * @return MessageResponse nếu có lỗi, null nếu hợp lệ
     */
    private MessageResponse validateEmployeeName(String name) {
        String label = getLabel(AppConstants.LABEL_NAME);
        MessageResponse error;

        if (ValidatorUtils.isEmpty(name)) {
            error = buildError(MessageCode.CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(name, AppConstants.MAX_LENGTH_125)) {
            error = buildError(MessageCode.CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_125));
        } else {
            error = null;
        }

        return error;
    }

    /**
     * Validate Tên Katakana: bắt buộc, tối đa 125 ký tự, phải là Katakana half-width.
     *
     * @param nameKana Tên Katakana
     * @return MessageResponse nếu có lỗi, null nếu hợp lệ
     */
    private MessageResponse validateEmployeeNameKana(String nameKana) {
        String label = getLabel(AppConstants.LABEL_NAME_KANA);
        MessageResponse error;

        if (ValidatorUtils.isEmpty(nameKana)) {
            error = buildError(MessageCode.CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(nameKana, AppConstants.MAX_LENGTH_125)) {
            error = buildError(MessageCode.CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_125));
        } else if (!ValidatorUtils.isKatakana(nameKana)) {
            error = buildError(MessageCode.CODE_ER009, label);
        } else {
            error = null;
        }

        return error;
    }

    /**
     * Validate Ngày sinh: bắt buộc, đúng định dạng yyyy/MM/dd.
     *
     * @param birthDate Chuỗi ngày sinh
     * @return MessageResponse nếu có lỗi, null nếu hợp lệ
     */
    private MessageResponse validateBirthDate(String birthDate) {
        String label = getLabel(AppConstants.LABEL_BIRTH_DATE);
        MessageResponse error;

        if (ValidatorUtils.isEmpty(birthDate)) {
            error = buildError(MessageCode.CODE_ER001, label);
        } else if (!isValidDateFormat(birthDate)) {
            error = buildError(MessageCode.CODE_ER005, label, AppConstants.DATE_FORMAT);
        } else {
            error = null;
        }

        return error;
    }

    /**
     * Validate Email: bắt buộc, tối đa 125 ký tự, đúng cú pháp email.
     *
     * @param email Địa chỉ email
     * @return MessageResponse nếu có lỗi, null nếu hợp lệ
     */
    private MessageResponse validateEmail(String email) {
        String label = getLabel(AppConstants.LABEL_EMAIL);
        MessageResponse error;

        if (ValidatorUtils.isEmpty(email)) {
            error = buildError(MessageCode.CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(email, AppConstants.MAX_LENGTH_125)) {
            error = buildError(MessageCode.CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_125));
        } else if (!ValidatorUtils.isValidEmail(email)) {
            error = buildError(MessageCode.CODE_ER005, label, "email");
        } else {
            error = null;
        }

        return error;
    }

    /**
     * Validate Số điện thoại: bắt buộc, tối đa 50 ký tự, chỉ chứa ký tự half-width.
     *
     * @param tel Số điện thoại
     * @return MessageResponse nếu có lỗi, null nếu hợp lệ
     */
    private MessageResponse validateTelephone(String tel) {
        String label = getLabel(AppConstants.LABEL_TELEPHONE);
        MessageResponse error;

        if (ValidatorUtils.isEmpty(tel)) {
            error = buildError(MessageCode.CODE_ER001, label);
        } else if (ValidatorUtils.isMaxLength(tel, AppConstants.MAX_LENGTH_50)) {
            error = buildError(MessageCode.CODE_ER006, label, String.valueOf(AppConstants.MAX_LENGTH_50));
        } else if (!ValidatorUtils.isHalfSize(tel)) {
            error = buildError(MessageCode.CODE_ER008, label);
        } else {
            error = null;
        }

        return error;
    }

    /**
     * Validate Mật khẩu: bắt buộc, độ dài từ 8 đến 50 ký tự.
     * Chỉ gọi khi Add (bắt buộc) hoặc khi Edit có nhập mật khẩu mới.
     *
     * @param password Chuỗi mật khẩu
     * @return MessageResponse nếu có lỗi, null nếu hợp lệ
     */
    private MessageResponse validatePassword(String password) {
        String label = getLabel(AppConstants.LABEL_PASSWORD);
        MessageResponse error;

        if (ValidatorUtils.isEmpty(password)) {
            error = buildError(MessageCode.CODE_ER001, label);
        } else if (ValidatorUtils.isInvalidLengthRange(password, AppConstants.MIN_LENGTH_8, AppConstants.MAX_LENGTH_50)) {
            error = buildError(MessageCode.CODE_ER007, label, String.valueOf(AppConstants.MIN_LENGTH_8), String.valueOf(AppConstants.MAX_LENGTH_50));
        } else {
            error = null;
        }

        return error;
    }

    // =========================================================================
    // CERTIFICATION VALIDATORS (Validate chứng chỉ)
    // =========================================================================

    /**
     * Kiểm tra certificationId có tồn tại trong bảng certifications không.
     * Bỏ qua nếu certificationRequest là null hoặc certificationId rỗng.
     *
     * @param certificationRequest Thông tin chứng chỉ từ request
     * @return MessageResponse nếu không tồn tại trong DB, null nếu hợp lệ
     */
    private MessageResponse validateCertificationExists(CertificationRequest certificationRequest) {
        if (certificationRequest == null || ValidatorUtils.isEmpty(certificationRequest.getCertificationId())) {
            return null;
        }
        String certLabel = getLabel(AppConstants.LABEL_CERT_NAME);
        MessageResponse error;

        try {
            Long certId = Long.parseLong(certificationRequest.getCertificationId());
            error = certificationRepository.existsById(certId) ? null : buildError(MessageCode.CODE_ER004, certLabel);
        } catch (NumberFormatException e) {
            error = buildError(MessageCode.CODE_ER004, certLabel);
        }

        return error;
    }

    /**
     * Validate đầy đủ thông tin chứng chỉ: tồn tại trong DB, ngày tháng hợp lệ, điểm số hợp lệ.
     * Được gọi trong validateEmployee (Confirm mode) khi có chứng chỉ được chọn.
     *
     * @param certificationRequest Đối tượng chứa thông tin chứng chỉ
     * @return MessageResponse nếu có lỗi, null nếu hợp lệ
     */
    private MessageResponse validateCertification(CertificationRequest certificationRequest) {
        String startLabel = getLabel(AppConstants.LABEL_CERT_START_DATE);
        String endLabel = getLabel(AppConstants.LABEL_CERT_END_DATE);
        String scoreLabel = getLabel(AppConstants.LABEL_CERT_SCORE);
        MessageResponse error;

        // Kiểm tra certificationId tồn tại trong DB
        error = validateCertificationExists(certificationRequest);

        if (error == null) {
            if (ValidatorUtils.isEmpty(certificationRequest.getCertificationStartDate())) {
                // Ngày bắt đầu bắt buộc → ER001
                error = buildError(MessageCode.CODE_ER001, startLabel);
            } else if (!isValidDateFormat(certificationRequest.getCertificationStartDate())) {
                // Sai định dạng ngày bắt đầu → ER005
                error = buildError(MessageCode.CODE_ER005, startLabel, AppConstants.DATE_FORMAT);
            }
        }

        if (error == null) {
            if (ValidatorUtils.isEmpty(certificationRequest.getCertificationEndDate())) {
                // Ngày kết thúc bắt buộc → ER001
                error = buildError(MessageCode.CODE_ER001, endLabel);
            } else if (!isValidDateFormat(certificationRequest.getCertificationEndDate())) {
                // Sai định dạng ngày kết thúc → ER005
                error = buildError(MessageCode.CODE_ER005, endLabel, AppConstants.DATE_FORMAT);
            } else if (ValidatorUtils.isEndDateBeforeStartDate(
                    certificationRequest.getCertificationStartDate(),
                    certificationRequest.getCertificationEndDate())) {
                // Ngày kết thúc trước ngày bắt đầu → ER012
                error = buildError(MessageCode.CODE_ER012, getLabel(AppConstants.LABEL_CERT_START_DATE));
            }
        }

        if (error == null) {
            if (ValidatorUtils.isEmpty(certificationRequest.getEmployeeCertificationScore())) {
                // Điểm số bắt buộc → ER001
                error = buildError(MessageCode.CODE_ER001, scoreLabel);
            } else if (!ValidatorUtils.isPositiveNumber(certificationRequest.getEmployeeCertificationScore())) {
                // Điểm số phải là số dương → ER018
                error = buildError(MessageCode.CODE_ER018, scoreLabel);
            } else if (ValidatorUtils.isMaxLength(certificationRequest.getEmployeeCertificationScore(), AppConstants.MAX_LENGTH_3)) {
                // Điểm số vượt quá 3 chữ số → ER006
                error = buildError(MessageCode.CODE_ER006, scoreLabel, String.valueOf(AppConstants.MAX_LENGTH_3));
            }
        }

        return error;
    }
}
