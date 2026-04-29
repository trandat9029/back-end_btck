/**
 * Copyright(C) 2026 - Luvina
 * [Constants.java], 29/04/2026 tranledat
 */
package com.luvina.la.config;

/**
 * Lớp định nghĩa các hằng số dùng chung cho toàn bộ hệ thống.
 * @author tranledat
 */
public class Constants {

    private Constants() {
    }

    // Các cấu hình về môi trường hệ thống
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev"; // Chế độ phát triển
    public static final String SPRING_PROFILE_PRODUCTION = "prod"; // Chế độ thực tế (Production)
    public static final boolean IS_CROSS_ALLOW = true; // Cho phép truy cập Cross-origin

    // Cấu hình bảo mật JWT
    public static final String JWT_SECRET = "Luvina-Academe"; // Khóa bí mật ký Token
    public static final long JWT_EXPIRATION = 160 * 60 * 60; // Thời gian hết hạn của Token (7 ngày)

    // Danh sách các API không yêu cầu xác thực
    public static final String[] ENDPOINTS_PUBLIC = new String[] {
            "/",
            "/login/**",
            "/error/**"
    };

    // Danh sách các API yêu cầu quyền USER
    public static final String[] ENDPOINTS_WITH_ROLE = new String[] {
            "/user/**"
    };

    // Các thuộc tính của người dùng được lưu vào JWT Token
    public static final String[] ATTRIBUTIES_TO_TOKEN = new String[] {
            "employeeId", // ID nhân viên
            "departmentId", // ID phòng ban
            "employeeName", // Tên nhân viên
            "employeeNameKana", // Tên Katakana
            "employeeBirthDate", // Ngày sinh
            "employeeEmail", // Email
            "employeeTelephone", // Số điện thoại
            "employeeLoginId", // Tên đăng nhập
            "employeeEmail" // Email (lặp lại)
    };

    // Các biểu thức chính quy (Regex) dùng để kiểm tra định dạng dữ liệu
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$"; // Định dạng Email
    public static final String LOGIN_ID_PATTERN = "^[a-zA-Z_][a-zA-Z0-9_]*$"; // Định dạng Login ID (chữ, số, _)
    public static final String HALFSIDE_KATAKANA_PATTERN = "^[\\uFF61-\\uFF9F]+$"; // Định dạng Katakana nửa chiều
    public static final String HALFSIDE_NUMBER_PATTERN = "^[0-9]*$"; // Định dạng số nửa chiều

    // Các khóa (Keys) để lấy nhãn hiển thị từ file messages.properties
    public static final String LABEL_LOGIN_ID = "label.employeeLoginId"; // Nhãn Tên đăng nhập
    public static final String LABEL_DEPARTMENT_NAME = "label.departmentName"; // Nhãn Tên phòng ban
    public static final String LABEL_EMPLOYEE_NAME = "label.employeeName"; // Nhãn Tên nhân viên
    public static final String LABEL_EMPLOYEE_NAME_KANA = "label.employeeNameKana"; // Nhãn Tên Katakana
    public static final String LABEL_BIRTH_DATE = "label.employeeBirthDate"; // Nhãn Ngày sinh
    public static final String LABEL_EMAIL = "label.employeeEmail"; // Nhãn Email
    public static final String LABEL_TELEPHONE = "label.employeeTelephone"; // Nhãn Số điện thoại
    public static final String LABEL_PASSWORD = "label.employeeLoginPassword"; // Nhãn Mật khẩu
    public static final String LABEL_CERT_NAME = "label.certificationName"; // Nhãn Tên chứng chỉ
    public static final String LABEL_CERT_START_DATE = "label.certificationStartDate"; // Nhãn Ngày cấp chứng chỉ
    public static final String LABEL_CERT_END_DATE = "label.certificationEndDate"; // Nhãn Ngày hết hạn chứng chỉ
    public static final String LABEL_CERT_SCORE = "label.employeeCertificationScore"; // Nhãn Điểm số chứng chỉ

    // Các nhãn mặc định bằng tiếng Nhật (sử dụng khi không tìm thấy trong file properties)
    public static final String DEFAULT_LABEL_LOGIN_ID = "アカウント名"; // Tên đăng nhập mặc định
    public static final String DEFAULT_LABEL_DEPARTMENT = "グループ"; // Nhóm mặc định
    public static final String DEFAULT_LABEL_NAME = "氏名"; // Tên mặc định
    public static final String DEFAULT_LABEL_NAME_KANA = "カタカナ氏名"; // Tên Katakana mặc định
    public static final String DEFAULT_LABEL_BIRTH_DATE = "生年月日"; // Ngày sinh mặc định
    public static final String DEFAULT_LABEL_EMAIL = "メールアドレス"; // Email mặc định
    public static final String DEFAULT_LABEL_TELEPHONE = "電話番号"; // Số điện thoại mặc định
    public static final String DEFAULT_LABEL_PASSWORD = "パスワード"; // Mật khẩu mặc định
    public static final String DEFAULT_LABEL_CERT_NAME = "資格"; // Chứng chỉ mặc định
    public static final String DEFAULT_LABEL_CERT_START_DATE = "資格交付日"; // Ngày cấp mặc định
    public static final String DEFAULT_LABEL_CERT_END_DATE = "失効日"; // Ngày hết hạn mặc định
    public static final String DEFAULT_LABEL_CERT_SCORE = "点数"; // Điểm số mặc định

    // Tên các trường dữ liệu (Field names) dùng để báo lỗi về Client
    public static final String FIELD_LOGIN_ID = "employeeLoginId"; // Trường Tên đăng nhập
    public static final String FIELD_DEPARTMENT_ID = "departmentId"; // Trường ID phòng ban
    public static final String FIELD_NAME = "employeeName"; // Trường Tên nhân viên
    public static final String FIELD_NAME_KANA = "employeeNameKana"; // Trường Tên Katakana
    public static final String FIELD_BIRTH_DATE = "employeeBirthDate"; // Trường Ngày sinh
    public static final String FIELD_EMAIL = "employeeEmail"; // Trường Email
    public static final String FIELD_TELEPHONE = "employeeTelephone"; // Trường Số điện thoại
    public static final String FIELD_PASSWORD = "employeeLoginPassword"; // Trường Mật khẩu
    public static final String FIELD_PASSWORD_CONFIRM = "employeeLoginPasswordConfirm"; // Trường Xác nhận mật khẩu
    public static final String FIELD_CERT_ID = "certificationId"; // Trường ID chứng chỉ
    public static final String FIELD_CERT_START_DATE = "certificationStartDate"; // Trường Ngày cấp
    public static final String FIELD_CERT_END_DATE = "certificationEndDate"; // Trường Ngày hết hạn
    public static final String FIELD_CERT_SCORE = "employeeCertificationScore"; // Trường Điểm số

    // Danh sách mã lỗi (Error Codes) trả về cho phía Frontend
    public static final String CODE_SUCCESS = "200"; // Mã thành công
    public static final String CODE_ER001 = "ER001"; // Lỗi: Trường bắt buộc nhập
    public static final String CODE_ER002 = "ER002"; // Lỗi: Trường bắt buộc chọn
    public static final String CODE_ER003 = "ER003"; // Lỗi: Dữ liệu đã tồn tại trong hệ thống
    public static final String CODE_ER004 = "ER004"; // Lỗi: Dữ liệu không tồn tại trong hệ thống
    public static final String CODE_ER005 = "ER005"; // Lỗi: Sai định dạng yêu cầu
    public static final String CODE_ER006 = "ER006"; // Lỗi: Vượt quá độ dài ký tự tối đa
    public static final String CODE_ER007 = "ER007"; // Lỗi: Sai khoảng độ dài quy định (Min/Max)
    public static final String CODE_ER008 = "ER008"; // Lỗi: Phải là ký tự số nửa chiều
    public static final String CODE_ER009 = "ER009"; // Lỗi: Phải là ký tự Katakana nửa chiều
    public static final String CODE_ER011 = "ER011"; // Lỗi: Định dạng ngày tháng không hợp lệ
    public static final String CODE_ER012 = "ER012"; // Lỗi: Ngày bắt đầu không được sau ngày kết thúc
    public static final String CODE_ER013 = "ER013"; // Lỗi: Không tìm thấy thực thể yêu cầu
    public static final String CODE_ER015 = "ER015"; // Lỗi: Lỗi hệ thống chung (General System Error)
    public static final String CODE_ER017 = "ER017"; // Lỗi: Mật khẩu xác nhận không khớp
    public static final String CODE_ER018 = "ER018"; // Lỗi: Yêu cầu nhập số nửa chiều (cho điểm số)
    public static final String CODE_ER019 = "ER019"; // Lỗi: Sai định dạng Tên đăng nhập (chỉ chữ, số, _)
    public static final String CODE_ER021 = "ER021"; // Lỗi: Tham số sắp xếp không hợp lệ
    public static final String CODE_ER022 = "ER022"; // Lỗi: Không tìm thấy trang (404)
    public static final String CODE_ERROR_SYSTEM = "500"; // Mã lỗi hệ thống mặc định (Internal Server Error)

    // Các hằng số định nghĩa giới hạn về độ dài dữ liệu (Validation constraints)
    public static final int MAX_LENGTH_LOGIN_ID = 50; // Độ dài tối đa của Tên đăng nhập
    public static final int MAX_LENGTH_NAME = 125; // Độ dài tối đa của Tên nhân viên
    public static final int MAX_LENGTH_NAME_KANA = 125; // Độ dài tối đa của Tên Katakana
    public static final int MAX_LENGTH_EMAIL = 125; // Độ dài tối đa của Email
    public static final int MAX_LENGTH_TELEPHONE = 50; // Độ dài tối đa của Số điện thoại
    public static final int MIN_LENGTH_PASSWORD = 8; // Độ dài tối thiểu của Mật khẩu
    public static final int MAX_LENGTH_PASSWORD = 50; // Độ dài tối đa của Mật khẩu
}
