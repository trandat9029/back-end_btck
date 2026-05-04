/**
 * Copyright(C) 2026 Luvina
 * [AppConstants.java], 09/04/2026 tranledat
 */
package com.luvina.la.constants;

import java.util.regex.Pattern;

/**
 * Lớp định nghĩa các hằng số dùng chung trong ứng dụng.
 *
 * @author tranledat
 */
public class AppConstants {
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MIN_LENGTH_8 = 8;
    public static final int MAX_LENGTH_50 = 50;
    public static final int MAX_LENGTH_125 = 125;
    public static final String DATE_FORMAT = "yyyy/MM/dd";

    public static final Pattern HALF_WIDTH_KATAKANA_PATTERN = Pattern.compile("^[\\uFF66-\\uFF9F]+$");

    // Labels
    public static final String LABEL_LOGIN_ID = "label.employee_login_id";
    public static final String LABEL_DEPARTMENT = "label.department";
    public static final String LABEL_NAME = "label.employee_name";
    public static final String LABEL_NAME_KANA = "label.employee_name_kana";
    public static final String LABEL_BIRTH_DATE = "label.employee_birth_date";
    public static final String LABEL_EMAIL = "label.employee_email";
    public static final String LABEL_TELEPHONE = "label.employee_telephone";
    public static final String LABEL_PASSWORD = "label.employee_password";
    public static final String LABEL_CERT_NAME = "label.certification_name";
    public static final String LABEL_CERT_START_DATE = "label.certification_start_date";
    public static final String LABEL_CERT_END_DATE = "label.certification_end_date";
    public static final String LABEL_CERT_SCORE = "label.certification_score";
    public static final String LABEL_ID = "label.id";

    // Regex & Patterns
    public static final String REGEX_LOGIN_ID = "^[a-zA-Z_][a-zA-Z0-9_]*$";
    public static final String EMAIL_AT = "@";
    public static final String EMAIL_DOT = ".";
    public static final String EMAIL_AT_DOT = "@.";
    public static final String EMAIL_DOT_AT = ".@";

    // Validation Modes
    public static final String MODE = "MODE";
    public static final String MODE_SUBMIT = "SUBMIT";   // Chặng ADM004 -> ADM005
    public static final String MODE_CONFIRM = "CONFIRM"; // Chặng ADM005 -> Lưu DB

    // System & Security Configs
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final boolean IS_CROSS_ALLOW = true;

    public static final String JWT_SECRET = "Luvina-Academe";
    public static final long JWT_EXPIRATION = 160 * 60 * 60; // 7 day

    public static final String[] ENDPOINTS_PUBLIC = new String[] {
            "/",
            "/login/**",
            "/error/**"
    };

    public static final String[] ENDPOINTS_WITH_ROLE = new String[] {
            "/user/**"
    };

    public static final String[] ATTRIBUTIES_TO_TOKEN = new String[] {
            "employeeId",
            "employeeName",
            "employeeLoginId",
            "employeeEmail"
    };
}
