package com.luvina.la.config;

public class Constants {

    private Constants() {
    }

    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final boolean IS_CROSS_ALLOW = true;

    public static final String JWT_SECRET = "Luvina-Academe";
    public static final long JWT_EXPIRATION = 160 * 60 * 60; // 7 day

    // config endpoints public
    public static final String[] ENDPOINTS_PUBLIC = new String[] {
            "/",
            "/login/**",
            "/error/**"
    };

    // config endpoints for USER role
    public static final String[] ENDPOINTS_WITH_ROLE = new String[] {
            "/user/**"
    };

    // user attributies put to token
    public static final String[] ATTRIBUTIES_TO_TOKEN = new String[] {
            "employeeId",
            "departmentId",
            "employeeName",
            "employeeNameKana",
            "employeeBirthDate",
            "employeeEmail",
            "employeeTelephone",
            "employeeLoginId",
            "employeeEmail"
    };
    // Regex Patterns
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final String LOGIN_ID_PATTERN = "^[a-zA-Z_][a-zA-Z0-9_]*$";
    public static final String HALFSIDE_KATAKANA_PATTERN = "^[\\uFF61-\\uFF9F]+$";
    public static final String HALFSIDE_NUMBER_PATTERN = "^[0-9]*$";

    // Message Keys (Labels)
    public static final String LABEL_LOGIN_ID = "label.employeeLoginId";
    public static final String LABEL_DEPARTMENT_NAME = "label.departmentName";
    public static final String LABEL_EMPLOYEE_NAME = "label.employeeName";
    public static final String LABEL_EMPLOYEE_NAME_KANA = "label.employeeNameKana";
    public static final String LABEL_BIRTH_DATE = "label.employeeBirthDate";
    public static final String LABEL_EMAIL = "label.employeeEmail";
    public static final String LABEL_TELEPHONE = "label.employeeTelephone";
    public static final String LABEL_PASSWORD = "label.employeeLoginPassword";
    public static final String LABEL_CERT_NAME = "label.certificationName";
    public static final String LABEL_CERT_START_DATE = "label.certificationStartDate";
    public static final String LABEL_CERT_END_DATE = "label.certificationEndDate";
    public static final String LABEL_CERT_SCORE = "label.employeeCertificationScore";

    // Default Labels (Japanese)
    public static final String DEFAULT_LABEL_LOGIN_ID = "アカウント名";
    public static final String DEFAULT_LABEL_DEPARTMENT = "グループ";
    public static final String DEFAULT_LABEL_NAME = "氏名";
    public static final String DEFAULT_LABEL_NAME_KANA = "カタカナ氏名";
    public static final String DEFAULT_LABEL_BIRTH_DATE = "生年月日";
    public static final String DEFAULT_LABEL_EMAIL = "メールアドレス";
    public static final String DEFAULT_LABEL_TELEPHONE = "電話番号";
    public static final String DEFAULT_LABEL_PASSWORD = "パスワード";
    public static final String DEFAULT_LABEL_CERT_NAME = "資格";
    public static final String DEFAULT_LABEL_CERT_START_DATE = "資格交付日";
    public static final String DEFAULT_LABEL_CERT_END_DATE = "失効日";
    public static final String DEFAULT_LABEL_CERT_SCORE = "点数";

    // Field Names
    public static final String FIELD_LOGIN_ID = "employeeLoginId";
    public static final String FIELD_DEPARTMENT_ID = "departmentId";
    public static final String FIELD_NAME = "employeeName";
    public static final String FIELD_NAME_KANA = "employeeNameKana";
    public static final String FIELD_BIRTH_DATE = "employeeBirthDate";
    public static final String FIELD_EMAIL = "employeeEmail";
    public static final String FIELD_TELEPHONE = "employeeTelephone";
    public static final String FIELD_PASSWORD = "employeeLoginPassword";
    public static final String FIELD_PASSWORD_CONFIRM = "employeeLoginPasswordConfirm";
    public static final String FIELD_CERT_ID = "certificationId";
    public static final String FIELD_CERT_START_DATE = "certificationStartDate";
    public static final String FIELD_CERT_END_DATE = "certificationEndDate";
    public static final String FIELD_CERT_SCORE = "employeeCertificationScore";

    // Common Codes
    public static final String CODE_SUCCESS = "200";
    public static final String CODE_ERROR_SYSTEM = "ER999";
    public static final String CODE_ERROR_PAGE_NOT_FOUND = "ER022";
    public static final String CODE_ERROR_SORT_ORDER = "ER021";
    public static final String CODE_ERROR_NUMERIC = "ER018";
}


