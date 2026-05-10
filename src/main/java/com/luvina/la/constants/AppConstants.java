/**
 * Copyright(C) 2026 Luvina Software
 * AppConstants.java, 09/04/2026 tranledat
 */
package com.luvina.la.constants;

/**
 * Định nghĩa các hằng số dùng chung cho toàn bộ ứng dụng.
 * Bao gồm các cấu hình về phân trang, bảo mật, JWT và định dạng dữ liệu.
 * 
 * @author tranledat
 */
public final class AppConstants {

    /**
     * Chặn việc khởi tạo đối tượng vì đây là lớp hằng số.
     */
    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }

    // --- Cấu hình chung ---
    /** Kích thước trang mặc định cho phân trang */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Độ dài tối thiểu của mật khẩu */
    public static final int MIN_LENGTH_8 = 8;

    /** Độ dài tối đa cho Login ID, Telephone */
    public static final int MAX_LENGTH_50 = 50;

    /** Độ dài tối đa cho Tên, Email */
    public static final int MAX_LENGTH_125 = 125;
    
    /** Độ dài tối đa cho điểm số */
    public static final int MAX_LENGTH_3 = 3;

    /** Định dạng ngày hiển thị và lưu trữ mặc định */
    public static final String DATE_FORMAT = "yyyy/MM/dd";

    // --- Cấu hình Spring Profile ---
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    // --- Cấu hình Bảo mật & CORS ---
    public static final boolean IS_CROSS_ALLOW = true;

    // --- Cấu hình JWT ---
    public static final String JWT_SECRET = "Luvina-Academe";
    public static final long JWT_EXPIRATION = 160 * 60 * 60; // 7 days

    // --- Danh sách Endpoint công khai ---
    public static final String[] ENDPOINTS_PUBLIC = new String[] {
            "/",
            "/login/**",
            "/error/**",
            "/employee/validate/**" // Cho phép validate không cần login nếu cần thiết
    };

    // --- Danh sách Endpoint yêu cầu quyền USER ---
    public static final String[] ENDPOINTS_WITH_ROLE = new String[] {
            "/user/**",
            "/employee/**"
    };

    // --- Các thuộc tính nhân viên được đưa vào Token ---
    public static final String[] ATTRIBUTIES_TO_TOKEN = new String[] {
            "employeeId",
            "employeeName",
            "employeeLoginId",
            "employeeEmail"
    };

    // --- Các hành động (Actions) cho Validate/Save ---
    public static final String ACTION_ADD = "add";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION = "action";

    // --- Quyền vai trò (Role) ---
    /** Quyền người dùng thông thường (employee_role = 1) */
    public static final int ROLE_USER = 1;

    /** Quyền quản trị viên (employee_role = 0) */
    public static final int ROLE_ADMIN = 0;

    // --- Key nhãn (Labels) cho thông báo lỗi ---
    public static final String LABEL_ID = "label.id";
    public static final String LABEL_LOGIN_ID = "label.employee_login_id";
    public static final String LABEL_NAME = "label.employee_name";
    public static final String LABEL_NAME_KANA = "label.employee_name_kana";
    public static final String LABEL_BIRTH_DATE = "label.employee_birth_date";
    public static final String LABEL_EMAIL = "label.employee_email";
    public static final String LABEL_TELEPHONE = "label.employee_telephone";
    public static final String LABEL_PASSWORD = "label.employee_password";
    public static final String LABEL_DEPARTMENT = "label.department";
    public static final String LABEL_CERT_NAME = "label.certification_name";
    public static final String LABEL_CERT_START_DATE = "label.certification_start_date";
    public static final String LABEL_CERT_END_DATE = "label.certification_end_date";
    public static final String LABEL_CERT_SCORE = "label.certification_score";

    // --- Regex & Patterns ---
    /** Regex validate Login ID: Bắt đầu bằng chữ hoặc gạch dưới, theo sau là chữ, số hoặc gạch dưới */
    public static final String REGEX_LOGIN_ID = "^[a-zA-Z_][a-zA-Z0-9_]*$";

    /** Pattern validate Katakana Half-width (Unicode range: FF66-FF9F) */
    public static final java.util.regex.Pattern HALF_WIDTH_KATAKANA_PATTERN = java.util.regex.Pattern.compile("^[\\uFF66-\\uFF9F]*$");

    // --- Email Tokens ---
    public static final String EMAIL_AT = "@";
    public static final String EMAIL_DOT = ".";
    public static final String EMAIL_AT_DOT = "@.";
    public static final String EMAIL_DOT_AT = ".@";
}
